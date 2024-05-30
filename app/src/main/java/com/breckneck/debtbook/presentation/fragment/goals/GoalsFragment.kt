package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.GoalAdapter
import com.breckneck.debtbook.presentation.viewmodel.GoalsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.util.ListState
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date

class GoalsFragment: Fragment() {

    private val TAG = "GoalsFragment"

    private val vm by viewModel<GoalsFragmentViewModel>()

    lateinit var goalsRecyclerView: RecyclerView
    lateinit var goalAdapter: GoalAdapter

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
    ): View? {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setFragmentResultListener("goalsFragmentKey") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getAllGoals()
        }
        vm.setListState(state = ListState.LOADING)
        if (vm.goalListNeedToUpdate == true)
            vm.getAllGoals()
        return inflater.inflate(R.layout.fragment_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        if (vm.isAddSumDialogOpened) {
            openAddSavedGoalSumDialog(goal = vm.changedGoal!!, position = vm.changedGoalPosition!!)
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
                onButtonClickListener?.onGoalClick(goal = goal)
            }

            override fun onAddButtonClick(goal: Goal, position: Int) {
                openAddSavedGoalSumDialog(goal = goal, position = position)
            }

            override fun onDeleteButtonClick(goal: Goal, position: Int) {
                deleteGoal(goal = goal, position = position)
            }
        }
        goalAdapter = GoalAdapter(goalListImmutable = listOf(), goalClickListener)
        goalsRecyclerView.adapter = goalAdapter

        val addGoalsButton: FloatingActionButton = view.findViewById(R.id.addGoalsButton)
        addGoalsButton.setOnClickListener {
            onButtonClickListener?.onAddGoalButtonClick()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({
            vm.goalList.observe(viewLifecycleOwner) { goalList ->
                if (goalList.isNotEmpty()) {
                    goalAdapter.updateGoalList(goalList)
                    Log.e(TAG, "data in adapter link success")
                    vm.setListState(state = ListState.FILLED)
                }
            }
        }, 400)
    }


    private fun openAddSavedGoalSumDialog(goal: Goal, position: Int) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_add_goal_sum)
        vm.onOpenAddSavedSumChangeDialog(changedGoal = goal, changedGoalPosition = position)
        val goalSumTextInput: TextInputLayout = dialog.findViewById(R.id.goalSumTextInput)!!
        val goalSumEditText: TextInputEditText = dialog.findViewById(R.id.goalSumEditText)!!

        goalSumEditText.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    val str = editable.toString()
                    val position = str.indexOf(".")
                    if (position != -1) {
                        val subStr = str.substring(position)
                        val subStrStart = str.substring(0, position)
                        if ((subStr.length > 3) || (subStrStart.length == 0))
                            editable?.delete(editable.length - 1, editable.length)
                    }
                }
            }
        }

        dialog.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            val savedSum = goalSumEditText.text.toString().trim()
            if (savedSum.isEmpty())
                goalSumTextInput.error = getString(R.string.youmustentername)
            else {
                addSavedGoalSum(goal = goal, position = position, sum = savedSum.toDouble())
                dialog.dismiss()
            }
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onCloseAddSumChangeDialog()
        }
        dialog.setOnCancelListener {
            vm.onCloseAddSumChangeDialog()
        }

        dialog.show()
    }

    private fun addSavedGoalSum(goal: Goal, position: Int, sum: Double) {
        goal.savedSum += sum
        goalAdapter.updateGoal(goal = goal, position = position)
        vm.updateGoal(goal = goal)
        vm.setGoalDeposit(goalDeposit = GoalDeposit(sum = sum, date = Date(), goalId = goal.id))
    }

    private fun deleteGoal(goal: Goal, position: Int) {
        goalAdapter.deleteGoal(position = position)
        vm.deleteGoal(goal = goal)
    }
}