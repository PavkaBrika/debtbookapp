package com.breckneck.debtbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.LAST_CHECKED_POSITION_NOT_EXISTS

class FinanceCategoryAdapter(
    private val financeCategoryList: List<FinanceCategory>,
    private val onFinanceCategoryClickListener: OnFinanceCategoryClickListener
): RecyclerView.Adapter<FinanceCategoryAdapter.FinanceCategoryViewHolder>() {

    var lastCheckedPosition = LAST_CHECKED_POSITION_NOT_EXISTS

    interface OnFinanceCategoryClickListener {
        fun onClick(financeCategory: FinanceCategory)
    }

    class FinanceCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryRootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
        val categoryBackgroundCardView: CardView = itemView.findViewById(R.id.categoryBackgroundCardView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_finance_category, parent, false)
        return FinanceCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanceCategoryViewHolder, position: Int) {
        val financeCategory = financeCategoryList[position]

        holder.categoryBackgroundCardView.setCardBackgroundColor(Color.parseColor(financeCategory.color))
        holder.categoryTextView.text = financeCategory.name

        if (financeCategory.isChecked) {
            holder.categoryRootLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.custom_border_filled)
            lastCheckedPosition = position
        } else
            holder.categoryRootLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.custom_border_outline)

        holder.categoryRootLayout.setOnClickListener {
            if (lastCheckedPosition != LAST_CHECKED_POSITION_NOT_EXISTS) {
                financeCategoryList[lastCheckedPosition].isChecked = false
                notifyItemChanged(lastCheckedPosition)
            }
            lastCheckedPosition = position
            notifyItemChanged(lastCheckedPosition)
            financeCategory.isChecked = true
            onFinanceCategoryClickListener.onClick(financeCategory = financeCategory)
        }
    }

    override fun getItemCount(): Int {
        return financeCategoryList.size
    }
}