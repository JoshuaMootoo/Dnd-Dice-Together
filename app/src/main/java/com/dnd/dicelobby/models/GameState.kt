package com.dnd.dicelobby.models

/**
 * Snapshot of the entire lobby state kept in memory by both host and clients.
 */
data class GameState(
    val lobbyName: String = "",
    val players: List<Player> = emptyList(),
    val rolls: List<Roll> = emptyList(),
    val isStarted: Boolean = false
)
