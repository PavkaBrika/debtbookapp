package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.FinanceAdapter
import com.breckneck.debtbook.presentation.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Debt.FormatDebtSum
import com.breckneck.deptbook.domain.util.revenuesCategoryEnglishNameList
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class FinanceDetailsFragment: Fragment() {

    private val TAG = "FinanceDetailsFragment"

    private val vm by viewModel<FinanceDetailsViewModel>()

    interface OnClickListener {
        fun onEditFinanceClick(finance: Finance)

        fun onBackButtonClick()
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
        setFragmentResultListener("financeDetailsKey") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getFinanceByCategoryIdAndExpenses(categoryId = vm.categoryId.value!!, isExpenses = vm.isExpenses.value!!)
        }
        return inflater.inflate(R.layout.fragment_finance_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var categoryName = arguments?.getString("categoryName")
        if (vm.categoryId.value == null)
            vm.setCategoryId(arguments?.getInt("categoryId")!!)
        if (vm.isExpenses.value == null)
            vm.setExpenses(arguments?.getBoolean("isExpenses")!!)
        val currency = arguments?.getString("currency")

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)

        for (i in revenuesCategoryEnglishNameList.indices) {
            if (categoryName == revenuesCategoryEnglishNameList[i]) {
                when (i) {
                    0 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.health)
                        break
                    }
                    1 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.entertainment)
                        break
                    }
                    2 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.home)
                        break
                    }
                    3 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.education)
                        break
                    }
                    4 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.presents)
                        break
                    }
                    5 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.food)
                        break
                    }
                    6 -> {
                        categoryName =
                            ContextCompat.getString(requireContext(), R.string.other)
                        break
                    }
                    else -> {
                        break
                    }
                }
            }
        }

        val spannable = if (vm.isExpenses.value == true)
            SpannableString("$categoryName\n${getString(R.string.expenses)}")
        else
            SpannableString("$categoryName\n${getString(R.string.revenues)}")
//        if (isRevenue == true)
//            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)), categoryName!!.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        else
//            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)), categoryName!!.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        collaps.title = spannable //TODO SPANNABLE DOESNT WORK IN TITLE NEED FIX
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        if (vm.isSettingsDialogOpened.value == true) {
            showDebtSettings(finance = vm.settingsFinance.value!!, currency = currency!!)
        }

        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            buttonClickListener!!.onBackButtonClick()
        }

        if (vm.financeList.value == null) {
            vm.getFinanceByCategoryIdAndExpenses(categoryId = vm.categoryId.value!!, isExpenses = vm.isExpenses.value!!)
        }

        val financeClickListener = object : FinanceAdapter.OnFinanceClickListener {
            override fun onFinanceClick(finance: Finance, position: Int) {
                showDebtSettings(finance = finance, currency = currency!!)
            }
        }
        val financeAdapter = FinanceAdapter(financeListImmutable = listOf(), financeClickListener = financeClickListener, currency!!)
        val financeRecyclerView: RecyclerView = view.findViewById(R.id.financeRecyclerView)
        financeRecyclerView.addItemDecoration(
            DividerItemDecoration(
                view.context,
                DividerItemDecoration.VERTICAL
            )
        )
        financeRecyclerView.adapter = financeAdapter
        val noNotesTextView: TextView = view.findViewById(R.id.noNotesTextView)
        val nestedScrollView: NestedScrollView = view.findViewById(R.id.nestedScroll)
        vm.financeList.observe(viewLifecycleOwner) { financeList ->
            financeAdapter.updateFinanceList(financeList = financeList)
            if (financeList.isEmpty()) {
                nestedScrollView.visibility = View.GONE
                noNotesTextView.visibility = View.VISIBLE
                if (vm.isExpenses.value!!) {
                    noNotesTextView.text = getString(R.string.there_are_no_expenses_yet)
                } else {
                    noNotesTextView.text = getString(R.string.there_are_no_incomes_yet)
                }
            } else {
                nestedScrollView.visibility = View.VISIBLE
                noNotesTextView.visibility = View.GONE
            }
        }
    }

    private fun showDebtSettings(finance: Finance, currency: String) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_extra_functions)
        val formatDebtSum = FormatDebtSum()
        val sdf = SimpleDateFormat("d MMM yyyy")

        vm.onFinanceSettingsDialogOpen()
        vm.onSetSettingFinance(finance = finance)

        dialog.findViewById<TextView>(R.id.extrasTitle)!!.text =
            "${sdf.format(finance.date)} : ${formatDebtSum.execute(finance.sum)} $currency"

        dialog.findViewById<Button>(R.id.deleteButton)!!.setOnClickListener {
            vm.deleteFinance(finance = finance)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.editButton)!!.setOnClickListener {
            buttonClickListener?.onEditFinanceClick(finance = finance)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onFinanceSettingsDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onFinanceSettingsDialogClose()
        }

        dialog.show()
    }


}