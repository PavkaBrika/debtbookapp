package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtBookTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
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
        actions = actions
    )
}

@Preview(name = "TopBar — root screen (light)")
@Preview(name = "TopBar — root screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DebtBookTopBarRootPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            DebtBookTopBar(
                title = "Debts",
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Preview(name = "TopBar — inner screen (light)")
@Preview(name = "TopBar — inner screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DebtBookTopBarInnerPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            DebtBookTopBar(
                title = "John Doe — very long name that should be ellipsized",
                onBackClick = {}
            )
        }
    }
}
