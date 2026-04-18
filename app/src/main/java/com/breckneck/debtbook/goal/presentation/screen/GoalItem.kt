package com.breckneck.debtbook.goal.presentation.screen

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.breckneck.debtbook.core.ui.theme.spacing
import com.breckneck.deptbook.domain.model.Goal
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun GoalItem(
    goal: Goal,
    onGoalClick: (Goal) -> Unit,
    onAddClick: (Goal) -> Unit,
    onDeleteClick: (Goal) -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember {
        DecimalFormat("###,###,###.##").apply {
            decimalFormatSymbols = DecimalFormatSymbols().apply {
                groupingSeparator = ' '
            }
        }
    }
    val sdf = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val spacing = MaterialTheme.spacing

    val hasPhoto = remember(goal.photoPath) {
        goal.photoPath != null && File(goal.photoPath!!).exists()
    }
    val isReached = goal.sum - goal.savedSum <= 0

    ElevatedCard(
        onClick = { onGoalClick(goal) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.space12, vertical = spacing.space4)
    ) {
        Column {
            if (hasPhoto) {
                AsyncImage(
                    model = goal.photoPath,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.padding(spacing.space16),
                verticalArrangement = Arrangement.spacedBy(spacing.space8)
            ) {
                // Name + date row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = spacing.space8)
                    )
                    if (isReached) {
                        val diffInDays = TimeUnit.DAYS.convert(
                            Date().time - goal.creationDate.time,
                            TimeUnit.MILLISECONDS
                        ).coerceAtLeast(1L)
                        GoalDateChip(
                            text = if (diffInDays == 1L)
                                stringResource(R.string.achieved_in_day, diffInDays)
                            else
                                stringResource(R.string.achieved_in_days, diffInDays),
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    } else if (goal.goalDate != null) {
                        val isOverdue = goal.goalDate!!.before(Date())
                        GoalDateChip(
                            text = if (isOverdue) stringResource(R.string.overdue)
                            else sdf.format(goal.goalDate!!),
                            iconTint = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Saved / total amounts
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = decimalFormat.format(goal.savedSum),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = " ${goal.currency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = decimalFormat.format(goal.sum),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = " ${goal.currency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isReached) {
                    Text(
                        text = stringResource(R.string.congratulations_you_have_reached_your_goal),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = { onDeleteClick(goal) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = decimalFormat.format(goal.sum - goal.savedSum),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = " ${goal.currency}",
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
                        onClick = { onAddClick(goal) },
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

@Preview(name = "GoalItem — active, light")
@Preview(name = "GoalItem — active, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalItemActivePreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalItem(
                goal = Goal(
                    id = 1,
                    name = "New Car",
                    sum = 5000.0,
                    savedSum = 1200.0,
                    currency = "USD",
                    photoPath = null,
                    creationDate = Date(),
                    goalDate = Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000)
                ),
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
                goal = Goal(
                    id = 2,
                    name = "Dream Vacation",
                    sum = 2000.0,
                    savedSum = 2000.0,
                    currency = "EUR",
                    photoPath = null,
                    creationDate = Date(System.currentTimeMillis() - 60L * 24 * 3600 * 1000),
                    goalDate = null
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
                goal = Goal(
                    id = 3,
                    name = "Emergency Fund",
                    sum = 10000.0,
                    savedSum = 3000.0,
                    currency = "USD",
                    photoPath = null,
                    creationDate = Date(System.currentTimeMillis() - 120L * 24 * 3600 * 1000),
                    goalDate = Date(System.currentTimeMillis() - 10L * 24 * 3600 * 1000)
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
                goal = Goal(
                    id = 4,
                    name = "Home Renovation",
                    sum = 15000.0,
                    savedSum = 500.0,
                    currency = "USD",
                    photoPath = null,
                    creationDate = Date(),
                    goalDate = null
                ),
                onGoalClick = {},
                onAddClick = {},
                onDeleteClick = {}
            )
        }
    }
}
