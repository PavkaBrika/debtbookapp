package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val vm by viewModel<MainFragmentViewModel>()

//    interface OnButtonClickListener{
//        fun OnHumanClick(idHuman: Int, currency: String, name: String)
//        fun OnAddButtonClick()
//    }

//    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
//        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "Activity created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener{
//            buttonClickListener?.OnAddButtonClick()
            Navigation.findNavController(view).navigate(MainFragmentDirections.actionMainFragmentToNewDebtFragment())
        }

        val humanClickListener = object: HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
//                buttonClickListener?.OnHumanClick(idHuman = humanDomain.id, currency = humanDomain.currency, name = humanDomain.name)
                val action = MainFragmentDirections.actionMainFragmentToDebtDetailsFragment(idHuman = humanDomain.id, currency = humanDomain.currency, name = humanDomain.name)
                Navigation.findNavController(view).navigate(action)
                Log.e("TAG", "Click on human with id = ${humanDomain.id}")
            }
        }

        vm.apply {
            getAllHumans()
            getNegativeSum()
            getPositiveSum()
        }
        vm.resultHumanList.observe(requireActivity()) {
            if (it.isNotEmpty())
                noDebtsTextView.visibility = View.INVISIBLE
            else
                noDebtsTextView.visibility = View.VISIBLE
            val adapter = HumanAdapter(it, humanClickListener)
            recyclerView.adapter = adapter
            Log.e("TAG", "adapter link success")
        }

        val overallPositiveSumTextView: TextView = view.findViewById(R.id.overallPositiveSumTextView)
        val overallNegativeSumTextView: TextView = view.findViewById(R.id.overallNegativeSumTextView)
        vm.resultPos.observe(requireActivity()) {
            overallPositiveSumTextView.text = it
            if (it == "") {
                overallPositiveSumTextView.visibility = View.GONE
            } else {
                overallPositiveSumTextView.visibility = View.VISIBLE
                overallPositiveSumTextView.text = it
            }
        }

        vm.resultNeg.observe(requireActivity()) {
            overallNegativeSumTextView.text = it
            if (it == "") {
                overallNegativeSumTextView.textSize = 0F
            } else {
                overallNegativeSumTextView.visibility = View.VISIBLE
                overallNegativeSumTextView.text = it
            }
        }

        return view
    }
}