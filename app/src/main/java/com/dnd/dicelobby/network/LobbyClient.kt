package com.dnd.dicelobby.network

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
import java.net.Socket

/**
 * TCP client running on non-DM player devices.
 *
 * Connects to the host's [LobbyServer] and:
 *   • Sends [NetworkMessage.JoinRequest] on connect.
 *   • Sends [NetworkMessage.RollRequest] when the player rolls dice.
 *   • Receives lobby state and all roll broadcasts from the host.
 *   • Handles reconnection with exponential back-off.
 *
 * All state updates from the server are surfaced through [StateFlow]s consumed by the
 * ViewModel so the UI remains decoupled from socket logic.
 */
class LobbyClient(private val scope: CoroutineScope) {

    private val _players       = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _rolls         = MutableStateFlow<List<Roll>>(emptyList())
    val rolls: StateFlow<List<Roll>> = _rolls.asStateFlow()

    private val _connected     = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _events        = MutableSharedFlow<ClientEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<ClientEvent> = _events.asSharedFlow()

    private var writer: PrintWriter? = null
    private var socket: Socket? = null
    private var localPlayer: Player? = null

    /** Connect to [host]:[port] and identify as [player]. */
    fun connect(host: String, port: Int = DEFAULT_PORT, player: Player) {
        localPlayer = player
        scope.launch(Dispatchers.IO) {
            var delay = 2_000L
            repeat(5) { attempt ->
                try {
                    val s = Socket(host, port)
                    socket = s
                    writer = PrintWriter(s.getOutputStream(), true)
                    _connected.value = true

                    // Introduce ourselves
                    send(NetworkMessage.JoinRequest(player))

                    // Read loop
                    val reader = BufferedReader(InputStreamReader(s.getInputStream()))
                    for (line in reader.lines()) {
                        handleMessage(NetworkMessage.deserialize(line) ?: continue)
                    }

                    // Socket closed cleanly — stop retrying
                    _connected.value = false
                    return@launch
                } catch (e: Exception) {
                    _connected.value = false
                    if (attempt < 4) {
                        _events.emit(ClientEvent.Error("Connection failed, retrying… (${e.message})"))
                        delay(delay)
                        delay = (delay * 2).coerceAtMost(16_000L)
                    } else {
                        _events.emit(ClientEvent.Error("Could not connect to host"))
                    }
                }
            }
        }
    }

    fun disconnect() {
        localPlayer?.id?.let { send(NetworkMessage.LeaveRequest(it)) }
        socket?.close()
        writer = null
        _connected.value = false
    }

    /** Request a dice roll from the host. Only the host computes the outcome. */
    fun requestRoll(formula: String, hidden: Boolean = false, faceValues: List<Int> = emptyList()) {
        val pid = localPlayer?.id ?: return
        send(NetworkMessage.RollRequest(
            playerId = pid,
            formula  = formula,
            hidden   = hidden,
            results  = faceValues.ifEmpty { null }
        ))
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun send(msg: NetworkMessage) {
        writer?.println(NetworkMessage.serialize(msg))
    }

    private fun handleMessage(msg: NetworkMessage) {
        when (msg) {
            is NetworkMessage.JoinAck -> {
                _players.value = msg.players
                _rolls.value   = msg.recentRolls
                _events.tryEmit(ClientEvent.LobbySync(msg.players, msg.recentRolls))
            }

            is NetworkMessage.PlayerConnected -> {
                _players.value = (_players.value + msg.player).distinctBy { it.id }
                _events.tryEmit(ClientEvent.PlayerJoined(msg.player))
            }

            is NetworkMessage.PlayerDisconnected -> {
                _players.value = _players.value.filter { it.id != msg.playerId }
                _events.tryEmit(ClientEvent.PlayerLeft(msg.playerId))
            }

            is NetworkMessage.LobbyState -> {
                _players.value = msg.players
                _rolls.value   = msg.rolls
            }

            is NetworkMessage.RollResult -> {
                _rolls.value = _rolls.value + msg.roll
                _events.tryEmit(ClientEvent.NewRoll(msg.roll))
            }

            is NetworkMessage.HiddenRollNotice -> {
                // Create a placeholder roll so the log shows "DM rolled (hidden)"
                val placeholder = Roll(
                    id          = msg.rollId,
                    playerId    = "",
                    playerName  = "DM",
                    diceFormula = "???",
                    diceResults = emptyList(),
                    total       = 0,
                    isHidden    = true,
                    timestamp   = msg.timestamp
                )
                _rolls.value = _rolls.value + placeholder
                _events.tryEmit(ClientEvent.HiddenRoll(placeholder))
            }

            is NetworkMessage.RevealRoll -> {
                // Replace the placeholder with the real roll
                _rolls.value = _rolls.value.map { r ->
                    if (r.id == msg.roll.id) msg.roll else r
                }
                _events.tryEmit(ClientEvent.RollRevealed(msg.roll))
            }

            is NetworkMessage.Error -> {
                _events.tryEmit(ClientEvent.Error(msg.message))
            }

            else -> { /* server-side messages we don't handle as a client */ }
        }
    }
}

/** UI-level events surfaced from the client socket to the ViewModel. */
sealed class ClientEvent {
    data class LobbySync(val players: List<Player>, val rolls: List<Roll>) : ClientEvent()
    data class PlayerJoined(val player: Player)  : ClientEvent()
    data class PlayerLeft(val playerId: String)  : ClientEvent()
    data class NewRoll(val roll: Roll)           : ClientEvent()
    data class HiddenRoll(val roll: Roll)        : ClientEvent()
    data class RollRevealed(val roll: Roll)      : ClientEvent()
    data class Error(val message: String)        : ClientEvent()
}
