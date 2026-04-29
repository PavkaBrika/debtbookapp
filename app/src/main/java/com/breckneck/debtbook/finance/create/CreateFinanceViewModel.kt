package com.breckneck.debtbook.finance.create

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.R
import com.breckneck.debtbook.finance.create.mapper.toCreateFinanceUi
import com.breckneck.debtbook.finance.create.model.CreateFinanceUi
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.Finance.DeleteAllFinancesByCategoryId
import com.breckneck.deptbook.domain.usecase.Finance.SetFinance
import com.breckneck.deptbook.domain.usecase.Finance.UpdateFinance
import com.breckneck.deptbook.domain.usecase.FinanceCategory.DeleteFinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetFinanceCategoriesByState
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateFinanceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val setFinanceUseCase: SetFinance,
    private val updateFinanceUseCase: UpdateFinance,
    private val getFinanceCategoriesByStateUseCase: GetFinanceCategoriesByState,
    private val deleteFinanceCategoryUseCase: DeleteFinanceCategory,
    private val deleteAllFinancesByCategoryIdUseCase: DeleteAllFinancesByCategoryId,
    private val getFinanceCurrencyUseCase: GetFinanceCurrency,
) : ViewModel(), ContainerHost<CreateFinanceState, CreateFinanceSideEffect> {

    private val TAG = "CreateFinanceViewModel"

    override val container = container<CreateFinanceState, CreateFinanceSideEffect>(
        initialState = CreateFinanceState.initial(),
        onCreate = { initScreen() },
    )

    init {
        Log.d(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Cleared")
    }

    fun onAction(action: CreateFinanceAction) = when (action) {
        is CreateFinanceAction.SumChanged -> onSumChanged(action.value)
        is CreateFinanceAction.InfoChanged -> onInfoChanged(action.value)
        is CreateFinanceAction.CategoryClick -> onCategoryClick(action.category)
        is CreateFinanceAction.CategoryLongClick -> onCategoryLongClick(action.category)
        CreateFinanceAction.SwitchCategoryState -> onSwitchCategoryState()
        CreateFinanceAction.DateClick -> onDateClick()
        is CreateFinanceAction.DateSelected -> onDateSelected(action.date)
        CreateFinanceAction.DismissDatePicker -> onDismissDatePicker()
        CreateFinanceAction.SaveClick -> onSaveClick()
        CreateFinanceAction.ConfirmDeleteCategory -> onConfirmDeleteCategory()
        CreateFinanceAction.DismissDeleteDialog -> onDismissDeleteDialog()
        CreateFinanceAction.AddCategoryClick -> onAddCategoryClick()
        is CreateFinanceAction.RefreshCategoriesAfterAdd -> onRefreshCategoriesAfterAdd(
            newState = action.newState,
            wasModified = action.wasModified,
        )
    }

    private fun initScreen() = intent {
        val isEditFinance = savedStateHandle.get<Boolean>("isEditFinance") ?: false
        val categoryStateArg = savedStateHandle.get<String>("categoryState").orEmpty()
        val financeCategoryState = when (categoryStateArg) {
            FinanceCategoryState.INCOME.name -> FinanceCategoryState.INCOME
            else -> FinanceCategoryState.EXPENSE
        }
        val currencySymbol = getFinanceCurrencyUseCase.execute()
        val currencyDisplayName = currencyDisplayNameFor(currencySymbol)

        if (isEditFinance) {
            val financeEdit = readFinanceEditFromHandle()
            if (financeEdit == null) {
                postSideEffect(CreateFinanceSideEffect.NavigateBack())
                return@intent
            }
            reduce {
                state.copy(
                    createFragmentState = CreateFragmentState.EDIT,
                    financeCategoryState = financeCategoryState,
                    financeEdit = financeEdit,
                    form = financeEdit.toCreateFinanceUi(
                        currency = currencySymbol,
                        currencyDisplayName = currencyDisplayName,
                    ),
                )
            }
        } else {
            val dayInMillis = savedStateHandle.get<Long>("dayInMillis") ?: System.currentTimeMillis()
            val date = buildDateWithCurrentTime(dayInMillis)
            reduce {
                state.copy(
                    createFragmentState = CreateFragmentState.CREATE,
                    financeCategoryState = financeCategoryState,
                    form = CreateFinanceUi(
                        date = date,
                        dayInMillis = date.time,
                        currency = currencySymbol,
                        currencyDisplayName = currencyDisplayName,
                    ),
                )
            }
            loadCategories(financeCategoryState)
        }
    }

    private fun loadCategories(financeCategoryState: FinanceCategoryState) = intent {
        try {
            val categories =
                getFinanceCategoriesByStateUseCase.execute(financeCategoryState = financeCategoryState)
            reduce {
                state.copy(
                    financeCategoryList = categories.map {
                        it.copy(isChecked = it.id == state.checkedCategoryId)
                    },
                )
            }
            Log.d(TAG, "Categories loaded: ${categories.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Load categories error: ${e.message}")
        }
    }

    private fun onSumChanged(value: String) = intent {
        if (!isValidDecimalInput(value)) return@intent
        reduce {
            state.copy(
                form = state.form.copy(sum = value),
                sumError = if (value.isNotEmpty()) null else state.sumError,
            )
        }
    }

    private fun onInfoChanged(value: String) = intent {
        reduce { state.copy(form = state.form.copy(info = value)) }
    }

    private fun onCategoryClick(category: FinanceCategory) = intent {
        reduce {
            state.copy(
                financeCategoryList = state.financeCategoryList.map {
                    it.copy(isChecked = it.id == category.id)
                },
                checkedCategoryId = category.id,
                categoryError = false,
            )
        }
    }

    private fun onCategoryLongClick(category: FinanceCategory) = intent {
        reduce {
            state.copy(
                deleteCategoryDialog = DeleteCategoryDialogState(isVisible = true, category = category),
            )
        }
    }

    private fun onSwitchCategoryState() = intent {
        val newState = when (state.financeCategoryState) {
            FinanceCategoryState.EXPENSE -> FinanceCategoryState.INCOME
            FinanceCategoryState.INCOME -> FinanceCategoryState.EXPENSE
        }
        reduce {
            state.copy(
                financeCategoryState = newState,
                checkedCategoryId = null,
                categoryError = false,
            )
        }
        postSideEffect(CreateFinanceSideEffect.Vibrate)
        loadCategories(newState)
    }

    private fun onDateClick() = intent {
        reduce { state.copy(isDatePickerVisible = true) }
    }

    private fun onDateSelected(date: Date) = intent {
        reduce {
            state.copy(
                form = state.form.copy(date = date, dayInMillis = date.time),
                isDatePickerVisible = false,
            )
        }
    }

    private fun onDismissDatePicker() = intent {
        reduce { state.copy(isDatePickerVisible = false) }
    }

    private fun onSaveClick() = intent {
        val sumText = state.form.sum.trim().replace(" ", "")
        val (sumDouble, sumError) = CreateFinanceFormValidation.validateSum(sumText)
        val categoryMissing = state.createFragmentState == CreateFragmentState.CREATE
            && state.checkedCategoryId == null

        if (sumError != null || categoryMissing) {
            reduce { state.copy(sumError = sumError, categoryError = categoryMissing) }
            return@intent
        }

        try {
            when (state.createFragmentState) {
                CreateFragmentState.CREATE -> {
                    val finance = Finance(
                        sum = sumDouble!!,
                        info = state.form.info.trim().ifEmpty { null },
                        financeCategoryId = state.checkedCategoryId!!,
                        date = state.form.date,
                    )
                    setFinanceUseCase.execute(finance = finance)
                    Log.d(TAG, "Finance added")
                }
                CreateFragmentState.EDIT -> {
                    val finance = Finance(
                        id = state.financeEdit!!.id,
                        sum = sumDouble!!,
                        info = state.form.info.trim().ifEmpty { null },
                        financeCategoryId = state.financeEdit.financeCategoryId,
                        date = state.form.date,
                    )
                    updateFinanceUseCase.execute(finance = finance)
                    Log.d(TAG, "Finance updated")
                }
            }
            postSideEffect(CreateFinanceSideEffect.NavigateBack(saved = true))
        } catch (e: Exception) {
            Log.e(TAG, "Save error: ${e.message}")
        }
    }

    private fun onConfirmDeleteCategory() = intent {
        val category = state.deleteCategoryDialog.category ?: return@intent
        reduce { state.copy(deleteCategoryDialog = DeleteCategoryDialogState.initial()) }
        try {
            deleteFinanceCategoryUseCase.execute(financeCategory = category)
            deleteAllFinancesByCategoryIdUseCase.execute(financeCategoryId = category.id)
            Log.d(TAG, "Category deleted: ${category.id}")
            loadCategories(state.financeCategoryState)
        } catch (e: Exception) {
            Log.e(TAG, "Delete category error: ${e.message}")
        }
    }

    private fun onDismissDeleteDialog() = intent {
        reduce { state.copy(deleteCategoryDialog = DeleteCategoryDialogState.initial()) }
    }

    private fun onAddCategoryClick() = intent {
        postSideEffect(CreateFinanceSideEffect.NavigateToAddCategory(state = state.financeCategoryState))
    }

    private fun onRefreshCategoriesAfterAdd(
        newState: FinanceCategoryState,
        wasModified: Boolean,
    ) = intent {
        if (!wasModified) return@intent
        reduce {
            state.copy(
                financeCategoryState = newState,
                checkedCategoryId = null,
            )
        }
        loadCategories(newState)
    }

    private fun readFinanceEditFromHandle(): Finance? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedStateHandle.get<Finance>("financeEdit")
        } else {
            @Suppress("DEPRECATION")
            savedStateHandle.get<Finance>("financeEdit")
        }
    }

    private fun currencyDisplayNameFor(currency: String): String {
        val names = context.resources.getStringArray(R.array.currencies).toList()
        return names.firstOrNull { it.contains(currency) } ?: currency
    }

    private fun buildDateWithCurrentTime(dayInMillis: Long): Date {
        val calendar = Calendar.getInstance().apply { timeInMillis = dayInMillis }
        val now = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, now.get(Calendar.SECOND))
        return calendar.time
    }

    private fun isValidDecimalInput(value: String): Boolean {
        if (value.isEmpty()) return true
        val dotIndex = value.indexOf('.')
        if (dotIndex == -1) return true
        val beforeDot = value.substring(0, dotIndex)
        val afterDot = value.substring(dotIndex + 1)
        return beforeDot.isNotEmpty() && afterDot.length <= 2
    }
}
