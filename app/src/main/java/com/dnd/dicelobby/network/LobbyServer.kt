package com.dnd.dicelobby.network

import com.dnd.dicelobby.dice.DiceFormula
import com.dnd.dicelobby.dice.DiceRoller
import com.dnd.dicelobby.models.Player
import com.dnd.dicelobby.models.Roll
import com.dnd.dicelobby.network.messages.NetworkMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val DEFAULT_PORT = 9876
const val MAX_PLAYERS  = 8

/**
 * TCP server running on the DM device.
 *
 * Responsibilities:
 *  • Accept up to [MAX_PLAYERS] client connections.
 *  • Receive [NetworkMessage.RollRequest] from clients and evaluate rolls authoritatively.
 *  • Broadcast [NetworkMessage.RollResult] / [NetworkMessage.HiddenRollNotice] to all peers.
 *  • Track connected players and broadcast join/leave events.
 *  • Manage the DM hidden-roll system: only the DM sees the hidden result until revealed.
 *
 * Threading model: each client is served by a separate coroutine.
 * The scope is created externally (passed in) so the ViewModel controls the lifetime.
 */
class LobbyServer(private val scope: CoroutineScope) {

    private val _players      = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _rolls        = MutableStateFlow<List<Roll>>(emptyList())
    val rolls: StateFlow<List<Roll>> = _rolls.asStateFlow()

    private val _events       = MutableSharedFlow<ServerEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<ServerEvent> = _events.asSharedFlow()

    /** Map of playerId → client writer (thread-safe). */
    private val clientWriters = ConcurrentHashMap<String, PrintWriter>()

    /** Hidden rolls only visible to the DM until revealed. */
    private val hiddenRolls   = ConcurrentHashMap<String, Roll>()

    private var serverSocket: ServerSocket? = null
    private var dmPlayerId: String = ""

