package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.UsedFinanceCategoryAdapter
import com.breckneck.debtbook.presentation.viewmodel.FinanceFragmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinanceFragment: Fragment() {

    private val vm by viewModel<FinanceFragmentViewModel>()

    interface OnButtonClickListener {
        fun onAddFinanceButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getAllFinances()
        }
        return inflater.inflate(R.layout.fragment_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.addItemDecoration(
            DividerItemDecoration(
                view.context,
                DividerItemDecoration.VERTICAL
            )
        )
        vm.financeList.observe(viewLifecycleOwner) { financeList ->
            categoryRecyclerView.adapter = UsedFinanceCategoryAdapter(financeList)
        }

        val addFinanceButton: FloatingActionButton = view.findViewById(R.id.addFinanceButton)
        addFinanceButton.setOnClickListener {
            buttonClickListener!!.onAddFinanceButtonClick()
        }
    }
}