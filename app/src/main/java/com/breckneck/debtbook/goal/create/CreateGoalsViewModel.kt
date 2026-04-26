package com.breckneck.debtbook.goal.create

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.R
import com.breckneck.debtbook.goal.create.mapper.toCreateGoalUi
import com.breckneck.debtbook.goal.create.mapper.toDomain
import com.breckneck.debtbook.goal.create.model.NameError
import com.breckneck.debtbook.goal.create.model.SavedSumError
import com.breckneck.debtbook.goal.create.model.SumError
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateGoalsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setGoal: SetGoal,
    private val updateGoal: UpdateGoal,
    private val getDefaultCurrency: GetDefaultCurrency,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ContainerHost<CreateGoalsState, CreateGoalsSideEffect> {

    private val TAG = "CreateGoalsViewModel"

    private var originalGoal: Goal? = null

    override val container = container<CreateGoalsState, CreateGoalsSideEffect>(
        initialState = CreateGoalsState.initial(),
        onCreate = {
            val names = context.resources.getStringArray(R.array.currencies).toList()
            val isEditMode: Boolean = savedStateHandle.get<Boolean>("isEditGoal") ?: false

            reduce { state.copy(currencyNames = names, isEditMode = isEditMode) }

            if (isEditMode) initEditMode() else initCreateMode()
        }
    )

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun onAction(action: CreateGoalsAction) = when (action) {
        is CreateGoalsAction.NameChanged -> onNameChanged(action.value)
        is CreateGoalsAction.SumChanged -> onSumChanged(action.value)
        is CreateGoalsAction.SavedSumChanged -> onSavedSumChanged(action.value)
        CreateGoalsAction.CurrencyClick -> onCurrencyClick()
        is CreateGoalsAction.CurrencySelected -> onCurrencySelected(action.index)
        CreateGoalsAction.DismissCurrencySheet -> onDismissCurrencySheet()
        CreateGoalsAction.DateClick -> onDateClick()
        CreateGoalsAction.DismissDatePicker -> onDismissDatePicker()
        is CreateGoalsAction.DateSelected -> onDateSelected(action.date)
        is CreateGoalsAction.ImagePicked -> onImagePicked(action.uri)
        CreateGoalsAction.DeleteImage -> onDeleteImage()
        CreateGoalsAction.PhotoCardClick -> onPhotoCardClick()
        CreateGoalsAction.SaveClick -> onSaveClick()
        CreateGoalsAction.BackClick -> onBackClick()
    }

    private fun initEditMode() = intent {
        val goal = savedStateHandle.get<Goal>("goal") ?: run {
            postSideEffect(CreateGoalsSideEffect.NavigateBack())
            return@intent
        }

        originalGoal = goal
        reduce {
            state.copy(
                goal = goal.toCreateGoalUi(currencyDisplayNameFor(goal.currency)),
                selectedCurrencyIndex = currencyIndexOf(goal.currency),
            )
        }
    }

    private fun initCreateMode() = intent {
        val defaultCurrency = getDefaultCurrency.execute()
        reduce {
            state.copy(
                goal = state.goal.copy(
                    currency = defaultCurrency,
                    currencyDisplayName = currencyDisplayNameFor(defaultCurrency),
                ),
                selectedCurrencyIndex = currencyIndexOf(defaultCurrency),
            )
        }
    }

    private fun onNameChanged(value: String) = intent {
        reduce { state.copy(goal = state.goal.copy(name = value), nameError = null) }
    }

    private fun onSumChanged(value: String) = intent {
        if (isValidDecimalInput(value)) {
            reduce { state.copy(goal = state.goal.copy(sum = value), sumError = null) }
        }
    }

    private fun onSavedSumChanged(value: String) = intent {
        if (isValidDecimalInput(value)) {
            reduce { state.copy(goal = state.goal.copy(savedSum = value), savedSumError = null) }
        }
    }

    private fun onCurrencyClick() = intent {
        reduce { state.copy(isCurrencySheetVisible = true) }
    }

    private fun onCurrencySelected(index: Int) = intent {
        val displayName = state.currencyNames.getOrNull(index) ?: return@intent
        val symbol = displayName.substringAfterLast(" ")
        reduce {
            state.copy(
                goal = state.goal.copy(
                    currency = symbol,
                    currencyDisplayName = displayName,
                ),
                selectedCurrencyIndex = index,
                isCurrencySheetVisible = false,
            )
        }
    }

    private fun onDismissCurrencySheet() = intent {
        reduce { state.copy(isCurrencySheetVisible = false) }
    }

    private fun onDateClick() = intent {
        reduce { state.copy(isDatePickerVisible = true) }
    }

    private fun onDismissDatePicker() = intent {
        reduce { state.copy(isDatePickerVisible = false) }
    }

    private fun onDateSelected(date: Date) = intent {
        reduce {
            state.copy(
                goal = state.goal.copy(goalDate = date),
                isDatePickerVisible = false,
            )
        }
    }

    private fun onImagePicked(uri: Uri) = intent {
        reduce {
            state.copy(
                goal = state.goal.copy(
                    imageUri = uri,
                    imagePath = null,
                ),
            )
        }
    }

    private fun onDeleteImage() = intent {
        reduce {
            state.copy(
                goal = state.goal.copy(
                    imageUri = null,
                    imagePath = null,
                ),
            )
        }
    }

    private fun onPhotoCardClick() = intent {
        postSideEffect(CreateGoalsSideEffect.LaunchImagePicker)
    }

    private fun onSaveClick() = intent {
        val goal = state.goal
        val name = goal.name.trim()
        val sumText = goal.sum.trim().replace(" ", "")
        val savedSumText = goal.savedSum.trim().replace(" ", "")

        val nameError = validateName(name)
        val (sumDouble, sumError) = validateSum(sumText)
        val (savedSumDouble, savedSumError) = validateSavedSum(savedSumText, sumDouble)

        if (nameError != null || sumError != null || savedSumError != null) {
            reduce {
                state.copy(
                    nameError = nameError,
                    sumError = sumError,
                    savedSumError = savedSumError
                )
            }
            return@intent
        }

        reduce { state.copy(nameError = null, sumError = null, savedSumError = null) }

        if (state.isEditMode) {
            saveEdit(sumDouble!!, savedSumDouble ?: 0.0)
        } else {
            saveCreate(sumDouble!!, savedSumDouble ?: 0.0)
        }
    }

    private fun saveCreate(sum: Double, savedSum: Double) = intent {
        val photoPath = savePhotoToInternalStorage(state.goal.imageUri)
        val newGoal = state.goal.toDomain(sum = sum, savedSum = savedSum, photoPath = photoPath)
        try {
            withContext(Dispatchers.IO) { setGoal.execute(goal = newGoal) }
            Log.e(TAG, "Goal added")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        postSideEffect(CreateGoalsSideEffect.NavigateBack(saved = true))
    }

    private fun saveEdit(sum: Double, savedSum: Double) = intent {
        val goal = state.goal
        val original = originalGoal ?: return@intent
        val photoPath = if (goal.imagePath != null) original.photoPath
        else savePhotoToInternalStorage(goal.imageUri)
        val editedGoal = goal.toDomain(
            sum = sum,
            savedSum = savedSum,
            photoPath = photoPath,
            originalGoal = original,
        )
        try {
            withContext(Dispatchers.IO) { updateGoal.execute(goal = editedGoal) }
            Log.e(TAG, "Goal updated")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        postSideEffect(CreateGoalsSideEffect.NavigateBack(editedGoal = editedGoal, saved = true))
    }

    private fun validateName(name: String): NameError? =
        if (name.isEmpty()) NameError.EMPTY else null

    private fun validateSum(sumText: String): Pair<Double?, SumError?> {
        val sumDouble = sumText.toDoubleOrNull()
        val error = when {
            sumText.isEmpty() || sumDouble == null -> SumError.INVALID
            sumDouble < 0 -> SumError.NEGATIVE
            sumDouble == 0.0 -> SumError.ZERO
            else -> null
        }
        return Pair(sumDouble, error)
    }

    private fun validateSavedSum(
        savedSumText: String,
        sumDouble: Double?
    ): Pair<Double?, SavedSumError?> {
        if (savedSumText.isEmpty()) return Pair(0.0, null)
        val parsed = savedSumText.toDoubleOrNull() ?: return Pair(0.0, SavedSumError.INVALID)
        if (sumDouble != null && parsed >= sumDouble) return Pair(
            0.0,
            SavedSumError.GREATER_THAN_SUM
        )
        return Pair(parsed, null)
    }

    private fun onBackClick() = intent {
        postSideEffect(CreateGoalsSideEffect.NavigateBack())
    }

    private fun savePhotoToInternalStorage(uri: Uri?): String? {
        if (uri == null) return null
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "${uri.pathSegments.last()}.jpg"
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            "${context.filesDir.absolutePath}/$fileName"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save photo: ${e.message}")
            null
        }
    }

    private fun currencyDisplayNameFor(currency: String): String =
        //todo fix this - remove currencyNames. Store currency in ENUM.
        container.stateFlow.value.currencyNames.firstOrNull { it.contains(currency) } ?: currency

    private fun currencyIndexOf(currency: String): Int =
        //todo fix this - remove currencyNames. Store currency in ENUM.
        container.stateFlow.value.currencyNames.indexOfFirst { it.contains(currency) }
            .coerceAtLeast(0)

    private fun isValidDecimalInput(value: String): Boolean {
        if (value.isEmpty()) return true
        val dotIndex = value.indexOf('.')
        if (dotIndex == -1) return true
        val afterDot = value.substring(dotIndex + 1)
        val beforeDot = value.substring(0, dotIndex)
        return beforeDot.isNotEmpty() && afterDot.length <= 2
    }
}
