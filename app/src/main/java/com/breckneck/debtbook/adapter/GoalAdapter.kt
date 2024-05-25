package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.HumanDomain
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class GoalAdapter(
    goalListImmutable: List<Goal>,
    private val goalClickListener: OnGoalClickListener
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private val decimalFormat = DecimalFormat("###,###,###.##")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val goalList: MutableList<Goal> = goalListImmutable.toMutableList()

    interface OnGoalClickListener{
        fun onGoalClick(goal: Goal, position: Int)

        fun onAddButtonClick(goal: Goal, position: Int)
    }

    fun updateGoal(goal: Goal, position: Int) {
        goalList[position] = goal
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGoalList(goalList: List<Goal>) {
        this.goalList.clear()
        this.goalList.addAll(goalList)
        notifyDataSetChanged()
    }

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.goalNameTextView)
        val savedMoneyTextView: TextView = itemView.findViewById(R.id.goalSavedMoneyTextView)
        val sumTextView: TextView = itemView.findViewById(R.id.goalSumTextView)
        val goalRemainingMoneyTextView: TextView = itemView.findViewById(R.id.goalRemainingMoneyTextView)

        val goalSavedMoneyCurrencyTextView: TextView = itemView.findViewById(R.id.goalSavedMoneyCurrencyTextView)
        val goalSumCurrencyTextView: TextView = itemView.findViewById(R.id.goalSumCurrencyTextView)
        val goalRemainingMoneyCurrencyTextView: TextView = itemView.findViewById(R.id.goalRemainingMoneyCurrencyTextView)

        val addButton: Button = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val goal = goalList[position]
        holder.nameTextView.text = goal.name
        holder.savedMoneyTextView.text = decimalFormat.format(goal.savedSum)
        holder.sumTextView.text = decimalFormat.format(goal.sum)
        holder.goalRemainingMoneyTextView.text = decimalFormat.format(goal.sum - goal.savedSum)

        holder.goalSavedMoneyCurrencyTextView.text = goal.currency
        holder.goalSumCurrencyTextView.text = goal.currency
        holder.goalRemainingMoneyCurrencyTextView.text = goal.currency

        holder.addButton.setOnClickListener {
            goalClickListener.onAddButtonClick(goal = goal, position = position)
        }
    }

    override fun getItemCount(): Int {
        return goalList.size
    }
}