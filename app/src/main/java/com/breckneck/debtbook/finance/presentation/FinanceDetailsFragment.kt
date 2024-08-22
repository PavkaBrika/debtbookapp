package com.breckneck.debtbook.finance.presentation

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.finance.adapter.FinanceAdapter
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.debtbook.finance.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Debt.FormatDebtSum
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.ListState
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class FinanceDetailsFragment: Fragment() {

    private val TAG = "FinanceDetailsFragment"

    private val vm by viewModel<FinanceDetailsViewModel>()

    lateinit var financeAdapter: FinanceAdapter

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
                vm.getFinanceByCategoryId(categoryId = vm.categoryId.value!!)
        }
        vm.setFinanceListState(state = ListState.LOADING)
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
        categoryName = GetFinanceCategoryNameInLocalLanguage().execute(
            financeName = categoryName!!,
            state = when (vm.isExpenses.value!!) {
                true -> FinanceCategoryState.EXPENSE
                false -> FinanceCategoryState.INCOME
            },
            context = requireContext())
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
            vm.getFinanceByCategoryId(categoryId = vm.categoryId.value!!)
        }

        val financeClickListener = object : FinanceAdapter.OnFinanceClickListener {
            override fun onFinanceClick(finance: Finance, position: Int) {
                showDebtSettings(finance = finance, currency = currency!!)
            }
        }
        financeAdapter = FinanceAdapter(financeListImmutable = listOf(), financeClickListener = financeClickListener, currency!!)

        val financeRecyclerView: RecyclerView = view.findViewById(R.id.financeRecyclerView)
        financeRecyclerView.adapter = financeAdapter
        val noNotesTextView: TextView = view.findViewById(R.id.noNotesTextView)
        val financesLayout: ConstraintLayout = view.findViewById(R.id.financesLayout)
        val emptyFinancesLayout: ConstraintLayout = view.findViewById(R.id.emptyFinancesLayout)
        val loadingFinancesLayout: ConstraintLayout = view.findViewById(R.id.loadingFinancesLayout)
        val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        vm.financeListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.LOADING -> {
                    financesLayout.visibility = View.GONE
                    emptyFinancesLayout.visibility = View.GONE
                    loadingFinancesLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmerAnimation()
                }
                ListState.FILLED -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(financesLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    financesLayout.visibility = View.VISIBLE
                    emptyFinancesLayout.visibility = View.GONE
                    loadingFinancesLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
                ListState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyFinancesLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    financesLayout.visibility = View.GONE
                    emptyFinancesLayout.visibility = View.VISIBLE
                    loadingFinancesLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                    if (vm.isExpenses.value!!) {
                        noNotesTextView.text = getString(R.string.there_are_no_expenses_yet)
                    } else {
                        noNotesTextView.text = getString(R.string.there_are_no_incomes_yet)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({
            vm.financeList.observe(this) { financeList ->
                if (financeList.isNotEmpty()) {
                    financeAdapter.updateFinanceList(financeList = financeList)
                    vm.setFinanceListState(ListState.FILLED)
                    Log.e(TAG, "Finance list updated in adapter")
                }
            }
        }, 400)
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