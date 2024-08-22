package com.breckneck.debtbook.finance.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.Finance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat

class FinanceAdapter(
    private val financeListImmutable: List<Finance>,
    private val financeClickListener: OnFinanceClickListener,
    private val currencyText: String
) : RecyclerView.Adapter<FinanceAdapter.FinanceViewHolder>() {

    private val decimalFormat = DecimalFormat("###,###,###.##")
    val sdf = SimpleDateFormat("d MMM yyyy")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val financeList: MutableList<Finance> = financeListImmutable.toMutableList()

    interface OnFinanceClickListener {
        fun onFinanceClick(finance: Finance, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFinanceList(financeList: List<Finance>) {
        this.financeList.clear()
        this.financeList.addAll(financeList)
        notifyDataSetChanged()
    }

    class FinanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val financeSumTextView: TextView = itemView.findViewById(R.id.financeSumTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val info: TextView = itemView.findViewById(R.id.infoTextView)
        val currency: TextView = itemView.findViewById(R.id.currencyTextVew)
        val infoLayout: LinearLayout = itemView.findViewById(R.id.infoLayout)
        val financeCardView: CardView = itemView.findViewById(R.id.financeCardView)
        val greenColor = itemView.resources.getColor(R.color.green)
        val redColor = itemView.resources.getColor(R.color.red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceViewHolder {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_finance, parent, false)
        return FinanceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanceViewHolder, position: Int) {
        val finance = financeList[position]
//        if (debtDomain.sum > 0) {
//            holder.debt.setTextColor(holder.greenColor)
//            holder.currency.setTextColor(holder.greenColor)
//            holder.debt.text = "+${decimalFormat.format(debtDomain.sum)}"
//        } else {
//            holder.debt.setTextColor(holder.redColor)
//            holder.currency.setTextColor(holder.redColor)
//            holder.debt.text = decimalFormat.format(debtDomain.sum)
//        }
        holder.financeSumTextView.text = decimalFormat.format(finance.sum)
        holder.date.text = sdf.format(finance.date)
        holder.info.text = finance.info
        holder.currency.text = currencyText
        if (finance.info.equals("")) {
            holder.infoLayout.visibility = View.GONE
        } else {
            holder.infoLayout.visibility = View.VISIBLE
        }
        holder.financeCardView.setOnClickListener {
            financeClickListener.onFinanceClick(finance = finance, position = position)
        }
    }

    override fun getItemCount(): Int {
        return financeList.size
    }
}

