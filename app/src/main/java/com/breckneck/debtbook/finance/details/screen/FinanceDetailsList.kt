package com.breckneck.debtbook.finance.details.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.deptbook.domain.model.Finance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
internal fun FinanceDetailsList(
    subtitle: String,
    isExpenses: Boolean,
    currency: String,
    financeList: List<Finance>,
    onFinanceClick: (Finance) -> Unit
) {
    val spacing = MaterialTheme.spacing
    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val rowDateFormatter = remember { SimpleDateFormat("d MMM", Locale.getDefault()) }
    val decimalFormat = remember {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        DecimalFormat("###,###,###.##", symbols)
    }
    val groupedFinances = remember(financeList) {
        financeList.groupBy { monthFormatter.format(it.date) }
    }
    val hintText = stringResource(R.string.details_recycler_view_hint)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item(key = "type_badge") {
            TypeBadge(
                label = subtitle,
                isExpenses = isExpenses,
                modifier = Modifier.padding(
                    horizontal = spacing.space16,
                    vertical = spacing.space12
                )
            )
        }

        groupedFinances.forEach { (monthLabel, entries) ->
            stickyHeader(key = "header_$monthLabel") {
                MonthSectionHeader(label = monthLabel)
            }
            items(entries, key = { "finance_${it.id}" }) { finance ->
                FinanceRow(
                    finance = finance,
                    currency = currency,
                    dateFormatter = rowDateFormatter,
                    decimalFormat = decimalFormat,
                    onClick = { onFinanceClick(finance) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = spacing.space16),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }

        item(key = "footer_hint") {
            Text(
                text = hintText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space32, vertical = spacing.space24)
            )
        }
    }
}

@Composable
private fun TypeBadge(
    label: String,
    isExpenses: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isExpenses) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = if (isExpenses) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = label.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun MonthSectionHeader(label: String) {
    val spacing = MaterialTheme.spacing
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(
                horizontal = spacing.space16,
                vertical = spacing.space8
            )
        )
    }
}

@Composable
private fun FinanceRow(
    finance: Finance,
    currency: String,
    dateFormatter: SimpleDateFormat,
    decimalFormat: DecimalFormat,
    onClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val formattedSum = remember(finance.sum) { decimalFormat.format(finance.sum) }
    val formattedDate = remember(finance.date) { dateFormatter.format(finance.date) }
    val accessibilityDesc = "$formattedSum $currency, $formattedDate"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityDesc
                role = Role.Button
            }
            .clickable { onClick() }
            .padding(horizontal = spacing.space16, vertical = spacing.space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = formattedSum,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline()
                )
                Spacer(modifier = Modifier.width(spacing.space8))
                Text(
                    text = currency,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alignByBaseline()
                )
            }
            if (!finance.info.isNullOrEmpty()) {
                Text(
                    text = finance.info!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(spacing.space12))

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(spacing.space8))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(name = "List Filled - light")
@Preview(name = "List Filled - dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsListFilledPreview() {
    val april = Calendar.getInstance().apply { set(2026, 3, 5) }.time
    val aprilEarly = Calendar.getInstance().apply { set(2026, 3, 1) }.time
    val march = Calendar.getInstance().apply { set(2026, 2, 28) }.time

    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsList(
                subtitle = "Expenses",
                isExpenses = true,
                currency = "USD",
                financeList = listOf(
                    Finance(id = 1, sum = 1500.0, date = april, info = "Lunch at restaurant", financeCategoryId = 1),
                    Finance(id = 2, sum = 250.50, date = aprilEarly, info = null, financeCategoryId = 1),
                    Finance(id = 3, sum = 89.99, date = march, info = "Coffee & snacks", financeCategoryId = 1)
                ),
                onFinanceClick = {}
            )
        }
    }
}

@Preview(name = "List Income - light")
@Preview(name = "List Income - dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinanceDetailsListIncomePreview() {
    @Suppress("MagicNumber")
    val date = Calendar.getInstance().apply { set(2026, 3, 1) }.time

    DebtBookTheme(dynamicColor = false) {
        Surface {
            FinanceDetailsList(
                subtitle = "Revenues",
                isExpenses = false,
                currency = "EUR",
                financeList = listOf(
                    Finance(id = 4, sum = 3200.0, date = date, info = "April salary", financeCategoryId = 2),
                    Finance(id = 5, sum = 150.0, date = date, info = null, financeCategoryId = 2)
                ),
                onFinanceClick = {}
            )
        }
    }
}
