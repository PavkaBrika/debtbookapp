package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.FinanceAdapter
import com.breckneck.debtbook.presentation.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinanceDetailsFragment: Fragment() {

    private val TAG = "FinanceDetailsFragment"

    private val vm by viewModel<FinanceDetailsViewModel>()

    interface OnClickListener {
        fun onBackButtonClick()

        fun editFinance()
    }

    var buttonClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_finance_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt("categoryId")
        val isRevenue = arguments?.getBoolean("isRevenue")


        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            buttonClickListener!!.onBackButtonClick()
        }

        if (vm.financeList.value == null) {
            vm.getFinanceByCategoryIdAndRevenue(categoryId = categoryId!!, isRevenue = isRevenue!!)
        }

        val financeClickListener = object : FinanceAdapter.OnFinanceClickListener {
            override fun onFinanceClick(finance: Finance, position: Int) {

            }
        }
        val financeAdapter = FinanceAdapter(financeListImmutable = listOf(), financeClickListener = financeClickListener, "RUB")
        val financeRecyclerView: RecyclerView = view.findViewById(R.id.financeRecyclerView)
        financeRecyclerView.adapter = financeAdapter
        vm.financeList.observe(viewLifecycleOwner) { financeList ->
            financeAdapter.updateFinanceList(financeList = financeList)
        }
    }
}