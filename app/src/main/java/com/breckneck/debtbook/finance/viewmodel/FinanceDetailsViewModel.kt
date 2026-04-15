package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.DeleteFinance
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryId
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class FinanceDetailsState(
    val financeList: List<Finance>,
    val financeListState: ListState,
    val isSettingsDialogOpened: Boolean,
    val settingsFinance: Finance?,
    val categoryId: Int?,
    val isExpenses: Boolean?,
) {
    companion object {
        fun initial() = FinanceDetailsState(
            financeList = emptyList(),
            financeListState = ListState.LOADING,
            isSettingsDialogOpened = false,
            settingsFinance = null,
            categoryId = null,
            isExpenses = null,
        )
    }
}

@HiltViewModel
class FinanceDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFinanceByCategoryIdUseCase: GetFinanceByCategoryId,
    private val deleteFinanceUseCase: DeleteFinance,
) : ViewModel(), ContainerHost<FinanceDetailsState, Nothing> {

    private val TAG = "FinanceDetailsViewModel"

    override val container = container<FinanceDetailsState, Nothing>(
        initialState = FinanceDetailsState.initial(),
        onCreate = {
            val categoryId = savedStateHandle.get<Int>("categoryId") ?: 0
            val categoryState = savedStateHandle.get<String>("categoryState").orEmpty()
            val isExpenses = categoryState == FinanceCategoryState.EXPENSE.toString()
            reduce {
                state.copy(
                    categoryId = categoryId,
                    isExpenses = isExpenses,
                    financeListState = ListState.LOADING,
                )
            }
            try {
                val result = loadFinances(categoryId = categoryId)
                reduce {
                    state.copy(
                        financeList = result,
                        financeListState = if (result.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                    )
                }
                Log.e(TAG, "Finances load success")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        },
    )

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun onAction(action: FinanceDetailsActions) = intent {
        when (action) {
            is FinanceDetailsActions.OpenFinanceSheet -> reduce {
                state.copy(
                    isSettingsDialogOpened = true,
                    settingsFinance = action.finance,
                )
            }

            FinanceDetailsActions.CloseFinanceSheet -> reduce {
                state.copy(isSettingsDialogOpened = false)
            }

            is FinanceDetailsActions.DeleteFinance -> {
                try {
                    deleteFinanceUseCase.execute(finance = action.finance)
                    val categoryId = state.categoryId ?: return@intent
                    reduce { state.copy(financeListState = ListState.LOADING) }
                    val result = loadFinances(categoryId = categoryId)
                    reduce {
                        state.copy(
                            financeList = result,
                            financeListState = if (result.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                        )
                    }
                    Log.e(TAG, "Finances load success")
                    Log.e(TAG, "Finance delete success")
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }

            is FinanceDetailsActions.RefreshListAfterEdit -> {
                if (!action.wasModified) return@intent
                val categoryId = state.categoryId ?: return@intent
                reduce { state.copy(financeListState = ListState.LOADING) }
                try {
                    val result = loadFinances(categoryId = categoryId)
                    reduce {
                        state.copy(
                            financeList = result,
                            financeListState = if (result.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                        )
                    }
                    Log.e(TAG, "Finances load success")
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }
    }

    private suspend fun loadFinances(categoryId: Int): List<Finance> {
        return getFinanceByCategoryIdUseCase.execute(categoryId = categoryId)
            .sortedByDescending { finance -> finance.date }
    }
}
