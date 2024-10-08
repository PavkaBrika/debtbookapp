package com.breckneck.debtbook.finance.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.util.LAST_CHECKED_POSITION_NOT_EXISTS

class FinanceCategoryColorAdapter(
    private val categoryColorList: List<String>,
    private val checkedCategoryColorPosition: Int?,
    private val onCategoryColorClickListener: OnCategoryColorClickListener
): RecyclerView.Adapter<FinanceCategoryColorAdapter.CategoryColorViewHolder>() {

    private var lastCheckedPosition = LAST_CHECKED_POSITION_NOT_EXISTS

    interface OnCategoryColorClickListener {
        fun onClick(position: Int, categoryColor: String)
    }

    class CategoryColorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryColorCheckImageView: ImageView = itemView.findViewById(R.id.categoryColorCheckImageView)
        val categoryColorRootLayout: CardView = itemView.findViewById(R.id.rootLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryColorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_color, parent, false)

        if (checkedCategoryColorPosition != null)
            lastCheckedPosition = checkedCategoryColorPosition

        return CategoryColorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryColorViewHolder, position: Int) {
        val categoryColor = categoryColorList[position]

        holder.categoryColorRootLayout.setCardBackgroundColor(Color.parseColor(categoryColor))

        if (lastCheckedPosition == position) {
            holder.categoryColorCheckImageView.visibility = View.VISIBLE
            lastCheckedPosition = position
        } else
            holder.categoryColorCheckImageView.visibility = View.GONE

        holder.categoryColorRootLayout.setOnClickListener {
            if (lastCheckedPosition != LAST_CHECKED_POSITION_NOT_EXISTS)
                notifyItemChanged(lastCheckedPosition)
            lastCheckedPosition = position
            notifyItemChanged(lastCheckedPosition)
            onCategoryColorClickListener.onClick(position = position, categoryColor = categoryColor)
        }
    }

    override fun getItemCount(): Int {
        return categoryColorList.size
    }
}