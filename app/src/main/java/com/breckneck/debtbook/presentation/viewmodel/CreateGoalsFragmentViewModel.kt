package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class CreateGoalsFragmentViewModel: ViewModel() {

    private val TAG = "CreateGoalsFragmentVM"

    init {
        Log.e(TAG, "initialized")
    }

    override fun onCleared() {
        super.onCleared()
    }
}