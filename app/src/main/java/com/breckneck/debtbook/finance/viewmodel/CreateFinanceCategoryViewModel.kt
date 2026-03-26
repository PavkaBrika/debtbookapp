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
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class CreateFinanceCategoryState(
    val categoryName: String = "",
    val selectedImageIndex: Int? = null,
    val selectedImage: Int? = null,
    val selectedColorIndex: Int? = null,
    val selectedColor: String? = null,
    val financeCategoryState: FinanceCategoryState = FinanceCategoryState.EXPENSE,
    val nameError: String? = null,
    val imageError: String? = null,
    val colorError: String? = null,
)

sealed class CreateFinanceCategorySideEffect {
    data object CategorySaved : CreateFinanceCategorySideEffect()
}

@HiltViewModel
class CreateFinanceCategoryViewModel @Inject constructor(
    private val setFinanceCategory: SetFinanceCategory
) : ViewModel(), ContainerHost<CreateFinanceCategoryState, CreateFinanceCategorySideEffect> {

    private val TAG = "CreateFinanceCatFragVM"

    override val container = container(CreateFinanceCategoryState())

    init {
        Log.e(TAG, "Initialized")
    }

    fun onNameChange(value: String) = intent {
        if (value.length <= 20) {
            reduce {
                state.copy(
                    categoryName = value,
                    nameError = if (value.trim().isNotEmpty()) null else state.nameError
                )
            }
        }
    }

    fun onImageSelected(index: Int, image: Int) = intent {
        reduce {
            state.copy(
                selectedImageIndex = index,
                selectedImage = image,
                imageError = null
            )
        }
    }

    fun onColorSelected(index: Int, color: String) = intent {
        reduce {
            state.copy(
                selectedColorIndex = index,
                selectedColor = color,
                colorError = null
            )
        }
    }

    fun setFinanceCategoryState(financeCategoryState: FinanceCategoryState) = intent {
        reduce { state.copy(financeCategoryState = financeCategoryState) }
    }

    fun onSaveClick(
        mustEnterNameError: String,
        mustSelectImageError: String,
        mustSelectColorError: String
    ) = intent {
        val nameError = if (state.categoryName.trim().isEmpty()) mustEnterNameError else null
        val imageError = if (state.selectedImage == null) mustSelectImageError else null
        val colorError = if (state.selectedColor == null) mustSelectColorError else null

        if (nameError != null || imageError != null || colorError != null) {
            reduce {
                state.copy(
                    nameError = nameError,
                    imageError = imageError,
                    colorError = colorError
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
