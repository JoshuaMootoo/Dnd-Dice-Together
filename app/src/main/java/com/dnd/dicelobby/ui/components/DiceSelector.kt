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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.dice.DiceType

/**
 * Horizontal quick-pick bar for selecting the active die type.
 * Tapping a chip updates [selectedType] via [onTypeSelected].
 */
@Composable
fun DiceSelector(
    selectedType: DiceType,
    onTypeSelected: (DiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
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
    val accent = MaterialTheme.colorScheme.primary

    Surface(
        onClick = onClick,
        shape   = RoundedCornerShape(12.dp),
        color   = if (isSelected) accent.copy(alpha = 0.25f) else Color.Transparent,
        border  = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accent else Color.White.copy(alpha = 0.3f)
        ),
        modifier = Modifier.size(width = 56.dp, height = 56.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text       = type.label.uppercase(),
                fontSize   = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = if (isSelected) accent else Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
