package com.breckneck.debtbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances

class UsedFinanceCategoryAdapter(
    private val usedFinanceCategoryList: List<FinanceCategoryWithFinances>
): RecyclerView.Adapter<UsedFinanceCategoryAdapter.UsedFinanceCategoryViewHolder>() {

    class UsedFinanceCategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val usedCategoryNameTextView: TextView = itemView.findViewById(R.id.usedCategoryNameTextView)
        val usedCategorySumTextView: TextView = itemView.findViewById(R.id.usedCategorySumTextView)
        val usedCategoryPercent: TextView = itemView.findViewById(R.id.usedCategoryPercent)
        val usedCategoryBackgroundCardView: CardView = itemView.findViewById(R.id.categoryBackgroundCardView)
        val categoryImageTextView: TextView = itemView.findViewById(R.id.categoryImageTextView)
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

        holder.usedCategoryNameTextView.text = usedFinanceCategory.financeCategory.name
        holder.usedCategorySumTextView.text = usedFinanceCategory.categorySum.toString()
        holder.usedCategoryPercent.text = "${usedFinanceCategory.categoryPercentage}%"
        holder.usedCategoryBackgroundCardView.setCardBackgroundColor(Color.parseColor(usedFinanceCategory.financeCategory.color))
        holder.categoryImageTextView.text = String(Character.toChars(usedFinanceCategory.financeCategory.image))
    }

    override fun getItemCount(): Int {
        return usedFinanceCategoryList.size
    }
}