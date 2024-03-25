package com.breckneck.debtbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.FinanceCategory

class FinanceCategoryAdapter(
    private val financeCategoryList: List<FinanceCategory>
): RecyclerView.Adapter<FinanceCategoryAdapter.FinanceCategoryViewHolder>() {

    class FinanceCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryBackgroundCardView: CardView = itemView.findViewById(R.id.categoryBackgroundCardView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_finance_category, parent, false)
        return FinanceCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanceCategoryViewHolder, position: Int) {
        val financeCategory = financeCategoryList[position]

        holder.categoryBackgroundCardView.setCardBackgroundColor(Color.parseColor("#${financeCategory.color}"))
        holder.categoryTextView.text = financeCategory.name
    }

    override fun getItemCount(): Int {
        return financeCategoryList.size
    }
}