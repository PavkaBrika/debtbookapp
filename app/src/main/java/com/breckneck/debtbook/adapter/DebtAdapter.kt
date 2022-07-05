package com.breckneck.debtbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.DebtDomain

class DebtAdapter(private val debtDomainList: List<DebtDomain>, val debtClickListener: OnDebtClickListener): RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    interface OnDebtClickListener {
        fun onDebtClick(debtDomain: DebtDomain, position: Int)
    }

    class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val debt: TextView = itemView.findViewById(R.id.debtTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val info: TextView = itemView.findViewById(R.id.infoTextView)
        val currency: TextView = itemView.findViewById(R.id.currencyTextVew)
        val infoLayout: LinearLayout = itemView.findViewById(R.id.infoLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.debt_list, parent, false)
        return  DebtViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val debtDomain = debtDomainList[position]
        holder.debt.text = debtDomain.sum.toString()
        holder.date.text = debtDomain.date
        holder.info.text = debtDomain.info
        holder.currency.text = debtDomain.currency
        if (holder.info.text.equals("")) {
            holder.infoLayout.visibility = View.GONE
        }
        holder.itemView.setOnClickListener{
            debtClickListener.onDebtClick(debtDomain = debtDomain, position = position)
        }
    }

    override fun getItemCount(): Int {
        return debtDomainList.size
    }
}

