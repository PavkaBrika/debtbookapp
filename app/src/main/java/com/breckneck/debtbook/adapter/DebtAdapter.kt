package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.DebtDomain
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DebtAdapter(
    private val debtDomainListImmutable: List<DebtDomain>,
    private val debtClickListener: OnDebtClickListener,
    private val currencyText: String
) : RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    private val decimalFormat = DecimalFormat("###,###,###.##")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val debtDomainList: MutableList<DebtDomain> = debtDomainListImmutable.toMutableList()

    interface OnDebtClickListener {
        fun onDebtClick(debtDomain: DebtDomain, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDebtList(debtList: List<DebtDomain>) {
        debtDomainList.clear()
        debtDomainList.addAll(debtList)
        notifyDataSetChanged()
    }

    class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val debt: TextView = itemView.findViewById(R.id.debtTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val info: TextView = itemView.findViewById(R.id.infoTextView)
        val currency: TextView = itemView.findViewById(R.id.currencyTextVew)
        val debtCardView: CardView = itemView.findViewById(R.id.debtCardView)
        val infoLayout: LinearLayout = itemView.findViewById(R.id.infoLayout)
        val greenColor = itemView.resources.getColor(R.color.green)
        val redColor = itemView.resources.getColor(R.color.red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_debt, parent, false)
        return DebtViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val debtDomain = debtDomainList[position]
        if (debtDomain.sum > 0) {
            holder.debt.setTextColor(holder.greenColor)
            holder.currency.setTextColor(holder.greenColor)
            holder.debt.text = "+${decimalFormat.format(debtDomain.sum)}"
        } else {
            holder.debt.setTextColor(holder.redColor)
            holder.currency.setTextColor(holder.redColor)
            holder.debt.text = decimalFormat.format(debtDomain.sum)
        }
        holder.date.text = debtDomain.date
        holder.info.text = debtDomain.info
        holder.currency.text = currencyText
        if (holder.info.text.equals("")) {
            holder.infoLayout.visibility = View.GONE
        }
        holder.debtCardView.setOnClickListener {
            debtClickListener.onDebtClick(debtDomain = debtDomain, position = position)
        }
    }

    override fun getItemCount(): Int {
        return debtDomainList.size
    }
}

