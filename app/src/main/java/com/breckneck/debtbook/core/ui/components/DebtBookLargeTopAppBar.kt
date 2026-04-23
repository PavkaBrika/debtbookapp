package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme

/**
 * Project-standard collapsing top bar.
 *
 * Fixes two M3 defaults that conflict with DebtBook's calm design language:
 * - Overrides [scrolledContainerColor] to [MaterialTheme.colorScheme.surface] so the bar
 *   stays the same color when collapsed (M3 default shifts to surfaceContainerHigh = gray).
 * - Adds a [HorizontalDivider] whose alpha tracks [TopAppBarScrollBehavior.state.collapsedFraction],
 *   producing a clean fade-in separator instead of a sudden color change.
 *
 * Pass the same [scrollBehavior] instance to both [Modifier.nestedScroll] on the [Scaffold]
 * and this composable so scrolling collapses the bar correctly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtBookLargeTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    ),
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    Column {
        LargeTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = collapsedFraction)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "LargeTopAppBar — expanded, light")
@Preview(name = "LargeTopAppBar — expanded, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DebtBookLargeTopAppBarExpandedPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            DebtBookLargeTopAppBar(
                title = "Goals",
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState()
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "LargeTopAppBar — with back, light")
@Composable
private fun DebtBookLargeTopAppBarBackPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            DebtBookLargeTopAppBar(
                title = "Food — Expenses",
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState()
                ),
                onBackClick = {},
            )
        }
    }
}
