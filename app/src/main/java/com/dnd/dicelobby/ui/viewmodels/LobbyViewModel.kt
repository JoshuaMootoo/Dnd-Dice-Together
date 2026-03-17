package com.dnd.dicelobby.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dnd.dicelobby.DndApplication
import com.dnd.dicelobby.models.Player
import com.dnd.dicelobby.models.Roll
import com.dnd.dicelobby.network.ClientEvent
import com.dnd.dicelobby.network.LobbyClient
import com.dnd.dicelobby.network.LobbyServer
import com.dnd.dicelobby.network.NetworkManager
import com.dnd.dicelobby.network.ServerEvent
import com.dnd.dicelobby.repository.RollRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Manages the live lobby state for both host (DM) and client (player) devices.
 *
 * Mode selection:
 *   • Call [startAsHost] on the DM device → creates a [LobbyServer].
 *   • Call [joinAsClient] on a player device → creates a [LobbyClient].
 *
 * The ViewModel is the single source of truth for:
 *   • [players]  — all connected players
 *   • [rolls]    — visible roll history (respects hidden-roll rules)
 *   • [messages] — ephemeral toast/snackbar strings
 */
class LobbyViewModel(application: Application) : AndroidViewModel(application) {

    private val rollRepository = RollRepository(
        (application as DndApplication).database.rollDao()
    )

    // Live rolls from Room DB (full persistence)
    val persistedRolls = rollRepository.allRolls
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _players     = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _rolls        = MutableStateFlow<List<Roll>>(emptyList())
    val rolls: StateFlow<List<Roll>> = _rolls.asStateFlow()

    private val _message      = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _localPlayer  = MutableStateFlow<Player?>(null)
    val localPlayer: StateFlow<Player?> = _localPlayer.asStateFlow()

    private val _isHost       = MutableStateFlow(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()

    private val _hostIp       = MutableStateFlow("")
    val hostIp: StateFlow<String> = _hostIp.asStateFlow()

    private val _connected    = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private var server: LobbyServer? = null
    private var client: LobbyClient? = null

    // ── Host setup ───────────────────────────────────────────────────────────

    fun startAsHost(player: Player) {
        val dmPlayer = player.copy(isDM = true)
        _localPlayer.value = dmPlayer
        _isHost.value      = true
        _connected.value   = true
        _players.value     = listOf(dmPlayer)

        val srv = LobbyServer(viewModelScope).also { server = it }
        srv.start(dmPlayer)

        // Sync server state into ViewModel
        viewModelScope.launch { srv.players.collect { _players.value = it } }
        viewModelScope.launch { srv.rolls.collect { updateRolls(it) } }
        viewModelScope.launch {
            srv.events.collect { event ->
                when (event) {
                    is ServerEvent.PlayerJoined  -> showMessage("${event.player.name} joined the lobby")
                    is ServerEvent.PlayerLeft    -> showMessage("A player left the lobby")
                    is ServerEvent.NewRoll       -> persistRoll(event.roll)
                    is ServerEvent.HiddenRoll    -> persistRoll(event.roll)
                    is ServerEvent.RollRevealed  -> rollRepository.revealRoll(event.roll.id)
                    is ServerEvent.Error         -> showMessage(event.message)
                }
            }
        }

        _hostIp.value = NetworkManager.getLocalIpAddress(getApplication())
    }

    // ── Client setup ─────────────────────────────────────────────────────────

    fun joinAsClient(player: Player, hostIp: String) {
        _localPlayer.value = player
        _isHost.value      = false

        val cli = LobbyClient(viewModelScope).also { client = it }
        cli.connect(hostIp, player = player)

        viewModelScope.launch { cli.connected.collect { _connected.value = it } }
        viewModelScope.launch { cli.players.collect { _players.value = it } }
        viewModelScope.launch { cli.rolls.collect { updateRolls(it) } }
        viewModelScope.launch {
            cli.events.collect { event ->
                when (event) {
                    is ClientEvent.PlayerJoined -> showMessage("${event.player.name} joined")
                    is ClientEvent.PlayerLeft   -> showMessage("A player left")
                    is ClientEvent.NewRoll      -> persistRoll(event.roll)
                    is ClientEvent.HiddenRoll   -> { /* placeholder already in roll list */ }
                    is ClientEvent.RollRevealed -> {
                        rollRepository.revealRoll(event.roll.id)
                        showMessage("DM revealed: ${event.roll.diceFormula} → ${event.roll.total}")
                    }
                    is ClientEvent.Error        -> showMessage(event.message)
                    else -> {}
                }
            }
        }
    }

    // ── Roll requests ────────────────────────────────────────────────────────

    /**
     * Request a dice roll.
     * On the host device, [LobbyServer.processRoll] is called directly.
     * On client devices, a [RollRequest] message is sent to the host.
     */
    fun requestRoll(formula: String, hidden: Boolean = false) {
        val pid = _localPlayer.value?.id ?: return
        if (_isHost.value) {
            server?.processRoll(pid, formula, hidden)
        } else {
            client?.requestRoll(formula, hidden)
        }
    }

    /** DM-only: reveal a previously hidden roll to all players. */
    fun revealHiddenRoll(rollId: String) {
        if (_isHost.value) server?.revealHiddenRoll(rollId)
    }

    /** Clear local roll history (Room + in-memory). */
    fun clearHistory() {
        viewModelScope.launch {
            rollRepository.clearHistory()
            _rolls.value = emptyList()
        }
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    fun leaveLobby() {
        client?.disconnect()
        server?.stop()
        _connected.value = false
    }

    override fun onCleared() {
        super.onCleared()
        leaveLobby()
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun updateRolls(newList: List<Roll>) {
        _rolls.value = newList
    }

    private fun persistRoll(roll: Roll) {
        viewModelScope.launch { rollRepository.insert(roll) }
    }

    private fun showMessage(msg: String) {
        _message.value = msg
    }

    fun clearMessage() { _message.value = "" }
}
