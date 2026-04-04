package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.SetFinanceCategory
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class CreateFinanceCategoryState(
    val categoryName: String,
    val selectedImage: Int?,
    val selectedColor: String?,
    val financeCategoryState: FinanceCategoryState,
    val isNameErrorVisible: Boolean,
    val isImageErrorVisible: Boolean,
    val isColorErrorVisible: Boolean,
) {
    companion object {
        fun initial() = CreateFinanceCategoryState(
            categoryName = "",
            selectedImage = null,
            selectedColor = null,
            financeCategoryState = FinanceCategoryState.EXPENSE,
            isNameErrorVisible = false,
            isImageErrorVisible = false,
            isColorErrorVisible = false,
        )
    }
}

sealed class CreateFinanceCategorySideEffect {
    data object CategorySaved : CreateFinanceCategorySideEffect()
}

@HiltViewModel
class CreateFinanceCategoryViewModel @Inject constructor(
    private val setFinanceCategory: SetFinanceCategory
) : ViewModel(), ContainerHost<CreateFinanceCategoryState, CreateFinanceCategorySideEffect> {

    private val TAG = "CreateFinanceCatFragVM"

    override val container = container<CreateFinanceCategoryState, CreateFinanceCategorySideEffect>(
        CreateFinanceCategoryState.initial()
    )

    init {
        Log.e(TAG, "Initialized")
    }

    fun onNameChange(value: String) = intent {
        if (value.length <= 20) {
            reduce {
                state.copy(
                    categoryName = value,
                    isNameErrorVisible = if (value.trim().isNotEmpty()) false else state.isNameErrorVisible
                )
            }
        }
    }

    fun onImageSelected(image: Int) = intent {
        reduce {
            state.copy(
                selectedImage = image,
                isImageErrorVisible = false
            )
        }
    }

    fun onColorSelected(color: String) = intent {
        reduce {
            state.copy(
                selectedColor = color,
                isColorErrorVisible = false
            )
        }
    }

    fun setFinanceCategoryState(financeCategoryState: FinanceCategoryState) = intent {
        reduce { state.copy(financeCategoryState = financeCategoryState) }
    }

    fun onSaveClick() = intent {
        val nameInvalid = state.categoryName.trim().isEmpty()
        val imageInvalid = state.selectedImage == null
        val colorInvalid = state.selectedColor == null

        if (nameInvalid || imageInvalid || colorInvalid) {
            reduce {
                state.copy(
                    isNameErrorVisible = nameInvalid,
                    isImageErrorVisible = imageInvalid,
                    isColorErrorVisible = colorInvalid
                )
            }
            return@intent
        }

        try {
            withContext(Dispatchers.IO) {
                setFinanceCategory.execute(
                    financeCategory = FinanceCategory(
                        name = state.categoryName.trim(),
                        color = state.selectedColor!!,
                        state = state.financeCategoryState,
                        image = state.selectedImage!!
                    )
                )
            }
            Log.e(TAG, "New category added")
            postSideEffect(CreateFinanceCategorySideEffect.CategorySaved)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }
}
