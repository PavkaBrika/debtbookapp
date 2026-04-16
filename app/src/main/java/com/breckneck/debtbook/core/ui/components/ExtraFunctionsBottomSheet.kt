package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing

/**
 * Bottom sheet with Edit / Delete / Cancel actions (replaces dialog_extra_functions.xml).
 * M3 button hierarchy: Edit = FilledTonal (primary), Delete = FilledTonal (error), Cancel = TextButton.
 * Used in: DebtDetails, FinanceDetails, GoalDetails.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFunctionsBottomSheet(
    title: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val hideAndDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        ExtraFunctionsContent(
            title = title,
            onEdit = onEdit,
            onDelete = onDelete,
            onDismiss = hideAndDismiss
        )
    }
}

@Composable
private fun ExtraFunctionsContent(
    title: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = spacing.space24)
    ) {
        Spacer(modifier = Modifier.height(spacing.space24))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(spacing.space24))

        FilledTonalButton(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(spacing.space8))

            Text(text = stringResource(R.string.edit))
        }

        Spacer(modifier = Modifier.height(spacing.space8))

        FilledTonalButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(spacing.space8))

            Text(text = stringResource(R.string.delete))
        }
        Spacer(modifier = Modifier.height(spacing.space8))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.cancel))
        }

        Spacer(modifier = Modifier.height(spacing.space16))
    }
}

@Preview(name = "ExtraFunctions — light")
@Preview(name = "ExtraFunctions — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExtraFunctionsBottomSheetPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            ExtraFunctionsContent(
                title = "Debt: 150.00 USD",
                onEdit = {},
                onDelete = {},
                onDismiss = {}
            )
        }
    }
}
