package com.breckneck.debtbook.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.DebtAdapter
import com.breckneck.deptbook.data.storage.database.DataBaseDebtStorageImpl
import com.breckneck.deptbook.data.storage.repository.DebtRepositoryImpl
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class DebtDetailsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_debt_details, container, false)

        val dataBaseDebtStorage = DataBaseDebtStorageImpl(context = view.context)
        val debtRepository = DebtRepositoryImpl(debtStorage = dataBaseDebtStorage)
        val getAllDebtsUseCase = GetAllDebtsUseCase(debtRepository)

        val recyclerView: RecyclerView = view.findViewById(R.id.debtsRecyclerView)

        val idHuman = arguments?.getInt("idHuman", 0)

        Single.just("1")
            .map {
                return@map getAllDebtsUseCase.execute(id = idHuman!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val adapter = DebtAdapter(view.context, it)
                recyclerView.adapter = adapter
                Log.e("TAG", "Debts load success")
            }, {

            })

        return view
    }
}