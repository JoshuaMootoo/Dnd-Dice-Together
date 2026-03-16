package com.dnd.dicelobby.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.ui.theme.BG3Gold
import com.dnd.dicelobby.ui.theme.BG3GoldBorder
import com.dnd.dicelobby.ui.theme.BG3GoldBright
import com.dnd.dicelobby.ui.theme.BG3Background
import com.dnd.dicelobby.ui.theme.BG3SurfaceCard
import com.dnd.dicelobby.ui.theme.BG3CreamMuted
import com.dnd.dicelobby.ui.theme.BG3Cream

/**
 * Horizontal quick-pick bar for selecting the active die type.
 * Styled after Baldur's Gate 3's die selection panel.
 */
@Composable
fun DiceSelector(
    selectedType: DiceType,
    onTypeSelected: (DiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(DiceType.entries) { type ->
            DiceChip(
                type       = type,
                isSelected = type == selectedType,
                onClick    = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun DiceChip(
    type: DiceType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick      = onClick,
        shape        = RoundedCornerShape(4.dp),
        color        = if (isSelected) BG3SurfaceCard else BG3Background,
        border       = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) BG3Gold else BG3GoldBorder
        ),
        modifier = Modifier.size(width = 52.dp, height = 60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(4.dp)
        ) {
            // Larger die-face number (e.g. "20" for d20)
            val sides = type.label.removePrefix("d").removePrefix("D")
            Text(
                text       = sides,
                fontSize   = if (sides.length <= 2) 20.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color      = if (isSelected) BG3GoldBright else BG3CreamMuted,
                textAlign  = TextAlign.Center
            )
            Text(
                text       = "d",
                fontSize   = 9.sp,
                fontWeight = FontWeight.Normal,
                color      = if (isSelected) BG3Gold.copy(alpha = 0.8f) else BG3GoldBorder,
                letterSpacing = 0.sp
            )
        }
    }
}
