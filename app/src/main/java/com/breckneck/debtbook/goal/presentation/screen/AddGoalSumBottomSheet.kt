package com.breckneck.debtbook.goal.presentation.screen

import android.content.res.Configuration
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSumBottomSheet(
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sumText by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val mustEnterText = stringResource(R.string.youmustentername)
    val zeroDebtText = stringResource(R.string.zerodebt)
    val spacing = MaterialTheme.spacing

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space24)
                .padding(bottom = spacing.space24)
                .imePadding()
        ) {
            Text(
                text = stringResource(R.string.add),
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = spacing.space16)
            )
            OutlinedTextField(
                value = sumText,
                onValueChange = { newValue ->
                    val dotIndex = newValue.indexOf('.')
                    val isValid = if (dotIndex != -1) {
                        val afterDot = newValue.substring(dotIndex + 1)
                        val beforeDot = newValue.substring(0, dotIndex)
                        afterDot.length <= 2 && beforeDot.isNotEmpty()
                    } else {
                        true
                    }
                    if (isValid) {
                        sumText = newValue
                        errorMessage = null
                    }
                },
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
                Button(
                    onClick = {
                        val trimmed = sumText.trim()
                        val parsed = trimmed.toDoubleOrNull()
                        when {
                            trimmed.isEmpty() -> errorMessage = mustEnterText
                            parsed == null || parsed == 0.0 -> errorMessage = zeroDebtText
                            else -> onConfirm(parsed)
                        }
                    }
                ) {
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
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
