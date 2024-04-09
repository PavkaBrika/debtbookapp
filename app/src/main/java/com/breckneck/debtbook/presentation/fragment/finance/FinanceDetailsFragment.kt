package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.FinanceAdapter
import com.breckneck.debtbook.presentation.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.google.android.material.appbar.CollapsingToolbarLayout
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

        val categoryName = arguments?.getString("categoryName")
        val categoryId = arguments?.getInt("categoryId")
        val isRevenue = arguments?.getBoolean("isRevenue")
        val currency = arguments?.getString("currency")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        val spannable = if (isRevenue == true)
            SpannableString("$categoryName - ${getString(R.string.revenues)}")
        else
            SpannableString("$categoryName - ${getString(R.string.expenses)}")
        if (isRevenue == true)
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)), categoryName!!.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        else
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)), categoryName!!.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        collaps.title = spannable //TODO SPANNABLE DOESNT WORK IN TITLE NEED FIX
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

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
        val financeAdapter = FinanceAdapter(financeListImmutable = listOf(), financeClickListener = financeClickListener, currency!!)
        val financeRecyclerView: RecyclerView = view.findViewById(R.id.financeRecyclerView)
        financeRecyclerView.adapter = financeAdapter
        vm.financeList.observe(viewLifecycleOwner) { financeList ->
            financeAdapter.updateFinanceList(financeList = financeList)
        }
    }


}