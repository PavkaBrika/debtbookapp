package com.breckneck.debtbook.finance.details.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.common.format
import com.breckneck.debtbook.common.toDMYFormat
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.DeleteFinance
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryId
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FinanceDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFinanceByCategoryIdUseCase: GetFinanceByCategoryId,
    private val deleteFinanceUseCase: DeleteFinance,
) : ViewModel(), ContainerHost<FinanceDetailsState, FinanceDetailsSideEffect> {

    private val TAG = "FinanceDetailsViewModel"

    override val container = container<FinanceDetailsState, FinanceDetailsSideEffect>(
        initialState = FinanceDetailsState.initial(),
        onCreate = {
            val categoryId = savedStateHandle.get<Int>("categoryId") ?: 0
            val categoryState = savedStateHandle.get<String>("categoryState").orEmpty()
            val isExpenses = categoryState == FinanceCategoryState.EXPENSE.name
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
        is FinanceDetailsActions.DeleteFinance -> deleteFinance()
        FinanceDetailsActions.EditFinanceClick -> editFinance()
        is FinanceDetailsActions.RefreshListAfterEdit ->
            refreshListAfterEdit(wasModified = action.wasModified)
    }

    private fun openFinanceSheet(finance: Finance) = intent {
        val title = "${finance.date.toDMYFormat()} : ${finance.sum.format()} ${state.currency}"
        reduce {
            state.copy(
                bottomSheet = state.bottomSheet.copy(
                    isOpened = true,
                    finance = finance,
                    title = title,
                )
            )
        }
    }

    private fun closeFinanceSheet() = intent {
        reduce { state.copy(bottomSheet = FinanceDetailsBottomSheetState.initial()) }
    }

    private fun editFinance() = intent {
        val finance = state.bottomSheet.finance ?: return@intent
        postSideEffect(FinanceDetailsSideEffect.NavigateToEditFinance(finance = finance))
        reduce { state.copy(bottomSheet = FinanceDetailsBottomSheetState.initial()) }
    }

    private fun deleteFinance() = intent {
        val finance = state.bottomSheet.finance ?: return@intent
        deleteFinanceUseCase.execute(finance)
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
