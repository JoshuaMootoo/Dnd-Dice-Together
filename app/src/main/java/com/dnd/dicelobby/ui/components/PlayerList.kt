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
import androidx.compose.material.icons.filled.SignalWifiOff
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
import com.dnd.dicelobby.ui.theme.*

/**
 * Horizontal scrollable strip of player portrait chips.
 * Styled after Baldur's Gate 3's party portrait bar.
 */
@Composable
fun PlayerList(
    players: List<Player>,
    localPlayerId: String,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
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
        BG3Gold
    }

    val borderColor = when {
        !player.isConnected -> BG3GoldBorder.copy(alpha = 0.3f)
        isLocal             -> BG3Gold
        else                -> BG3GoldBorder
    }
    val borderWidth = if (isLocal) 1.5.dp else 1.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(52.dp)
            .widthIn(min = 70.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(BG3SurfaceCard)
            .border(borderWidth, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Portrait circle
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    if (player.isConnected) chipColor.copy(alpha = 0.9f)
                    else chipColor.copy(alpha = 0.2f)
                )
                .border(1.dp, if (isLocal) BG3Gold else BG3GoldBorder.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (player.isDM) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = "DM",
                    tint     = BG3Cream,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text       = player.name.take(1).uppercase(),
                    color      = BG3Cream,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp
                )
            }
        }

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text       = player.name,
                color      = if (player.isConnected) BG3Cream else BG3CreamMuted.copy(alpha = 0.5f),
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text  = if (player.isDM) "DUNGEON MASTER" else if (isLocal) "YOU" else "",
                fontSize   = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = if (player.isDM) BG3Gold else BG3GoldBorder
            )
        }

        if (!player.isConnected) {
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.SignalWifiOff,
                contentDescription = "Disconnected",
                tint     = BG3GoldBorder.copy(alpha = 0.4f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
