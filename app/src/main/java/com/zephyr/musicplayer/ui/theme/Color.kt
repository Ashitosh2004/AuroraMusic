package com.zephyr.musicplayer.ui.theme

import androidx.compose.ui.graphics.Color

// Aurora theme palettes — flat solid colors, NO gradients, NO neon.
// Inspired by Apple Music / Spotify aesthetic but muted and refined.

// ---------- Midnight (Dark default) ----------
val MidnightPrimary = Color(0xFFE8E6E3)        // warm off-white
val MidnightOnSurface = Color(0xFFEAEAEA)
val MidnightSurface = Color(0xFF15161A)         // near-black, slight blue tint
val MidnightSurfaceVariant = Color(0xFF1F2025)
val MidnightSurfaceTint = Color(0xFF26282E)
val MidnightBackground = Color(0xFF0E0F12)

// ---------- Light (Daylight) ----------
val DaylightPrimary = Color(0xFF1A1A1A)
val DaylightOnSurface = Color(0xFF1B1B1D)
val DaylightSurface = Color(0xFFFBFBFC)
val DaylightSurfaceVariant = Color(0xFFF1F1F3)
val DaylightSurfaceTint = Color(0xFFE9E9EC)
val DaylightBackground = Color(0xFFF5F5F7)

// ---------- AMOLED (Pure black) ----------
val AmoledPrimary = Color(0xFFEDEDED)
val AmoledOnSurface = Color(0xFFEFEFEF)
val AmoledSurface = Color(0xFF000000)
val AmoledSurfaceVariant = Color(0xFF0A0A0A)
val AmoledSurfaceTint = Color(0xFF121214)
val AmoledBackground = Color(0xFF000000)

// ---------- Sepia (Warm paper) ----------
val SepiaPrimary = Color(0xFF3B2A1A)
val SepiaOnSurface = Color(0xFF332419)
val SepiaSurface = Color(0xFFF2E5D0)
val SepiaSurfaceVariant = Color(0xFFE8DBC4)
val SepiaSurfaceTint = Color(0xFFDCC9A8)
val SepiaBackground = Color(0xFFEAD9BE)

// ---------- Rose (Soft pink-tinted light) ----------
val RosePrimary = Color(0xFF2A1A1F)
val RoseOnSurface = Color(0xFF241519)
val RoseSurface = Color(0xFFFFF4F6)
val RoseSurfaceVariant = Color(0xFFF7E3E7)
val RoseSurfaceTint = Color(0xFFF0D3D8)
val RoseBackground = Color(0xFFFBE9ED)

// ---------- Forest (Deep green-tinted dark) ----------
val ForestPrimary = Color(0xFFE0E5DC)
val ForestOnSurface = Color(0xFFDCE2D7)
val ForestSurface = Color(0xFF101411)
val ForestSurfaceVariant = Color(0xFF181E1A)
val ForestSurfaceTint = Color(0xFF1F2721)
val ForestBackground = Color(0xFF0A0D0B)

// ---------- Accent options (all flat, no neon) ----------
val AccentCoral = Color(0xFFE2725B)            // muted coral
val AccentSlate = Color(0xFF5B7C99)            // dusty blue
val AccentSage = Color(0xFF7B8F6A)             // muted sage green
val AccentBurgundy = Color(0xFF8E3B46)         // deep burgundy
val AccentOlive = Color(0xFF7A7B3F)            // olive
val AccentIndigo = Color(0xFF4F5B8F)           // dusty indigo
val AccentTerracotta = Color(0xFFC46B4A)       // terracotta
val AccentCharcoal = Color(0xFF3A3D44)         // neutral charcoal
val AccentPlum = Color(0xFF6B486D)             // plum
val AccentTeal = Color(0xFF3A6E6A)             // deep teal

data class AccentPalette(
    val name: String,
    val color: Color
)

val accentOptions = listOf(
    AccentPalette("Coral", AccentCoral),
    AccentPalette("Slate", AccentSlate),
    AccentPalette("Sage", AccentSage),
    AccentPalette("Burgundy", AccentBurgundy),
    AccentPalette("Olive", AccentOlive),
    AccentPalette("Indigo", AccentIndigo),
    AccentPalette("Terracotta", AccentTerracotta),
    AccentPalette("Charcoal", AccentCharcoal),
    AccentPalette("Plum", AccentPlum),
    AccentPalette("Teal", AccentTeal)
)
