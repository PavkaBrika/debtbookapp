package com.breckneck.debtbook.finance.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsActions
import com.breckneck.debtbook.finance.viewmodel.FinanceDetailsViewModel
import com.breckneck.deptbook.domain.model.Finance
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
            vm.onAction(
                FinanceDetailsActions.RefreshListAfterEdit(
                    wasModified = bundle.getBoolean("isListModified")
                )
            )
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    FinanceDetailsScreen(
                        vm = vm,
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
