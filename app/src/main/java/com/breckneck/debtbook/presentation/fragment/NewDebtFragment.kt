package com.breckneck.debtbook.presentation.fragment

import android.os.Bundle
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

class NewDebtFragment: Fragment() {

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
            setHumanUseCase.execute(name = name, sumDebt = sum.toDouble())
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frameLayout, DebtDetailsFragment())?.addToBackStack(null)?.commit()
        }

        return view
    }
}