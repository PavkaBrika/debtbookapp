package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragment : Fragment() {



    interface OnButtonClickListener{
        fun OnHumanClick(idHuman: Int, sharedView: View, sharedName: String)
        fun OnAddButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val getAllHumansUseCase by lazy { GetAllHumansUseCase(humanRepository = humanRepository) }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val addButton: Button = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
            buttonClickListener?.OnAddButtonClick()
        }

        val humanClickListener = object: HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int, name: TextView) {
                val transitionName = ViewCompat.getTransitionName(name)
                buttonClickListener?.OnHumanClick(idHuman = humanDomain.id, sharedView = name, sharedName = transitionName!!)
                Log.e("TAG", "Click on human with id = ${humanDomain.id}")
            }
        }

        Single.just("1")
            .map { val humanList = getAllHumansUseCase.execute()
                return@map humanList
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val adapter = HumanAdapter(it, humanClickListener)
                recyclerView.adapter = adapter
                Log.e("TAG", "Humans load success")
            }, {

            })

        return view
    }
}