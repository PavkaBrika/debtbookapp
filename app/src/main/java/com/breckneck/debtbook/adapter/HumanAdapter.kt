package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.HumanDomain
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class HumanAdapter(
    humanDomainListImmutable: List<HumanDomain>,
    val humanClickListener: OnHumanClickListener
) : RecyclerView.Adapter<HumanAdapter.HumanViewHolder>() {

    val decimalFormat = DecimalFormat("###,###,###.##")
    val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    val humanDomainList: MutableList<HumanDomain> = humanDomainListImmutable.toMutableList()

    interface OnHumanClickListener{
        fun onHumanClick(humanDomain: HumanDomain, position: Int)

        fun onHumanLongClick(humanDomain: HumanDomain, position: Int)
    }

    fun updateHuman(humanDomain: HumanDomain, position: Int) {
        humanDomainList[position] = humanDomain
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateHumansList(humanList: List<HumanDomain>) {
        humanDomainList.clear()
        humanDomainList.addAll(humanList)
        notifyDataSetChanged()
    }

    class HumanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val debt: TextView = itemView.findViewById(R.id.debt)
        val currency: TextView = itemView.findViewById(R.id.currency)
        val greenColor = itemView.resources.getColor(R.color.green)
        val redColor = itemView.resources.getColor(R.color.red)
        val grayColor = itemView.resources.getColor(R.color.darkgray)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_human, parent, false)
        return  HumanViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HumanViewHolder, position: Int) {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val humanDomain = humanDomainList[position]
        holder.name.text = humanDomain.name
        holder.currency.text = humanDomain.currency
        if (humanDomain.sumDebt > 0) {
            holder.debt.setTextColor(holder.greenColor)
            holder.currency.setTextColor(holder.greenColor)
            holder.debt.text = "+${decimalFormat.format(humanDomain.sumDebt)}"
        } else if (humanDomain.sumDebt < 0) {
            holder.debt.setTextColor(holder.redColor)
            holder.currency.setTextColor(holder.redColor)
            holder.debt.text = decimalFormat.format(humanDomain.sumDebt)
        } else {
            holder.debt.setTextColor(holder.grayColor)
            holder.currency.setTextColor(holder.grayColor)
            holder.debt.text = decimalFormat.format(humanDomain.sumDebt)
        }


        holder.itemView.setOnClickListener{
            humanClickListener.onHumanClick(humanDomain = humanDomain, position = position)
        }
        holder.itemView.setOnLongClickListener {
            humanClickListener.onHumanLongClick(humanDomain = humanDomain, position = position)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return humanDomainList.size
    }
}