package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateFinanceCategoryFragmentViewModel: ViewModel() {

    val TAG = "CreateFinanceCatFragVM"

    private val _checkedImage = MutableLiveData<Int>()
    val checkedImage: LiveData<Int>
        get() = _checkedImage
    private val _checkedImagePosition = MutableLiveData<Int>()
    val checkedImagePosition: LiveData<Int>
        get() = _checkedImagePosition
    private val _checkedColor = MutableLiveData<String>()
    val checkedColor: LiveData<String>
        get() = _checkedColor
    private val _checkedColorPosition = MutableLiveData<Int>()
    val checkedColorPosition: LiveData<Int>
        get() = _checkedColorPosition

    init {
        Log.e(TAG, "Initialized")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun setCheckedImage(image: Int) {
        _checkedImage.value = image
    }

    fun setCheckedImagePosition(position: Int) {
        _checkedImagePosition.value = position
    }

    fun setCheckedColor(color: String) {
        _checkedColor.value = color
    }

    fun setCheckedColorPosition(position: Int) {
        _checkedColorPosition.value = position
    }
}