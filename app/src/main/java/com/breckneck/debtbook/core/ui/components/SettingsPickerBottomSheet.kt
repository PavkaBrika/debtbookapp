package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme

/**
 * Generic bottom sheet for picking one option from a list (replaces dialog_setting.xml + SettingsAdapter).
 * Used in: NewDebt (currency), CreateGoals (currency), Finance (currency, interval), Settings (currency x3, theme).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPickerBottomSheet(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SettingsPickerContent(
            title = title,
            options = options,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected
        )
    }
}

@Composable
internal fun SettingsPickerContent(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            itemsIndexed(options) { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(index) }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = index == selectedIndex,
                        onClick = { onItemSelected(index) }
                    )
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "SettingsPicker — light")
@Preview(name = "SettingsPicker — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsPickerPreview() {
    DebtBookTheme {
        Surface {
            SettingsPickerContent(
                title = "Select currency",
                options = listOf("🇷🇺 RUB", "🇺🇸 USD", "🇪🇺 EUR", "🇬🇧 GBP", "🇨🇳 CNY"),
                selectedIndex = 1,
                onItemSelected = {}
            )
        }
    }
}
