package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.common.empty
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
    val categoryName: String,
    val currency: String,
    val isExpenses: Boolean,
) {
    companion object {
        fun initial() = FinanceDetailsState(
            financeList = emptyList(),
            financeListState = ListState.LOADING,
            isSettingsDialogOpened = false,
            settingsFinance = null,
            categoryId = null,
            categoryName = String.empty,
            currency = String.empty,
            isExpenses = false,
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
            val categoryName = savedStateHandle.get<String>("categoryName").orEmpty()
            val currency = savedStateHandle.get<String>("currency").orEmpty()
            reduce {
                state.copy(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    currency = currency,
                    isExpenses = isExpenses,
                    financeListState = ListState.LOADING,
                )
            }

            loadFinances()
        },
    )

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun onAction(action: FinanceDetailsActions) = when (action) {
        is FinanceDetailsActions.OpenFinanceSheet -> openFinanceSheet(finance = action.finance)
        FinanceDetailsActions.CloseFinanceSheet -> closeFinanceSheet()
        is FinanceDetailsActions.DeleteFinance -> deleteFinance(finance = action.finance)
        is FinanceDetailsActions.RefreshListAfterEdit ->
            refreshListAfterEdit(wasModified = action.wasModified)
    }

    private fun openFinanceSheet(finance: Finance) = intent {
        reduce {
            state.copy(
                isSettingsDialogOpened = true,
                settingsFinance = finance,
            )
        }
    }

    private fun closeFinanceSheet() = intent {
        reduce { state.copy(isSettingsDialogOpened = false) }
    }

    private fun deleteFinance(finance: Finance) = intent {
        deleteFinanceUseCase.execute(finance = finance)
        Log.e(TAG, "Finance delete success")
        loadFinances()
    }

    private fun refreshListAfterEdit(wasModified: Boolean) = intent {
        if (!wasModified) return@intent
        loadFinances()
    }

    private fun loadFinances() = intent {
        val categoryId = state.categoryId ?: return@intent

        reduce { state.copy(financeListState = ListState.LOADING) }

        val finances = getFinanceByCategoryIdUseCase.execute(categoryId = categoryId)
        reduce {
            state.copy(
                financeList = finances,
                financeListState = if (finances.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
            )
        }
        Log.e(TAG, "Finances load success")
    }
}
