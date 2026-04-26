package com.breckneck.debtbook.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard elevation tokens for surfaces (cards, etc.). Provided with [DebtBookTheme]
 * via [LocalElevation]; read with [MaterialTheme.elevation].
 */
data class Elevation(
    val card: Dp = 4.dp,
    val borderHairline: Dp = 1.dp,
)

val LocalElevation = staticCompositionLocalOf { Elevation() }

val MaterialTheme.elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current
