package com.sinced.ui.theme

import androidx.compose.ui.graphics.Color

val SincedPrimary = Color(0xFF7AA2F7)
val SincedOnPrimary = Color(0xFF0B1220)
val SincedBackground = Color(0xFF121212)
val SincedSurface = Color(0xFF1A1A1A)
val SincedSurfaceVariant = Color(0xFF242424)
val SincedOnSurface = Color(0xFFE6E6E6)
val SincedOnSurfaceVariant = Color(0xFF9AA0A6)
val SincedOutline = Color(0xFF3A3A3A)

val StatusOverdue = Color(0xFFE57373)
val StatusDueToday = Color(0xFFFFB74D)
val StatusDueSoon = Color(0xFFFFD54F)
val StatusOk = SincedOnSurface
val StatusNeverLogged = SincedOnSurfaceVariant

object CategoryPalette {
    const val CYAN = 0xFF4DD0E1.toInt()
    const val PURPLE = 0xFFB39DDB.toInt()
    const val AMBER = 0xFFFFB74D.toInt()
    const val GREEN = 0xFF81C784.toInt()
    const val PINK = 0xFFF48FB1.toInt()
    const val BLUE = 0xFF7AA2F7.toInt()
    const val LIME = 0xFFCDDC39.toInt()
    const val ORANGE = 0xFFFF8A65.toInt()

    val presets: List<Int> = listOf(CYAN, PURPLE, AMBER, GREEN, PINK, BLUE, LIME, ORANGE)
}
