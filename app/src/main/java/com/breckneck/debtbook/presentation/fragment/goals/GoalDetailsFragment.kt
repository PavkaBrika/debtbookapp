package com.breckneck.debtbook.presentation.fragment.goals

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.viewmodel.GoalDetailsFragmentViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.CollapsingToolbarLayout
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

    interface OnClickListener {

//        fun onGoalClick()

        fun onBackButtonClick()
    }

    private var onClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vm.setGoal(arguments?.getSerializable("goal", Goal::class.java)!!)
        } else {
            vm.setGoal(arguments?.getSerializable("goal") as Goal)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goal_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val decimalFormat = DecimalFormat("###,###,###.##")
        val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
        val sdf = SimpleDateFormat("d MMM yyyy")
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
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

        vm.goal.observe(viewLifecycleOwner) { goal ->
            collaps.title = goal.name
            goalSavedMoneyTextView.text = decimalFormat.format(goal.savedSum)
            goalSumTextView.text = decimalFormat.format(goal.sum)
            goalCreationDateTextView.text = sdf.format(goal.creationDate)

            goalSavedMoneyCurrencyTextView.text = goal.currency
            goalSumCurrencyTextView.text = goal.currency
            goalRemainingMoneyCurrencyTextView.text = goal.currency

            if (goal.sum - goal.savedSum <= 0) { // if goal is reached
                goalRemainingSumLayout.visibility = View.GONE
                val diffInDays = if (TimeUnit.DAYS.convert(
                        Date().time - goal.creationDate.time,
                        TimeUnit.MILLISECONDS
                    ) == 0L
                )
                    1
                else
                    TimeUnit.DAYS.convert(
                        Date().time - goal.creationDate.time,
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

                }
            } else {
                goalRemainingMoneyTextView.text = decimalFormat.format(goal.sum - goal.savedSum)
                if (goal.goalDate != null) {
                    goalDateLayout.visibility = View.VISIBLE
                    if (goal.goalDate!!.before(Date())) {
                        goalDateTextView.text = requireActivity().getString(R.string.overdue)
                        goalDateTextView.setTextColor(Color.RED)
                        goalDateImageView.setColorFilter(Color.RED)
                    } else {
                        goalDateTextView.text = sdf.format(goal.goalDate!!)
                    }
                } else {
                    goalDateLayout.visibility = View.GONE
                }

                deleteButton.visibility = View.GONE
                actionButtonLayout.visibility = View.VISIBLE
                subtractButton.setOnClickListener {

                }
                addButton.setOnClickListener {

                }
            }

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

        super.onViewCreated(view, savedInstanceState)
    }

}