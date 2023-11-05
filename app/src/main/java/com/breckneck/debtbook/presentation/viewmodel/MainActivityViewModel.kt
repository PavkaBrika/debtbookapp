package com.breckneck.debtbook.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivityViewModel(private val getDebtQuantity: GetDebtQuantity) : ViewModel() {

    var resultIsAppRateDialogShow = MutableLiveData<Boolean>()
    var resultIsAppReviewDialogShow = MutableLiveData<Boolean>()
    var resultIsAppReviewDialogFromSettings = MutableLiveData<Boolean>()
    var resultDebtQuantity = MutableLiveData<Int>()
    var resultAppRate = MutableLiveData<Int>()
    var resultAppReviewText = MutableLiveData<String>()
    var resultIsInAppReviewTimerEnds = MutableLiveData<Boolean>()

    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null
    private var isInAppReviewInitCalled = false

    private val disposeBag = CompositeDisposable()

    init {
        Log.e("TAG", "Main Activity View Model Started")
        resultIsInAppReviewTimerEnds.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e("TAG", "Main Activity View Model cleared")
    }

    fun getDebtQuantity() {
        val result = Single.create {
            it.onSuccess(getDebtQuantity.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultDebtQuantity.value = it
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun setAppRateDialogShown(shown: Boolean) {
        resultIsAppRateDialogShow.value = shown
    }

    fun setAppReviewDialogShown(shown: Boolean) {
        resultIsAppReviewDialogShow.value = shown
    }

    fun setAppReviewFromSettings(fromSettings: Boolean) {
        resultIsAppReviewDialogFromSettings.value = fromSettings
    }

    fun setAppRate(rate: Int) {
        resultAppRate.value = rate
    }

    fun setAppReviewText(text: String) {
        resultAppReviewText.value = text
    }

    fun initInAppReview(context: Context) {
        if (isInAppReviewInitCalled) return
        reviewManager = ReviewManagerFactory.create(context)
        Log.e("TAG", "Init App Review")
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { task ->
            if (task.isSuccessful)
                reviewInfo = task.result
            else
                Log.e("TAG", "In App Review error")
        }
        isInAppReviewInitCalled = true
    }

    fun startInAppReviewWithTimer() {
        if (!resultIsInAppReviewTimerEnds.value!!) {
            object : CountDownTimer(10000, 1000) {
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {
                    Log.e("TAG", "timer finished")
                    resultIsInAppReviewTimerEnds.value = true
                }
            }.start()
        }
    }

    fun launchInAppReview(activity: Activity) {
        if (reviewInfo != null) {
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo!!)
            flow.addOnSuccessListener {
                Log.e("TAG", "review success")
            }
        }
    }
}