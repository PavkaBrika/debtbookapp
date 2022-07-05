package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.HumanDomain

class HumanAdapter(private val humanDomainList: List<HumanDomain>, val humanClickListener: OnHumanClickListener): RecyclerView.Adapter<HumanAdapter.HumanViewHolder>() {

    interface OnHumanClickListener{
        fun onHumanClick(humanDomain: HumanDomain, position: Int)
    }


    class HumanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val debt: TextView = itemView.findViewById(R.id.debt)
        val greenColor = itemView.resources.getColor(R.color.green)
        val redColor = itemView.resources.getColor(R.color.red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.human_list, parent, false)
        return  HumanViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HumanViewHolder, position: Int) {
        val humanDomain = humanDomainList[position]
        holder.name.text = humanDomain.name
        if (humanDomain.sumDebt > 0)
            holder.debt.setTextColor(holder.greenColor)
        else
            holder.debt.setTextColor(holder.redColor)
        holder.debt.text = humanDomain.sumDebt.toString()
        holder.itemView.setOnClickListener{
            humanClickListener.onHumanClick(humanDomain = humanDomain, position = position)
        }
    }

    override fun getItemCount(): Int {
        return humanDomainList.size
    }
}