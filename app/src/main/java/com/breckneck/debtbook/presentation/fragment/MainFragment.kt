package com.breckneck.debtbook.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.usecase.GetAllHumansUseCase

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val getAllHumansUseCase by lazy { GetAllHumansUseCase(humanRepository = humanRepository) }

        var humanList = getAllHumansUseCase.execute()

        val addButton: Button = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frameLayout, NewDebtFragment())?.addToBackStack(null)?.commit()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        val adapter = HumanAdapter(view.context, humanList)
        recyclerView.adapter = adapter
        return view
    }
}