    /**
     * Start listening on [port]. The DM player is registered immediately so the host
     * can roll without being a "client" of themselves.
     */
    fun start(dmPlayer: Player, port: Int = DEFAULT_PORT) {
        dmPlayerId = dmPlayer.id
        addPlayer(dmPlayer)

        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(port).also { ss ->
                    ss.reuseAddress = true
                    while (isActive) {
                        val socket = ss.accept()
                        launch { handleClient(socket) }
                    }
                }
            } catch (e: Exception) {
                if (isActive) _events.tryEmit(ServerEvent.Error("Server error: ${e.message}"))
            }
        }
    }

    fun stop() {
        serverSocket?.close()
        clientWriters.values.forEach { it.close() }
        clientWriters.clear()
    }

    // ── Roll authority ───────────────────────────────────────────────────────

    /**
     * Called by the DM's ViewModel directly (no network round-trip needed for host).
     * Also called internally when a client's [RollRequest] arrives.
     */
    fun processRoll(playerId: String, formulaStr: String, hidden: Boolean = false) {
        val player = _players.value.find { it.id == playerId } ?: return
        val formula = try { DiceFormula.parse(formulaStr) } catch (e: Exception) {
            _events.tryEmit(ServerEvent.Error("Invalid formula: $formulaStr"))
            return
        }
        val result  = DiceRoller.roll(formula)
        val roll    = Roll(
            id          = UUID.randomUUID().toString(),
            playerId    = playerId,
            playerName  = player.name,
            diceFormula = formula.text,
            diceResults = result.rolls,
            modifier    = result.modifier,
            total       = result.total,
            isHidden    = hidden && player.isDM,
            timestamp   = System.currentTimeMillis()
        )

        if (roll.isHidden) {
            // Store the real roll server-side; broadcast only the notice to non-DMs
            hiddenRolls[roll.id] = roll
            addRoll(roll)                               // DM's own roll list (full)
            broadcastExcept(
                msg       = NetworkMessage.HiddenRollNotice(roll.id, roll.timestamp),
                excludeId = dmPlayerId
            )
            _events.tryEmit(ServerEvent.HiddenRoll(roll))
        } else {
            addRoll(roll)
            broadcast(NetworkMessage.RollResult(roll))
            _events.tryEmit(ServerEvent.NewRoll(roll))
        }
    }

    /**
     * DM reveals a previously hidden roll. The full [Roll] is broadcast to all clients.
     */
    fun revealHiddenRoll(rollId: String) {
        val roll = hiddenRolls.remove(rollId) ?: return
        val revealed = roll.copy(isHidden = false)

        // Update server roll list
        _rolls.value = _rolls.value.map { if (it.id == rollId) revealed else it }

        broadcast(NetworkMessage.RevealRoll(revealed))
        _events.tryEmit(ServerEvent.RollRevealed(revealed))
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun handleClient(socket: Socket) {
        var playerId = ""
        val writer   = PrintWriter(socket.getOutputStream(), true)
        val reader   = BufferedReader(InputStreamReader(socket.getInputStream()))
        try {
            for (line in reader.lines()) {
                val msg = NetworkMessage.deserialize(line) ?: continue
                when (msg) {
                    is NetworkMessage.JoinRequest -> {
                        if (_players.value.size >= MAX_PLAYERS) {
                            writer.println(NetworkMessage.serialize(
                                NetworkMessage.Error("Lobby is full (max $MAX_PLAYERS)")
                            ))
                            break
                        }
                        playerId = msg.player.id
                        clientWriters[playerId] = writer
                        addPlayer(msg.player)

                        // Send lobby snapshot to the newcomer
                        writer.println(NetworkMessage.serialize(
                            NetworkMessage.JoinAck(
                                players     = _players.value,
                                recentRolls = _rolls.value.takeLast(50)
                            )
                        ))
                        // Notify everyone else
                        broadcastExcept(NetworkMessage.PlayerConnected(msg.player), playerId)
                        _events.tryEmit(ServerEvent.PlayerJoined(msg.player))
                    }

                    is NetworkMessage.RollRequest -> {
                        processRoll(msg.playerId, msg.formula, msg.hidden)
                    }

                    is NetworkMessage.LeaveRequest -> break

                    else -> { /* unknown client message — ignore */ }
                }
            }
        } catch (e: Exception) {
            // Socket closed or read error — treat as disconnect
        } finally {
            socket.close()
            if (playerId.isNotEmpty()) {
                clientWriters.remove(playerId)
                removePlayer(playerId)
                broadcast(NetworkMessage.PlayerDisconnected(playerId))
                _events.tryEmit(ServerEvent.PlayerLeft(playerId))
            }
        }
    }

    private fun addPlayer(player: Player) {
        _players.value = (_players.value + player).distinctBy { it.id }
    }

    private fun removePlayer(playerId: String) {
        _players.value = _players.value.filter { it.id != playerId }
    }

    private fun addRoll(roll: Roll) {
        _rolls.value = _rolls.value + roll
    }

    private fun broadcast(msg: NetworkMessage) {
        val json = NetworkMessage.serialize(msg)
        clientWriters.values.forEach { it.println(json) }
    }

    private fun broadcastExcept(msg: NetworkMessage, excludeId: String) {
        val json = NetworkMessage.serialize(msg)
        clientWriters.forEach { (id, writer) ->
            if (id != excludeId) writer.println(json)
        }
    }
}

/** Events emitted by the server to the ViewModel for UI updates. */
sealed class ServerEvent {
    data class PlayerJoined(val player: Player) : ServerEvent()
    data class PlayerLeft(val playerId: String)  : ServerEvent()
    data class NewRoll(val roll: Roll)           : ServerEvent()
    data class HiddenRoll(val roll: Roll)        : ServerEvent()
    data class RollRevealed(val roll: Roll)      : ServerEvent()
    data class Error(val message: String)        : ServerEvent()
}
