package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.UsedFinanceCategoryAdapter
import com.breckneck.debtbook.presentation.viewmodel.FinanceFragmentViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinanceFragment: Fragment() {

    private val vm by viewModel<FinanceFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            vm.setFinance(Finance(id = 0 , name = "123", sum = 123.0, info = ""))
        }
    }
}