package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

class GoalDetailsFragmentViewModel: ViewModel() {

    private val TAG = "GoalDetailsFragmentVM"

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }
}