package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.viewmodel.FinanceFragmentViewModel
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

        vm.financeList.observe(viewLifecycleOwner) { financeList ->

        }
    }
}