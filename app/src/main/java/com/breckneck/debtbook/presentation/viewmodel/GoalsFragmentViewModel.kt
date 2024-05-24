package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class GoalsFragmentViewModel: ViewModel() {

    private val TAG = "GoalsFragmentVM"

    init {
        Log.e(TAG, "initialized")
    }

    override fun onCleared() {
        super.onCleared()
    }
}