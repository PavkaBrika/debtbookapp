package com.breckneck.debtbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.HumanDomain

class HumanAdapter(val context: Context, private val humanDomainList: List<HumanDomain>, val humanClickListener: OnHumanClickListener): RecyclerView.Adapter<HumanAdapter.HumanViewHolder>() {

    lateinit var inflater: LayoutInflater

    interface OnHumanClickListener{
        fun onHumanClick(humanDomain: HumanDomain, position: Int)
    }


    class HumanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val debt: TextView = itemView.findViewById(R.id.debt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.human_list, parent, false)
        return  HumanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HumanViewHolder, position: Int) {
        val humanDomain = humanDomainList[position]
        holder.name.text = humanDomain.name
        holder.debt.text = humanDomain.sumDebt.toString()
    }

    override fun getItemCount(): Int {
        return humanDomainList.size
    }
}