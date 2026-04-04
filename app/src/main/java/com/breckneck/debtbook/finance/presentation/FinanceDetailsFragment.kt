package com.breckneck.debtbook.finance.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinanceDetailsFragment : Fragment() {

    private val vm by viewModels<FinanceDetailsViewModel>()

    interface OnClickListener {
        fun onEditFinanceClick(finance: Finance)
        fun onBackButtonClick()
    }

    private var buttonClickListener: OnClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setFragmentResultListener("financeDetailsKey") { _, bundle ->
            if (bundle.getBoolean("isListModified") && vm.categoryId.value != null) {
                vm.getFinanceByCategoryId(categoryId = vm.categoryId.value!!)
            }
        }

        val categoryName = arguments?.getString("categoryName") ?: ""
        val categoryId = arguments?.getInt("categoryId") ?: 0
        val isExpenses = arguments?.getString("categoryState") == FinanceCategoryState.EXPENSE.toString()
        val currency = arguments?.getString("currency") ?: ""

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    FinanceDetailsScreen(
                        vm = vm,
                        categoryName = categoryName,
                        categoryId = categoryId,
                        isExpenses = isExpenses,
                        currency = currency,
                        onBackClick = { buttonClickListener?.onBackButtonClick() },
                        onEditFinanceClick = { finance ->
                            buttonClickListener?.onEditFinanceClick(finance = finance)
                        }
                    )
                }
            }
        }
    }
}
