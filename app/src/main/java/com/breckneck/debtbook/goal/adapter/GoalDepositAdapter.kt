package com.breckneck.debtbook.goal.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.GoalDeposit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat

class GoalDepositAdapter(
    private val goalDepositListImmutable: List<GoalDeposit>,
    private val currency: String
): RecyclerView.Adapter<GoalDepositAdapter.GoalDepositViewHolder>() {

    private val decimalFormat = DecimalFormat("###,###,###.##")
    private val sdf = SimpleDateFormat("d MMM yyyy")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val goalDepositList: MutableList<GoalDeposit> = goalDepositListImmutable.toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateGoalDepositList(goalDepositList: List<GoalDeposit>) {
        this.goalDepositList.clear()
        this.goalDepositList.addAll(goalDepositList)
        notifyDataSetChanged()
    }

    fun addGoalDeposit(goalDeposit: GoalDeposit) {
        goalDepositList.add(goalDeposit)
        notifyItemInserted(goalDepositList.size - 1);
    }

    class GoalDepositViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val transactionSumTextView: TextView = itemView.findViewById(R.id.transactionSumTextView)
        val transactionCurrencyTextVew: TextView = itemView.findViewById(R.id.transactionCurrencyTextVew)
        val transactionDateTextView: TextView = itemView.findViewById(R.id.transactionDateTextView)

        val greenColor = ContextCompat.getColor(itemView.context, R.color.green)
        val redColor = ContextCompat.getColor(itemView.context, R.color.red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalDepositViewHolder {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_goal_transaction, parent, false)
        return GoalDepositViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GoalDepositViewHolder, position: Int) {
        val goalDeposit = goalDepositList[position]
        if (goalDeposit.sum > 0) {
            holder.transactionSumTextView.text = "+${decimalFormat.format(goalDeposit.sum)}"
            holder.transactionSumTextView.setTextColor(holder.greenColor)
            holder.transactionCurrencyTextVew.setTextColor(holder.greenColor)
        } else {
            holder.transactionSumTextView.text = decimalFormat.format(goalDeposit.sum)
            holder.transactionSumTextView.setTextColor(holder.redColor)
            holder.transactionCurrencyTextVew.setTextColor(holder.redColor)
        }
        holder.transactionCurrencyTextVew.text = currency
        holder.transactionDateTextView.text = sdf.format(goalDeposit.date)
    }

    override fun getItemCount(): Int {
        return goalDepositList.size
    }

}