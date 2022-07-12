package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.deptbook.data.storage.database.DataBaseHumanStorageImpl
import com.breckneck.deptbook.data.storage.repository.HumanRepositoryImpl
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragment : Fragment() {

    interface OnButtonClickListener{
        fun OnHumanClick(idHuman: Int, currency: String, name: String)
        fun OnAddButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val inflater = TransitionInflater.from(requireContext())
//        exitTransition = inflater.inflateTransition(R.transition.slide_right)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val dataBaseHumanStorage by lazy { DataBaseHumanStorageImpl(context = view.context) }
        val humanRepository by lazy { HumanRepositoryImpl(humanStorage = dataBaseHumanStorage) }
        val getAllHumansUseCase by lazy { GetAllHumansUseCase(humanRepository = humanRepository) }
        val getAllDebtsSumUseCase by lazy { GetAllDebtsSumUseCase(humanRepository = humanRepository) }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
            buttonClickListener?.OnAddButtonClick()
        }

        val humanClickListener = object: HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
                buttonClickListener?.OnHumanClick(idHuman = humanDomain.id, currency = humanDomain.currency, name = humanDomain.name)
                Log.e("TAG", "Click on human with id = ${humanDomain.id}")
            }
        }

        Single.just("1")
            .map { val humanList = getAllHumansUseCase.execute()
                if (humanList.isNotEmpty())
                    noDebtsTextView.visibility = View.INVISIBLE
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

        val overallPositiveSumTextView: TextView = view.findViewById(R.id.overallPositiveSumTextView)
        val overallNegativeSumTextView: TextView = view.findViewById(R.id.overallNegativeSumTextView)
        Single.just(overallPositiveSumTextView)
            .map {
                return@map getAllDebtsSumUseCase.execute("positive")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == "") {
                    overallPositiveSumTextView.visibility = View.GONE
                } else {
                    overallPositiveSumTextView.visibility = View.VISIBLE
                    overallPositiveSumTextView.text = it
                }
            },{})
        Single.just(overallNegativeSumTextView)
            .map {
                return@map getAllDebtsSumUseCase.execute("negative")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == "") {
                    overallNegativeSumTextView.textSize = 0F
                } else {
                    overallNegativeSumTextView.visibility = View.VISIBLE
                    overallNegativeSumTextView.text = it
                }
            },{})


        return view
    }
}