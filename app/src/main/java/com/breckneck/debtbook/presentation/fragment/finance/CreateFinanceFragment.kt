package com.breckneck.debtbook.presentation.fragment.finance

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.FinanceCategoryAdapter
import com.breckneck.debtbook.presentation.viewmodel.CreateFinanceFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CreateFinanceFragment: Fragment() {

    private val vm by viewModel<CreateFinanceFragmentViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val financeDateTextView: TextView = view.findViewById(R.id.financeDateTextView)
        vm.date.observe(viewLifecycleOwner) { date ->
            financeDateTextView.text = "$date ${getString(R.string.year)}"
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener{ view, year, month, day ->
            vm.setCurrentDate(year = year, month = month, day = day)
        }
        financeDateTextView.setOnClickListener{
            val calendar = Calendar.getInstance()
            DatePickerDialog(view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        vm.financeCategoryList.observe(viewLifecycleOwner) { financeCategoryList ->
            categoryRecyclerView.adapter = FinanceCategoryAdapter(financeCategoryList = financeCategoryList)
        }
    }
}