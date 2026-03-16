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
    val color: String = "#E53935",
    // Basic Info
    val characterName: String = "",
    val race: String = "",
    val subrace: String = "",
    val characterClass: String = "",
    val subclass: String = "",
    val background: String = "",
    val alignment: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    // Ability Scores
    val strScore: Int = 10,
    val dexScore: Int = 10,
    val conScore: Int = 10,
    val intScore: Int = 10,
    val wisScore: Int = 10,
    val chaScore: Int = 10,
    // Equipment
    val startingArmor: String = "",
    val startingWeapon: String = "",
    val startingGear: List<String> = emptyList()
)
