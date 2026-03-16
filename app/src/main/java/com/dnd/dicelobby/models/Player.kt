package com.dnd.dicelobby.models

import kotlinx.serialization.Serializable

/**
 * Represents a player connected to the lobby.
 *
 * @param id        Unique identifier (UUID string) assigned when the player first connects.
 * @param name      Display name chosen by the user.
 * @param isDM      True when this player is the Dungeon Master / lobby host.
 * @param isConnected Live connection state (updated by the server on disconnect / reconnect).
 * @param color     Hex colour string (e.g. "#E53935") used to tint that player's dice.
 */
@Serializable
data class Player(
    val id: String,
    val name: String,
    val isDM: Boolean = false,
    val isConnected: Boolean = true,
    val color: String = "#E53935"
)
