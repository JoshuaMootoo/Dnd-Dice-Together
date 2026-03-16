package com.dnd.dicelobby.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.dnd.dicelobby.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Scrollable combat-log style roll history. Newest entries appear at the top.
 * Styled after Baldur's Gate 3's action log.
 */
@Composable
fun RollLogList(
    rolls: List<Roll>,
    viewerIsDM: Boolean,
    onReveal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(rolls.size) {
        if (rolls.isNotEmpty()) listState.animateScrollToItem(0)
    }

    if (rolls.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = "— No rolls yet —",
                    color = BG3GoldBorder,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Select your dice and roll",
                    color = BG3CreamMuted.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    } else {
        LazyColumn(
            state               = listState,
            modifier            = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding      = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
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

    // BG3: critical results get special treatment
    val isCritSuccess = !isHiddenForViewer && roll.diceResults.size == 1 && roll.diceResults.first() == 20
    val isCritFail    = !isHiddenForViewer && roll.diceResults.size == 1 && roll.diceResults.first() == 1
    val totalColor = when {
        isCritSuccess -> BG3CritGold
        isCritFail    -> BG3RedBright
        isHiddenForViewer -> BG3CreamMuted.copy(alpha = 0.4f)
        else          -> BG3Cream
    }
    val cardBg = when {
        isCritSuccess -> Color(0xFF1A1600)
        isCritFail    -> Color(0xFF1A0800)
        isHiddenForViewer -> Color(0xFF0F0C08)
        else          -> BG3SurfaceCard
    }
    val borderColor = when {
        isCritSuccess -> BG3CritGold.copy(alpha = 0.5f)
        isCritFail    -> BG3RedBright.copy(alpha = 0.4f)
        else          -> BG3GoldBorder.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(4.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(enabled = !isHiddenForViewer) { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isHiddenForViewer) {
                        Text(
                            text      = roll.playerName,
                            color     = BG3GoldBorder,
                            fontWeight = FontWeight.SemiBold,
                            fontSize  = 13.sp
                        )
                        Text(
                            text      = "rolled hidden dice…",
                            color     = BG3CreamMuted.copy(alpha = 0.5f),
                            fontStyle = FontStyle.Italic,
                            fontSize  = 12.sp
                        )
                    } else {
                        // Player name in gold
                        Text(
                            text       = roll.playerName,
                            color      = BG3Gold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp
                        )
                        // Formula in muted cream
                        Text(
                            text  = "rolled ${roll.diceFormula}",
                            color = BG3CreamMuted,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        // Big result number
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text       = "${roll.total}",
                                color      = totalColor,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 28.sp,
                                lineHeight = 28.sp
                            )
                            if (isCritSuccess) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text     = "CRITICAL",
                                    color    = BG3CritGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            } else if (isCritFail) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text     = "FUMBLE",
                                    color    = BG3RedBright,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (roll.isHidden && viewerIsDM) {
                        OutlinedButton(
                            onClick      = { onReveal(roll.id) },
                            modifier     = Modifier.height(30.dp),
                            shape        = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            border       = androidx.compose.foundation.BorderStroke(1.dp, BG3Gold),
                            colors       = ButtonDefaults.outlinedButtonColors(contentColor = BG3Gold)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Reveal",
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("REVEAL", fontSize = 10.sp, letterSpacing = 0.5.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text  = formatTimestamp(roll.timestamp),
                        color = BG3GoldBorder,
                        fontSize = 10.sp,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            // Expanded die breakdown
            if (expanded && !isHiddenForViewer && roll.diceResults.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = BG3GoldBorder.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = roll.detailText(),
                    color = BG3CreamMuted,
                    fontSize = 12.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

private fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(ts))
}
