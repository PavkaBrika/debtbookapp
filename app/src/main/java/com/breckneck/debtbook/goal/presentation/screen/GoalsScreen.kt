package com.breckneck.debtbook.goal.presentation.screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.breckneck.debtbook.R
import com.breckneck.debtbook.core.ui.components.EmptyListPlaceholder
import com.breckneck.debtbook.core.ui.components.ShimmerListPlaceholder
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.goal.viewmodel.GoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.util.ListState
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    vm: GoalsFragmentViewModel,
    onAddGoalClick: () -> Unit,
    onGoalClick: (Goal) -> Unit
) {
    val goalList by vm.goalList.observeAsState(emptyList())
    val goalListState by vm.goalListState.observeAsState(ListState.LOADING)

    var selectedGoalForDeposit by rememberSaveable { mutableStateOf<Goal?>(null) }

    GoalsContent(
        goalList = goalList,
        goalListState = goalListState,
        onAddGoalClick = onAddGoalClick,
        onGoalClick = onGoalClick,
        onAddSumClick = { goal -> selectedGoalForDeposit = goal },
        onDeleteClick = { goal -> vm.deleteGoal(goal) }
    )

    if (selectedGoalForDeposit != null) {
        val goal = selectedGoalForDeposit!!
        AddGoalSumBottomSheet(
            onConfirm = { sum ->
                val updatedGoal = goal.copy(savedSum = goal.savedSum + sum)
                vm.updateGoal(updatedGoal)
                vm.setGoalDeposit(GoalDeposit(sum = sum, date = Date(), goalId = goal.id))
                vm.updateGoalInList(updatedGoal)
                selectedGoalForDeposit = null
            },
            onDismiss = { selectedGoalForDeposit = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsContent(
    goalList: List<Goal>,
    goalListState: ListState,
    onAddGoalClick: () -> Unit,
    onGoalClick: (Goal) -> Unit,
    onAddSumClick: (Goal) -> Unit,
    onDeleteClick: (Goal) -> Unit
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
            FloatingActionButton(onClick = onAddGoalClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_goal)
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Crossfade(
            targetState = goalListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "goals_state"
        ) { state ->
            when (state) {
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
                            items = goalList,
                            key = { it.id }
                        ) { goal ->
                            GoalItem(
                                goal = goal,
                                onGoalClick = onGoalClick,
                                onAddClick = onAddSumClick,
                                onDeleteClick = onDeleteClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "GoalsScreen — list, light")
@Preview(name = "GoalsScreen — list, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GoalsScreenListPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            GoalsContent(
                goalList = listOf(
                    Goal(
                        id = 1,
                        name = "New Car",
                        sum = 5000.0,
                        savedSum = 1200.0,
                        currency = "USD",
                        photoPath = null,
                        creationDate = Date(),
                        goalDate = Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000)
                    ),
                    Goal(
                        id = 2,
                        name = "Dream Vacation",
                        sum = 2000.0,
                        savedSum = 2000.0,
                        currency = "EUR",
                        photoPath = null,
                        creationDate = Date(System.currentTimeMillis() - 45L * 24 * 3600 * 1000),
                        goalDate = null
                    )
                ),
                goalListState = ListState.RECEIVED,
                onAddGoalClick = {},
                onGoalClick = {},
                onAddSumClick = {},
                onDeleteClick = {}
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
                goalList = emptyList(),
                goalListState = ListState.EMPTY,
                onAddGoalClick = {},
                onGoalClick = {},
                onAddSumClick = {},
                onDeleteClick = {}
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
                goalList = emptyList(),
                goalListState = ListState.LOADING,
                onAddGoalClick = {},
                onGoalClick = {},
                onAddSumClick = {},
                onDeleteClick = {}
            )
        }
    }
}
