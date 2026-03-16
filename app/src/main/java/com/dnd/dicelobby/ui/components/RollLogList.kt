package com.dnd.dicelobby.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.models.Roll
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Scrollable roll log. Newest entries appear at the top.
 *
 * @param rolls        The ordered list of [Roll] items to display.
 * @param viewerIsDM   Whether the current device is the DM (affects hidden-roll display).
 * @param onReveal     Called when the DM taps "Reveal" on a hidden roll.
 */
@Composable
fun RollLogList(
    rolls: List<Roll>,
    viewerIsDM: Boolean,
    onReveal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Scroll to top whenever a new roll arrives
    LaunchedEffect(rolls.size) {
        if (rolls.isNotEmpty()) listState.animateScrollToItem(0)
    }

    if (rolls.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "No rolls yet. Roll some dice!",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            state    = listState,
            modifier = modifier,
            reverseLayout        = false,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(
                items = rolls.sortedByDescending { it.timestamp },
                key   = { it.id }
            ) { roll ->
                RollLogItem(roll, viewerIsDM, onReveal)
            }
        }
    }
}

@Composable
private fun RollLogItem(
    roll: Roll,
    viewerIsDM: Boolean,
    onReveal: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isHiddenForViewer = roll.isHidden && !viewerIsDM
    val cardColor = when {
        isHiddenForViewer      -> Color(0xFF1A1A2E)
        roll.total >= 20 && !isHiddenForViewer -> Color(0xFF1A3A1A)  // highlight nat-20 range
        else                   -> Color(0xFF1E1A2E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { if (!isHiddenForViewer) expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape  = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.SpaceBetween,
                modifier               = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isHiddenForViewer) {
                        Text(
                            text  = "${roll.playerName} rolled (hidden)",
                            color = TextSecondary,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text  = "${roll.playerName} rolled ${roll.diceFormula}",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                        Text(
                            text  = "→ ${roll.total}",
                            color = AccentGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    // DM "Reveal" button for hidden rolls
                    if (roll.isHidden && viewerIsDM) {
                        FilledTonalButton(
                            onClick = { onReveal(roll.id) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = "Reveal",
                                 modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reveal", fontSize = 12.sp)
                        }
                    }
                    Text(
                        text  = formatTimestamp(roll.timestamp),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Expanded detail row — individual die values
            if (expanded && !isHiddenForViewer && roll.diceResults.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Divider(color = Color.White.copy(alpha = 0.1f))
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = roll.detailText(),
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(ts))
}
