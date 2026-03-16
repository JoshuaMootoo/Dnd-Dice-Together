package com.dnd.dicelobby.network.messages

import com.dnd.dicelobby.models.Player
import com.dnd.dicelobby.models.Roll
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Sealed hierarchy of all messages exchanged between host and clients over TCP.
 *
 * Every message is serialized as a JSON object with a "type" discriminator so
 * a single receiver loop can dispatch to the correct handler.
 *
 * Message flow overview
 * ─────────────────────
 * Client → Host
 *   JoinRequest      – first message sent upon connection; carries player info
 *   RollRequest      – client asks host to roll dice on their behalf
 *   LeaveRequest     – graceful disconnect
 *
 * Host → Client(s)
 *   JoinAck          – confirms join, sends back full lobby state
 *   PlayerConnected  – broadcasts new player to existing clients
 *   PlayerDisconnected – broadcasts departure
 *   LobbyState       – full resync (e.g. after reconnect)
 *   RollResult       – authoritative roll outcome, always from host
 *   HiddenRollNotice – tells all clients "DM rolled (hidden)" without the value
 *   RevealRoll       – DM broadcasts a previously hidden roll with its result
 *   Error            – generic error feedback
 */
@Serializable
sealed class NetworkMessage {

    // ── Client → Host ────────────────────────────────────────────────────────

    @Serializable
    @SerialName("JoinRequest")
    data class JoinRequest(
        val player: Player
    ) : NetworkMessage()

    @Serializable
    @SerialName("RollRequest")
    data class RollRequest(
        val playerId: String,
        val formula: String,
        /** DM-only: if true the result is initially hidden from other players. */
        val hidden: Boolean = false
    ) : NetworkMessage()

    @Serializable
    @SerialName("LeaveRequest")
    data class LeaveRequest(val playerId: String) : NetworkMessage()

    // ── Host → Client(s) ────────────────────────────────────────────────────

    @Serializable
    @SerialName("JoinAck")
    data class JoinAck(
        val players: List<Player>,
        val recentRolls: List<Roll>
    ) : NetworkMessage()

    @Serializable
    @SerialName("PlayerConnected")
    data class PlayerConnected(val player: Player) : NetworkMessage()

    @Serializable
    @SerialName("PlayerDisconnected")
    data class PlayerDisconnected(val playerId: String) : NetworkMessage()

    @Serializable
    @SerialName("LobbyState")
    data class LobbyState(
        val players: List<Player>,
        val rolls: List<Roll>
    ) : NetworkMessage()

    @Serializable
    @SerialName("RollResult")
    data class RollResult(val roll: Roll) : NetworkMessage()

    /** Sent to all non-DM clients so they see "DM rolled (hidden)". */
    @Serializable
    @SerialName("HiddenRollNotice")
    data class HiddenRollNotice(
        val rollId: String,
        val timestamp: Long
    ) : NetworkMessage()

    /** Broadcast when DM reveals a hidden roll. */
    @Serializable
    @SerialName("RevealRoll")
    data class RevealRoll(val roll: Roll) : NetworkMessage()

    @Serializable
    @SerialName("Error")
    data class Error(val message: String) : NetworkMessage()

    companion object {
        private val json = Json { ignoreUnknownKeys = true; classDiscriminator = "type" }

        fun serialize(msg: NetworkMessage): String = json.encodeToString(serializer(), msg)

        fun deserialize(raw: String): NetworkMessage? = try {
            json.decodeFromString(serializer(), raw)
        } catch (e: Exception) {
            null
        }
    }
}
