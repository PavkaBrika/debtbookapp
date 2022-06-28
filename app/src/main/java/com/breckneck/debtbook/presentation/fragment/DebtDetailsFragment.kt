package com.breckneck.debtbook.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.domain.usecase.GetAllDebtsUseCase
import io.reactivex.rxjava3.core.Single

class DebtDetailsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val dataBaseDebtStorage = DataBaseDebtStorageImpl(context = view.context)
        val debtRepository = DebtRepositoryImpl(debtStorage = dataBaseDebtStorage)
        val getAllDebtsUseCase = GetAllDebtsUseCase(debtRepository)

//        Single.just("1")
//            .map { getAllDebtsUseCase.execute() }

        return view
    }
}