package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.util.ListState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GoalsFragmentViewModel(
    private val getAllGoals: GetAllGoals
) : ViewModel() {

    private val TAG = "GoalsFragmentVM"

    private val _goalList = MutableLiveData<List<Goal>>()
    val goalList: LiveData<List<Goal>>
        get() = _goalList
    private val _goalListState = MutableLiveData<ListState>(ListState.LOADING)
    val goalListState: LiveData<ListState>
        get() = _goalListState

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "initialized")
    }

    fun getAllGoals() {
        val result = Single.create {
            it.onSuccess(getAllGoals.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _goalListState.value = ListState.LOADING
            }
            .delay(400, TimeUnit.MILLISECONDS)
            .subscribe({
                _goalList.value = it
                if (_goalList.value!!.isEmpty())
                    _goalListState.value = ListState.EMPTY
                else
                    _goalListState.value = ListState.FILLED
            }, {
                Log.e(TAG, it.message.toString())
            })
    }

    override fun onCleared() {
        super.onCleared()
    }
}