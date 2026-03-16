package com.dnd.dicelobby.ui.theme

import androidx.compose.ui.graphics.Color

// ── Baldur's Gate 3 inspired warm-dark palette ──────────────────────────────
val BG3Background   = Color(0xFF0A0806)   // near-black with warm undertone
val BG3Surface      = Color(0xFF1A1208)   // dark leather/mahogany surface
val BG3SurfaceCard  = Color(0xFF231A0C)   // card / panel surface
val BG3Gold         = Color(0xFFC8A84B)   // primary accent — muted warm gold
val BG3GoldBright   = Color(0xFFE8C868)   // highlight / selected gold
val BG3GoldBorder   = Color(0xFF5A4A1C)   // subtle unselected border
val BG3Red          = Color(0xFF6B1414)   // dark blood red
val BG3RedBright    = Color(0xFFB52828)   // brighter red for buttons / alerts
val BG3Cream        = Color(0xFFF0E6D0)   // primary text — parchment/cream
val BG3CreamMuted   = Color(0xFFB8A888)   // secondary text — muted parchment
val BG3CritGold     = Color(0xFFFFD700)   // critical success highlight (nat 20)
val BG3CritRed      = Color(0xFF8B0000)   // critical fail  highlight (nat 1)

// Backward-compatible aliases used throughout the codebase
val DarkBackground  = BG3Background
val SurfaceVariant  = BG3Surface
val AccentCrimson   = BG3RedBright
val AccentGold      = BG3Gold
val TextPrimary     = BG3Cream
val TextSecondary   = BG3CreamMuted
val DiceTableGreen  = Color(0xFF1B4332)

// Purple80/40 placeholders still referenced by Theme.kt — remap to gold tones
val Purple80        = BG3Gold
val PurpleGrey80    = BG3CreamMuted
val Pink80          = BG3GoldBright
val Purple40        = BG3GoldBorder
val PurpleGrey40    = Color(0xFF4A3C18)
val Pink40          = BG3Red

/** Player colour choices shown in the colour picker. */
val playerColors = listOf(
    "#E53935", // Red
    "#8E24AA", // Purple
    "#1E88E5", // Blue
    "#00ACC1", // Cyan
    "#43A047", // Green
    "#FB8C00", // Orange
    "#F4511E", // Deep Orange
    "#6D4C41"  // Brown
)
