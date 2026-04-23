package com.breckneck.debtbook.goal.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.DebtBookBottomSheet
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.debtbook.goal.main.AddDepositPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSumBottomSheet(
    sumText: String,
    inputError: AddDepositPopup.DepositInputError?,
    onSumChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val errorMessage = when (inputError) {
        AddDepositPopup.DepositInputError.EMPTY -> stringResource(R.string.youmustentername)
        AddDepositPopup.DepositInputError.ZERO_OR_INVALID -> stringResource(R.string.zerodebt)
        null -> null
    }

    DebtBookBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space24)
                .padding(top = spacing.space24, bottom = spacing.space24)
                .imePadding()
        ) {
            Text(
                text = stringResource(R.string.add),
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = spacing.space16)
            )
            OutlinedTextField(
                value = sumText,
                onValueChange = onSumChange,
                label = { Text(stringResource(R.string.sum)) },
                isError = errorMessage != null,
                supportingText = errorMessage?.let { msg -> { Text(msg) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.space24),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(spacing.space8))
                Button(onClick = onConfirm) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AddGoalSumBottomSheetPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            AddGoalSumBottomSheet(
                sumText = "1500",
                inputError = null,
                onSumChange = {},
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddGoalSumBottomSheetErrorPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            AddGoalSumBottomSheet(
                sumText = "",
                inputError = AddDepositPopup.DepositInputError.EMPTY,
                onSumChange = {},
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
