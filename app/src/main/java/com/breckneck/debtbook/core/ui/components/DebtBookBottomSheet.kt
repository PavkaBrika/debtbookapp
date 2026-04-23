package com.breckneck.debtbook.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

/**
 * Project-standard bottom sheet wrapper.
 *
 * Enforces design system rules for every bottom sheet in the app:
 * - [dragHandle] = null — the default M3 drag pill is visually noisy and conflicts
 *   with DebtBook's calm design language; swipe-to-dismiss still works via back gesture / scrim tap.
 * - [skipPartiallyExpanded] = true — sheets always open fully expanded; half-expanded state
 *   is confusing and not used anywhere in the app.
 *
 * Content is responsible for its own top padding (24.dp) to compensate for the removed drag handle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtBookBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        content = content,
    )
}
