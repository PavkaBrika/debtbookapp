package com.breckneck.debtbook.goal.main.screen

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.EmptyListPlaceholder
import com.breckneck.debtbook.core.ui.components.ShimmerListPlaceholder
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.goal.main.GoalsAction
import com.breckneck.debtbook.goal.main.GoalsState
import com.breckneck.debtbook.goal.main.GoalsViewModel
import com.breckneck.debtbook.goal.main.model.GoalUi
import com.breckneck.deptbook.domain.util.ListState
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun GoalsScreen(vm: GoalsViewModel) {
    val state by vm.collectAsState()
    GoalsContent(
        state = state,
        onAction = vm::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsContent(
    state: GoalsState,
    onAction: (GoalsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.goals)) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(GoalsAction.AddGoalClick) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_goal)
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Crossfade(
            targetState = state.listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "goals_state"
        ) { listState ->
            when (listState) {
                ListState.LOADING -> {
                    ShimmerListPlaceholder(rowCount = 2, rowHeight = 240.dp)
                }
                ListState.EMPTY -> {
                    EmptyListPlaceholder(
                        emptyText = stringResource(R.string.there_are_no_goals_yet_click_the_button_below_to_add),
                        emptyIcon = Icons.Default.Flag
                    )
                }
                ListState.RECEIVED -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 88.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.goalList,
                            key = { it.goalId }
                        ) { goalUi ->
                            GoalItem(
                                goalUi = goalUi,
                                onGoalClick = { onAction(GoalsAction.GoalClick(it)) },
                                onAddClick = { onAction(GoalsAction.ShowAddDepositPopup(it)) },
                                onDeleteClick = { onAction(GoalsAction.DeleteGoal(it)) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.addDepositPopup.isVisible) {
        AddGoalSumBottomSheet(
            sumText = state.addDepositPopup.sumText,
            inputError = state.addDepositPopup.inputError,
            onSumChange = { onAction(GoalsAction.UpdateDepositSum(it)) },
            onConfirm = { onAction(GoalsAction.AddGoalDeposit) },
            onDismiss = { onAction(GoalsAction.DismissAddDepositPopup) }
        )
    }
}

private fun previewGoalUi(
    id: Int,
    name: String,
    savedAmount: String,
    totalAmount: String,
    dateChipText: String? = null,
    isOverdue: Boolean = false,
    isReached: Boolean = false,
    remainingAmount: String? = null,
) = GoalUi(
    goalId = id,
    name = name,
    photoPath = null,
    hasPhoto = false,
    savedAmount = savedAmount,
    totalAmount = totalAmount,
    currency = "USD",
    isReached = isReached,
    remainingAmount = remainingAmount,
    date = dateChipText,
    isOverdue = isOverdue,
)

@Preview(name = "GoalsScreen — list, light")
@Preview(name = "GoalsScreen — list, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalsScreenListPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalsContent(
                state = GoalsState(
                    goalList = listOf(
                        previewGoalUi(
                            id = 1,
                            name = "New Car",
                            savedAmount = "1 200",
                            totalAmount = "5 000",
                            dateChipText = "15 Jan 2026",
                            remainingAmount = "3 800",
                        ),
                        previewGoalUi(
                            id = 2,
                            name = "Dream Vacation",
                            savedAmount = "2 000",
                            totalAmount = "2 000",
                            isReached = true,
                            dateChipText = "Achieved in 45 days",
                        ),
                    ),
                    listState = ListState.RECEIVED
                ),
                onAction = {}
            )
        }
    }
}

@Preview(name = "GoalsScreen — empty, light")
@Composable
private fun GoalsScreenEmptyPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalsContent(
                state = GoalsState(goalList = emptyList(), listState = ListState.EMPTY),
                onAction = {}
            )
        }
    }
}

@Preview(name = "GoalsScreen — loading, light")
@Composable
private fun GoalsScreenLoadingPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalsContent(
                state = GoalsState(goalList = emptyList(), listState = ListState.LOADING),
                onAction = {}
            )
        }
    }
}
