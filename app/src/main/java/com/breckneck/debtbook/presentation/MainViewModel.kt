package com.breckneck.debtbook.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.HumanDomain

class MainViewModel : ViewModel() {

    init {
        Log.e("TAG", "VM created")
    }

    override fun onCleared() {
        Log.e("TAG", "VM cleared")
        super.onCleared()
    }

//    fun loadAllHumans() : List<HumanDomain> {
////        return getAllHumansUseCase.execute()
//    }
}