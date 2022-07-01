package com.breckneck.debtbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.DebtDomain

class DebtAdapter(val context: Context, private val debtDomainList: List<DebtDomain>): RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val debt: TextView = itemView.findViewById(R.id.debtTextView)
//        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val currency: TextView = itemView.findViewById(R.id.currencyTextVew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.debt_list, parent, false)
        return  DebtViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val debtDomain = debtDomainList[position]
        holder.debt.text = debtDomain.sum.toString()
//        holder.date.text = debtDomain.date.toString()
        holder.currency.text = debtDomain.currency
    }

    override fun getItemCount(): Int {
        return debtDomainList.size
    }
}

