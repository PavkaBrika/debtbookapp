package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.GoalAdapter
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.viewmodel.GoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.util.ListState
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalsFragment: Fragment() {

    private val TAG = "GoalsFragment"

    private val vm by viewModel<GoalsFragmentViewModel>()

    lateinit var goalsRecyclerView: RecyclerView
    lateinit var goalAdapter: GoalAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val goalsLayout: ConstraintLayout = view.findViewById(R.id.goalsLayout)
        val emptyGoalsLayout: ConstraintLayout = view.findViewById(R.id.emptyGoalsLayout)
        val loadingGoalsLayout: ConstraintLayout = view.findViewById(R.id.loadingGoalsLayout)
        val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        vm.goalListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.LOADING -> {
                    goalsLayout.visibility = View.GONE
                    emptyGoalsLayout.visibility = View.GONE
                    loadingGoalsLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmerAnimation()
                }
                ListState.FILLED -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(goalsLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    goalsLayout.visibility = View.VISIBLE
                    emptyGoalsLayout.visibility = View.GONE
                    loadingGoalsLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
                ListState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyGoalsLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    goalsLayout.visibility = View.GONE
                    emptyGoalsLayout.visibility = View.VISIBLE
                    loadingGoalsLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
            }
        }

        goalsRecyclerView = view.findViewById(R.id.goalsRecyclerView)
        val goalClickListener = object: GoalAdapter.OnGoalClickListener {
            override fun onGoalClick(goal: Goal, position: Int) {

            }

            override fun onAddButtonClick(goal: Goal, position: Int) {

            }
        }
        goalAdapter = GoalAdapter(goalListImmutable = listOf(), goalClickListener)
        goalsRecyclerView.adapter = goalAdapter

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        vm.goalList.observe(viewLifecycleOwner) { goalList ->
            goalAdapter.updateGoalList(goalList)
            Log.e(TAG, "data in adapter link success")
        }
    }

}