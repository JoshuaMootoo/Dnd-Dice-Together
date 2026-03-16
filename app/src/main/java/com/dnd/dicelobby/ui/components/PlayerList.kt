package com.dnd.dicelobby.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.models.Player

/**
 * Horizontal scrollable strip of player avatar chips.
 * Highlights the DM with a shield icon and shows disconnected state with grey colouring.
 */
@Composable
fun PlayerList(
    players: List<Player>,
    localPlayerId: String,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(players, key = { it.id }) { player ->
            PlayerChip(player = player, isLocal = player.id == localPlayerId)
        }
    }
}

@Composable
private fun PlayerChip(player: Player, isLocal: Boolean) {
    val chipColor = try {
        Color(android.graphics.Color.parseColor(player.color))
    } catch (e: Exception) {
        Color(0xFFE53935)
    }

    val containerColor = if (player.isConnected) chipColor.copy(alpha = 0.15f)
                         else Color.Gray.copy(alpha = 0.10f)

    val borderColor = when {
        isLocal           -> chipColor
        player.isConnected -> chipColor.copy(alpha = 0.5f)
        else              -> Color.Gray.copy(alpha = 0.3f)
    }

    Surface(
        shape  = RoundedCornerShape(24.dp),
        color  = containerColor,
        modifier = Modifier
            .height(56.dp)
            .widthIn(min = 72.dp)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Colour dot / avatar circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(chipColor.copy(alpha = if (player.isConnected) 1f else 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (player.isDM) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "DM",
                        tint     = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text  = player.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            Column {
                Text(
                    text  = player.name,
                    color = if (player.isConnected) MaterialTheme.colorScheme.onSurface
                            else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (player.isDM) {
                    Text(text = "DM", fontSize = 10.sp, color = chipColor)
                }
            }

            if (!player.isConnected) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.WifiOff, contentDescription = "Disconnected",
                     tint = Color.Gray, modifier = Modifier.size(12.dp))
            }
        }
    }
}
