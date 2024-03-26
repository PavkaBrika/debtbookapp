package com.breckneck.debtbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.util.LAST_CHECKED_POSITION_NOT_EXISTS

class CategoryColorAdapter(
    private val categoryColorList: List<String>,
    private val onCategoryColorClickListener: OnCategoryColorClickListener
): RecyclerView.Adapter<CategoryColorAdapter.CategoryColorViewHolder>() {

    private var lastCheckedPosition = LAST_CHECKED_POSITION_NOT_EXISTS

    interface OnCategoryColorClickListener {
        fun onCLick(categoryColor: String)
    }

    class CategoryColorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryColorCheckImageView: ImageView = itemView.findViewById(R.id.categoryColorCheckImageView)
        val categoryColorRootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryColorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_color, parent, false)
        return CategoryColorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryColorViewHolder, position: Int) {
        val categoryColor = categoryColorList[position]

        holder.categoryColorRootLayout.setBackgroundColor(Color.parseColor(categoryColor))

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
            onCategoryColorClickListener.onCLick(categoryColor = categoryColor)
        }
    }

    override fun getItemCount(): Int {
        return categoryColorList.size
    }
}