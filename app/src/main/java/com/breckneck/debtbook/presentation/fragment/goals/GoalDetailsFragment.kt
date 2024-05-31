package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.GoalDepositAdapter
import com.breckneck.debtbook.presentation.viewmodel.GoalDetailsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Debt.FormatDebtSum
import com.breckneck.deptbook.domain.util.ChangeGoalSavedSumDialogState
import com.breckneck.deptbook.domain.util.ListState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class GoalDetailsFragment : Fragment() {

    private val TAG = "GoalDetailsFragment"

    private val vm by viewModel<GoalDetailsFragmentViewModel>()

    lateinit var goalDepositRecyclerView: RecyclerView
    lateinit var goalDepositAdapter: GoalDepositAdapter

    interface OnClickListener {

        fun onEditGoalButtonClick(goal: Goal)

        fun onBackButtonClick()
    }

    private var onClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (vm.goal.value == null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                vm.setGoal(arguments?.getSerializable("goal", Goal::class.java)!!)
            else
                vm.setGoal(arguments?.getSerializable("goal") as Goal)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentResultListener("goalDetailsFragmentKey") { requestKey, bundle ->
            if (bundle.getBoolean("isGoalEdited"))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    vm.setGoal(bundle.getSerializable("goal", Goal::class.java)!!)
                else
                    vm.setGoal(bundle.getSerializable("goal") as Goal)
        }
        vm.setGoalListState(ListState.LOADING)
        if (vm.isGoalDepositListNeedToUpdate)
            vm.getGoalDepositList()
        val view = inflater.inflate(R.layout.fragment_goal_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val decimalFormat = DecimalFormat("###,###,###.##")
        val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
        val sdf = SimpleDateFormat("d MMM yyyy")
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol

        if (vm.isChangeSavedSumDialogOpened == true) {
            openChangeSavedGoalSumDialog(state = vm.changeDialogState!!)
        }
        if (vm.isEditOptionsDialogOpened == true) {
            openEditOptionsDialog()
        }

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onClickListener?.onBackButtonClick()
        }

        val editGoalButton: ImageView = view.findViewById(R.id.editGoalButton)
        editGoalButton.setOnClickListener {
            openEditOptionsDialog()
        }

        val goalSavedMoneyTextView: TextView = view.findViewById(R.id.goalSavedMoneyTextView)
        val goalSumTextView: TextView = view.findViewById(R.id.goalSumTextView)
        val goalRemainingSumLayout: LinearLayout = view.findViewById(R.id.goalRemainingSumLayout)
        val goalRemainingMoneyTextView: TextView =
            view.findViewById(R.id.goalRemainingMoneyTextView)
        val goalDateImageView: ImageView = view.findViewById(R.id.goalDateImageView)
        val goalDateLayout: LinearLayout = view.findViewById(R.id.goalDateLayout)
        val goalDateTextView: TextView = view.findViewById(R.id.goalDateTextView)
        val goalCreationDateTextView: TextView = view.findViewById(R.id.goalCreationDateTextView)
        val goalImageView: ImageView = view.findViewById(R.id.goalImageView)
        val goalImageCardView: CardView = view.findViewById(R.id.goalImageCardView)

        val goalSavedMoneyCurrencyTextView: TextView =
            view.findViewById(R.id.goalSavedMoneyCurrencyTextView)
        val goalSumCurrencyTextView: TextView = view.findViewById(R.id.goalSumCurrencyTextView)
        val goalRemainingMoneyCurrencyTextView: TextView =
            view.findViewById(R.id.goalRemainingMoneyCurrencyTextView)

        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val actionButtonLayout: LinearLayout = view.findViewById(R.id.actionButtonLayout)
        val subtractButton: Button = view.findViewById(R.id.subtractButton)
        val addButton: Button = view.findViewById(R.id.addButton)

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        goalDepositRecyclerView = view.findViewById(R.id.goalTransactionRecyclerView)
        goalDepositAdapter = GoalDepositAdapter(listOf(), vm.goal.value!!.currency)
        goalDepositRecyclerView.adapter = goalDepositAdapter

        val goalTransactionLayout: ConstraintLayout = view.findViewById(R.id.goalTransactionLayout)
        val emptyGoalTransactionLayout: ConstraintLayout = view.findViewById(R.id.emptyGoalTransactionLayout)
        val loadingGoalTransactionLayout: ConstraintLayout = view.findViewById(R.id.loadingGoalTransactionLayout)
        val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        vm.goalDepositListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.LOADING -> {
                    goalTransactionLayout.visibility = View.GONE
                    emptyGoalTransactionLayout.visibility = View.GONE
                    loadingGoalTransactionLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmerAnimation()
                }
                ListState.FILLED -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(goalTransactionLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    goalTransactionLayout.visibility = View.VISIBLE
                    emptyGoalTransactionLayout.visibility = View.GONE
                    loadingGoalTransactionLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
                ListState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyGoalTransactionLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    goalTransactionLayout.visibility = View.GONE
                    emptyGoalTransactionLayout.visibility = View.VISIBLE
                    loadingGoalTransactionLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
            }
        }

        vm.goal.observe(viewLifecycleOwner) { goal ->
            collaps.title = goal.name
            goalSumTextView.text = decimalFormat.format(goal.sum)
            goalCreationDateTextView.text = sdf.format(goal.creationDate)

            goalSavedMoneyCurrencyTextView.text = goal.currency
            goalSumCurrencyTextView.text = goal.currency
            goalRemainingMoneyCurrencyTextView.text = goal.currency

            if ((goal.photoPath != null) && (File(goal.photoPath!!).exists())) {
                goalImageCardView.visibility = View.VISIBLE
                val result = Glide.with(goalImageView.context)
                    .load(goal.photoPath)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            goalImageCardView.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(goalImageView)
            } else {
                goalImageCardView.visibility = View.GONE
            }
        }

        vm.goalSavedSum.observe(viewLifecycleOwner) { savedSum ->
            goalSavedMoneyTextView.text = decimalFormat.format(savedSum)
            val sum = vm.goal.value!!.sum
            if (sum - savedSum <= 0) { // if goal is reached
                goalRemainingSumLayout.visibility = View.GONE
                val diffInDays = if (TimeUnit.DAYS.convert(
                        Date().time - vm.goal.value!!.creationDate.time,
                        TimeUnit.MILLISECONDS
                    ) == 0L
                )
                    1
                else
                    TimeUnit.DAYS.convert(
                        Date().time - vm.goal.value!!.creationDate.time,
                        TimeUnit.MILLISECONDS
                    )
                if (diffInDays == 1L) //для разных склонений
                    goalDateTextView.text =
                        requireActivity().getString(R.string.achieved_in_day, diffInDays)
                else
                    goalDateTextView.text =
                        requireActivity().getString(R.string.achieved_in_days, diffInDays)

                deleteButton.visibility = View.VISIBLE
                actionButtonLayout.visibility = View.GONE
                deleteButton.setOnClickListener {
                    vm.deleteGoal(goal = vm.goal.value!!)
                    setFragmentResult("goalsFragmentKey", bundleOf("isListModified" to true))
                    onClickListener!!.onBackButtonClick()
                }
            } else {
                goalRemainingMoneyTextView.text = decimalFormat.format(sum - savedSum)
                if (vm.goal.value!!.goalDate != null) {
                    goalDateLayout.visibility = View.VISIBLE
                    if (vm.goal.value!!.goalDate!!.before(Date())) {
                        goalDateTextView.text = requireActivity().getString(R.string.overdue)
                        goalDateTextView.setTextColor(Color.RED)
                        goalDateImageView.setColorFilter(Color.RED)
                    } else {
                        goalDateTextView.text = sdf.format(vm.goal.value!!.goalDate!!)
                    }
                } else {
                    goalDateLayout.visibility = View.GONE
                }

                deleteButton.visibility = View.GONE
                actionButtonLayout.visibility = View.VISIBLE
                subtractButton.setOnClickListener {
                    openChangeSavedGoalSumDialog(state = ChangeGoalSavedSumDialogState.SUBTRACT)
                }
                addButton.setOnClickListener {
                    openChangeSavedGoalSumDialog(state = ChangeGoalSavedSumDialogState.ADD)
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({
            vm.goalDepositList.observe(viewLifecycleOwner) { goalDepositList ->
                if (goalDepositList.isNotEmpty()) {
                    goalDepositAdapter.updateGoalDepositList(goalDepositList)
                    Log.e(TAG, "data in adapter link success")
                    vm.setGoalListState(state = ListState.FILLED)
                } else {
                    vm.setGoalListState(ListState.EMPTY)
                }
            }
        }, 400)
    }

    private fun openChangeSavedGoalSumDialog(state:ChangeGoalSavedSumDialogState) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_add_goal_sum)
        vm.onOpenChangeSavedSumChangeDialog(
            state = state
        )
        val goalSumTextInput: TextInputLayout = dialog.findViewById(R.id.goalSumTextInput)!!
        val goalSumEditText: TextInputEditText = dialog.findViewById(R.id.goalSumEditText)!!
        val dialogTitleTextView: TextView = dialog.findViewById(R.id.dialogTitleTextView)!!
        dialogTitleTextView.text =  when (state) {
            ChangeGoalSavedSumDialogState.ADD -> requireActivity().getString(R.string.add)
            ChangeGoalSavedSumDialogState.SUBTRACT -> requireActivity().getString(R.string.subtract)
        }

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
                val savedSumDouble = when (state) {
                    ChangeGoalSavedSumDialogState.ADD -> savedSum.toDouble()
                    ChangeGoalSavedSumDialogState.SUBTRACT -> savedSum.toDouble() * (-1)
                }
                if (savedSumDouble == 0.0) {
                    goalSumTextInput.error = getString(R.string.zerodebt)
                } else {
                    vm.updateGoalSum(sum = savedSumDouble)
                    val goalDeposit = GoalDeposit(sum = savedSumDouble, date = Date(), goalId = vm.goal.value!!.id)
                    goalDepositAdapter.addGoalDeposit(goalDeposit = goalDeposit)
                    vm.setGoalListState(state = ListState.FILLED)
                    vm.setGoalDeposit(goalDeposit = goalDeposit)
                    dialog.dismiss()
                }
            }
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onCloseChangeSumChangeDialog()
        }
        dialog.setOnCancelListener {
            vm.onCloseChangeSumChangeDialog()
        }

        dialog.show()
    }

    private fun openEditOptionsDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_extra_functions)

        vm.onOpenEditOptionsDialog()

        dialog.findViewById<TextView>(R.id.extrasTitle)!!.text = vm.goal.value!!.name

        dialog.findViewById<Button>(R.id.deleteButton)!!.setOnClickListener {
            vm.deleteGoal(goal = vm.goal.value!!)
            setFragmentResult("goalsFragmentKey", bundleOf("isListModified" to true))
            dialog.dismiss()
            onClickListener!!.onBackButtonClick()
        }

        dialog.findViewById<Button>(R.id.editButton)!!.setOnClickListener {
            onClickListener!!.onEditGoalButtonClick(goal = vm.goal.value!!)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onCloseEditOptionsDialog()
        }
        dialog.setOnCancelListener {
            vm.onCloseEditOptionsDialog()
        }

        dialog.show()
    }

}