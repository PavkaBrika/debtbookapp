package com.breckneck.debtbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.Finance

class UsedFinanceCategoryAdapter(
    private val usedFinanceCategoryList: List<Finance> //TODO CHANGE TYPE FROM FINANCE TO CATEGORY
): RecyclerView.Adapter<UsedFinanceCategoryAdapter.UsedFinanceCategoryViewHolder>() {

    class UsedFinanceCategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val usedCategoryNameTextView: TextView = itemView.findViewById(R.id.usedCategoryNameTextView)
        val usedCategorySumTextView: TextView = itemView.findViewById(R.id.usedCategorySumTextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsedFinanceCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_used_category, parent, false)
        return UsedFinanceCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsedFinanceCategoryViewHolder, position: Int) {
        val usedFinanceCategory = usedFinanceCategoryList[position]

        holder.usedCategoryNameTextView.text = usedFinanceCategory.name
        holder.usedCategorySumTextView.text = usedFinanceCategory.sum.toString()
    }

    override fun getItemCount(): Int {
        return usedFinanceCategoryList.size
    }
}