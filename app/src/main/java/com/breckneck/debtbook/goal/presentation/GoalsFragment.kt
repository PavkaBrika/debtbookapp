package com.breckneck.debtbook.goal.presentation

import android.content.Context
import android.content.pm.ActivityInfo
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
import com.breckneck.debtbook.goal.presentation.screen.GoalsScreen
import com.breckneck.debtbook.goal.viewmodel.GoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    private val vm by viewModels<GoalsFragmentViewModel>()

    interface OnClickListener {
        fun onAddGoalButtonClick()
        fun onGoalClick(goal: Goal)
    }

    private var onButtonClickListener: OnClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onButtonClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        setFragmentResultListener("goalsFragmentKey") { _, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getAllGoals()
        }

        vm.setListState(ListState.LOADING)
        if (vm.goalListNeedToUpdate)
            vm.getAllGoals()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    GoalsScreen(
                        vm = vm,
                        onAddGoalClick = { onButtonClickListener?.onAddGoalButtonClick() },
                        onGoalClick = { goal -> onButtonClickListener?.onGoalClick(goal) }
                    )
                }
            }
        }
    }
}
