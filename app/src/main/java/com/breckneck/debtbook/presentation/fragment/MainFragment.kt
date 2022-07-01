package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.MainViewModel
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragment : Fragment() {

    private lateinit var vm: MainViewModel

    interface OnButtonClickListener{
        fun changeMainFragment(Fragment: String, idHuman: Int)
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)

        Log.e("TAG", "Main Fragment created")

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val getAllHumansUseCase by lazy { GetAllHumansUseCase(humanRepository = humanRepository) }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)

        val addButton: Button = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
            buttonClickListener?.changeMainFragment(Fragment = "NewDebtFragment", idHuman = 0)
        }

        val humanClickListener = object: HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
                buttonClickListener?.changeMainFragment(Fragment = "DebtDetailsFragment", idHuman = humanDomain.id)
                Log.e("TAG", "Click on human")
            }
        }

        Single.just("1")
            .map {
//                return@map vm.loadAllHumans()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                val adapter = HumanAdapter(view.context, it, humanClickListener)
//                recyclerView.adapter = adapter
//                Log.e("TAG", "Humans load success")
            }, {

            })

        return view
    }
}