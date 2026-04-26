package com.breckneck.debtbook.goal.create

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.breckneck.debtbook.R
import com.breckneck.debtbook.goal.create.model.CreateGoalUi
import com.breckneck.debtbook.goal.create.model.NameError
import com.breckneck.debtbook.goal.create.model.SumError
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.orbitmvi.orbit.test.TestSettings
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGoalsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun mockContext(): Context {
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        `when`(context.resources).thenReturn(resources)
        `when`(resources.getStringArray(R.array.currencies)).thenReturn(arrayOf("US Dollar $"))
        `when`(context.contentResolver).thenReturn(mock(ContentResolver::class.java))
        return context
    }

    private fun baseReadyState(): CreateGoalsState =
        CreateGoalsState.initial().copy(
            currencyNames = listOf("US Dollar $"),
            goal = CreateGoalUi(
                name = "Trip",
                sum = "100",
                savedSum = "10",
                currency = "$",
                currencyDisplayName = "US Dollar $",
            ),
            selectedCurrencyIndex = 0,
        )

    private fun viewModel(
        setGoal: SetGoal = mock(SetGoal::class.java),
        updateGoal: UpdateGoal = mock(UpdateGoal::class.java),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): CreateGoalsViewModel {
        val getDefaultCurrency = mock(GetDefaultCurrency::class.java)
        `when`(getDefaultCurrency.execute()).thenReturn("$")
        return CreateGoalsViewModel(
            mockContext(),
            setGoal,
            updateGoal,
            getDefaultCurrency,
            savedStateHandle,
        )
    }

    @Test
    fun `SaveClick with invalid name sets nameError and does not navigate`() = runTest {
        val scope = this
        val setGoal = mock(SetGoal::class.java)
        val vm = viewModel(setGoal = setGoal)
        val state = baseReadyState().copy(goal = baseReadyState().goal.copy(name = "  "))
        vm.test(
            scope,
            initialState = state,
            settings = TestSettings(autoCheckInitialState = false),
        ) {
            scope.advanceUntilIdle()
            expectState(containerHost.container.stateFlow.value)
            vm.onAction(CreateGoalsAction.SaveClick)
            scope.advanceUntilIdle()
            expectState {
                copy(
                    nameError = NameError.EMPTY,
                    sumError = null,
                    savedSumError = null,
                )
            }
        }
    }

    @Test
    fun `SaveClick when valid posts NavigateBack saved`() = runTest {
        val scope = this
        val setGoal = mock(SetGoal::class.java)
        doNothing().`when`(setGoal).execute(any())
        val vm = viewModel(setGoal = setGoal)
        vm.test(
            scope,
            initialState = baseReadyState(),
            settings = TestSettings(autoCheckInitialState = false),
        ) {
            scope.advanceUntilIdle()
            expectState(containerHost.container.stateFlow.value)
            vm.onAction(CreateGoalsAction.SaveClick)
            scope.advanceUntilIdle()
            expectSideEffect(CreateGoalsSideEffect.NavigateBack(saved = true))
        }
    }

    @Test
    fun `SaveClick when SetGoal throws does not navigate`() = runTest {
        val scope = this
        val setGoal = mock(SetGoal::class.java)
        doThrow(RuntimeException("db")).`when`(setGoal).execute(any())
        val vm = viewModel(setGoal = setGoal)
        vm.test(
            scope,
            initialState = baseReadyState(),
            settings = TestSettings(autoCheckInitialState = false),
        ) {
            scope.advanceUntilIdle()
            expectState(containerHost.container.stateFlow.value)
            vm.onAction(CreateGoalsAction.SaveClick)
            scope.advanceUntilIdle()
        }
    }

    @Test
    fun `SaveClick with zero sum sets ZERO error`() = runTest {
        val scope = this
        val vm = viewModel()
        val state = baseReadyState().copy(
            goal = baseReadyState().goal.copy(sum = "0", savedSum = ""),
        )
        vm.test(
            scope,
            initialState = state,
            settings = TestSettings(autoCheckInitialState = false),
        ) {
            scope.advanceUntilIdle()
            expectState(containerHost.container.stateFlow.value)
            vm.onAction(CreateGoalsAction.SaveClick)
            scope.advanceUntilIdle()
            expectState {
                copy(
                    nameError = null,
                    sumError = SumError.ZERO,
                    savedSumError = null,
                )
            }
        }
    }
}
