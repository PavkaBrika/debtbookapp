package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.usecase.SetHumanUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class NewDebtFragment: Fragment() {

    interface OnButtonClickListener{
        fun changeNewDebtFragmentToDebtDetailsFragment()
    }

    var buttonClickListener: OnButtonClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addnewhuman, container, false)

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val setHumanUseCase by lazy { SetHumanUseCase(humanRepository = humanRepository) }

        val humanNameEditText: EditText = view.findViewById(R.id.humanNameEditText)
        val debtSumEditText : EditText = view.findViewById(R.id.debtSumEditText)
        val setButton : Button = view.findViewById(R.id.setDebtButton)
        setButton.setOnClickListener{
            val name = humanNameEditText.text.toString()
            val sum = debtSumEditText.text.toString()
            Single.just(name)
                .map {
                    setHumanUseCase.execute(name = name, sumDebt = sum.toDouble())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("TAG", "Human set success")
                }, {

                })
            buttonClickListener?.changeNewDebtFragmentToDebtDetailsFragment()
        }

        return view
    }
}