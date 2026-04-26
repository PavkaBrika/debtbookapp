package com.breckneck.debtbook.goal.main.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.core.ui.theme.LocalDebtBookDarkTheme
import com.breckneck.debtbook.core.ui.theme.elevation
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.debtbook.goal.main.model.GoalUi

@Composable
fun GoalItem(
    goalUi: GoalUi,
    onGoalClick: (GoalUi) -> Unit,
    onAddClick: (GoalUi) -> Unit,
    onDeleteClick: (GoalUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing

    val isDark = LocalDebtBookDarkTheme.current
    Card(
        onClick = { onGoalClick(goalUi) },
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
                             else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.card),
        border = if (isDark) null
                 else BorderStroke(MaterialTheme.elevation.borderHairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.space12, vertical = spacing.space4)
    ) {
        Column {
            if (goalUi.hasPhoto) {
                AsyncImage(
                    model = goalUi.photoPath,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.padding(spacing.space16),
                verticalArrangement = Arrangement.spacedBy(spacing.space8)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goalUi.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = spacing.space8)
                    )
                    if (goalUi.date != null) {
                        val chipTint = when {
                            goalUi.isReached -> MaterialTheme.colorScheme.primary
                            goalUi.isOverdue -> Color.Red
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        GoalDateChip(text = goalUi.date, iconTint = chipTint)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goalUi.savedAmount,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = " ${goalUi.currency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = goalUi.totalAmount,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = " ${goalUi.currency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (goalUi.isReached) {
                    Text(
                        text = stringResource(R.string.congratulations_you_have_reached_your_goal),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = { onDeleteClick(goalUi) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = goalUi.remainingAmount.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = " ${goalUi.currency}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " ${stringResource(R.string.remaining)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onAddClick(goalUi) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.space8)
                    ) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalDateChip(
    text: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(spacing.space4))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = iconTint
        )
    }
}

private fun previewGoalUi(
    id: Int = 1,
    name: String = "New Car",
    savedAmount: String = "1 200",
    totalAmount: String = "5 000",
    currency: String = "USD",
    isReached: Boolean = false,
    remainingAmount: String? = "3 800",
    dateChipText: String? = "15 Jan 2026",
    isOverdue: Boolean = false,
) = GoalUi(
    goalId = id,
    name = name,
    photoPath = null,
    hasPhoto = false,
    savedAmount = savedAmount,
    totalAmount = totalAmount,
    currency = currency,
    isReached = isReached,
    remainingAmount = remainingAmount,
    date = dateChipText,
    isOverdue = isOverdue,
)

@Preview(name = "GoalItem — active, light")
@Preview(name = "GoalItem — active, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalItemActivePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalItem(
                goalUi = previewGoalUi(),
                onGoalClick = {},
                onAddClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(name = "GoalItem — reached, light")
@Composable
private fun GoalItemReachedPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalItem(
                goalUi = previewGoalUi(
                    name = "Dream Vacation",
                    savedAmount = "2 000",
                    totalAmount = "2 000",
                    isReached = true,
                    remainingAmount = null,
                    dateChipText = "Achieved in 45 days",
                ),
                onGoalClick = {},
                onAddClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(name = "GoalItem — overdue, light")
@Composable
private fun GoalItemOverduePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalItem(
                goalUi = previewGoalUi(
                    name = "Emergency Fund",
                    savedAmount = "3 000",
                    totalAmount = "10 000",
                    remainingAmount = "7 000",
                    dateChipText = "Overdue",
                    isOverdue = true,
                ),
                onGoalClick = {},
                onAddClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(name = "GoalItem — no date, light")
@Composable
private fun GoalItemNoDatePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalItem(
                goalUi = previewGoalUi(
                    name = "Home Renovation",
                    savedAmount = "500",
                    totalAmount = "15 000",
                    remainingAmount = "14 500",
                    dateChipText = null,
                ),
                onGoalClick = {},
                onAddClick = {},
                onDeleteClick = {}
            )
        }
    }
}
