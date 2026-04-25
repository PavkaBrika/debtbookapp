package com.breckneck.debtbook.goal.main

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
import com.breckneck.debtbook.common.launchOnLifecycleStarted
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.breckneck.debtbook.goal.main.screen.GoalsScreen
import com.breckneck.deptbook.domain.model.Goal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    private val vm by viewModels<GoalsViewModel>()

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
            vm.onAction(
                GoalsAction.RefreshAfterNavigation(
                    wasModified = bundle.getBoolean("isListModified")
                )
            )
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebtBookTheme {
                    GoalsScreen(vm = vm)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchOnLifecycleStarted {
            vm.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    GoalsSideEffect.NavigateToAddGoal ->
                        onButtonClickListener?.onAddGoalButtonClick()
                    is GoalsSideEffect.NavigateToGoalDetails ->
                        onButtonClickListener?.onGoalClick(effect.goal)
                }
            }
        }
    }
}