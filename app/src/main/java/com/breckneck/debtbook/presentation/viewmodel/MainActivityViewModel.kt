package com.breckneck.debtbook.presentation.viewmodel

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.rustore.sdk.core.tasks.OnCompleteListener
import ru.vk.store.sdk.review.RuStoreReviewManager
import ru.vk.store.sdk.review.RuStoreReviewManagerFactory
import ru.vk.store.sdk.review.model.ReviewInfo

class MainActivityViewModel(private val getDebtQuantity: GetDebtQuantity) : ViewModel() {

    private val TAG = "MainActivityViewModel"

    var resultIsAppRateDialogShow = MutableLiveData<Boolean>()
    var resultIsAppReviewDialogShow = MutableLiveData<Boolean>()
    var resultIsAppReviewDialogFromSettings = MutableLiveData<Boolean>()
    var resultDebtQuantity = MutableLiveData<Int>()
    var resultAppRate = MutableLiveData<Int>()
    var resultAppReviewText = MutableLiveData<String>()
    var resultIsInAppReviewTimerEnds = MutableLiveData<Boolean>()

    private lateinit var reviewManager: RuStoreReviewManager
    private var reviewInfo: ReviewInfo? = null
    private var isInAppReviewInitCalled = false

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Main Activity View Model Started")
        resultIsInAppReviewTimerEnds.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Main Activity View Model cleared")
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
                Log.e(TAG, it.stackTrace.toString())
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
        reviewManager = RuStoreReviewManagerFactory.create(context)
        Log.e("TAG", "Init App Review")
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener(object : OnCompleteListener<ReviewInfo> {
            override fun onFailure(throwable: Throwable) {
                Log.e("TAG", "In app review error $throwable")
                if (throwable.toString().contains("Application is not verified yet"))
                    Toast.makeText(context, "${context.getText(R.string.error_code)} 100", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: ReviewInfo) {
                reviewInfo = result
            }
        })
        isInAppReviewInitCalled = true
    }

    fun startInAppReviewWithTimer() {
        if (!resultIsInAppReviewTimerEnds.value!!) {
            object : CountDownTimer(10000, 1000) {
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {
                    Log.e(TAG, "timer finished")
                    resultIsInAppReviewTimerEnds.value = true
                }
            }.start()
        }
    }

    fun launchInAppReview() {
        if (reviewInfo != null) {
            val flow = reviewManager.launchReviewFlow(reviewInfo!!)
            flow.addOnSuccessListener {
                Log.e(TAG, "review success")
            }
        }
    }
}