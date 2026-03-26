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
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFinanceCategoryFragment : Fragment() {

    private val vm by viewModels<CreateFinanceCategoryViewModel>()

    interface OnClickListener {
        fun onBackButtonClick()
    }

    private var onClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (vm.financeCategoryState.value == null)
            vm.setFinanceCategoryState(
                financeCategoryState =
                when (arguments?.getString("categoryState").toString()) {
                    FinanceCategoryState.EXPENSE.toString() -> FinanceCategoryState.EXPENSE
                    FinanceCategoryState.INCOME.toString() -> FinanceCategoryState.INCOME
                    else -> FinanceCategoryState.EXPENSE
                }
            )

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    CreateFinanceCategoryScreen(
                        vm = vm,
                        onBackClick = { onClickListener!!.onBackButtonClick() },
                        onCategorySaved = {
                            setFragmentResult(
                                "createFinanceFragmentKey",
                                bundleOf(
                                    "isListModified" to true,
                                    "categoryState" to vm.financeCategoryState.value!!.toString()
                                )
                            )
                            onClickListener!!.onBackButtonClick()
                        }
                    )
                }
            }
        }
    }
}
