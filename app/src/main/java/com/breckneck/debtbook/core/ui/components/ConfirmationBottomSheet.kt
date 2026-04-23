package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing

/**
 * Confirmation bottom sheet with title, message and action buttons (replaces dialog_are_you_sure.xml).
 * Follows M3 button hierarchy: destructive confirm = FilledTonal(error), dismiss = TextButton.
 * Used in: DebtDetails, CreateFinance, Synchronization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    title: String,
    message: String,
    confirmText: String = stringResource(R.string.yes),
    dismissText: String = stringResource(R.string.No),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DebtBookBottomSheet(onDismiss = onDismiss) {
        ConfirmationContent(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
internal fun ConfirmationContent(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = spacing.space24)
            .padding(top = spacing.space24, bottom = spacing.space24)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.space16))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing.space24))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
            FilledTonalButton(
                onClick = onConfirm,
                modifier = Modifier.padding(start = spacing.space8),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(text = confirmText)
            }
        }
    }
}

@Preview(name = "Confirmation — light")
@Preview(name = "Confirmation — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ConfirmationBottomSheetPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            ConfirmationContent(
                title = "Are you sure?",
                message = "Delete this debt? This action cannot be undone.",
                confirmText = "Yes",
                dismissText = "No",
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
