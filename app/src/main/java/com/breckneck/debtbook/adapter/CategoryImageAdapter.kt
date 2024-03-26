package com.breckneck.debtbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.util.LAST_CHECKED_POSITION_NOT_EXISTS

class CategoryImageAdapter(
    private val categoryImageList: List<Int>,
    private val onCategoryImageClickListener: OnCategoryImageClickListener
): RecyclerView.Adapter<CategoryImageAdapter.CategoryImageViewHolder>() {

    var lastCheckedPosition = LAST_CHECKED_POSITION_NOT_EXISTS

    interface OnCategoryImageClickListener {
        fun onCLick(categoryImage: Int)
    }

    class CategoryImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val categoryImageRootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_image, parent, false)
        return CategoryImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryImageViewHolder, position: Int) {
        val categoryImage = categoryImageList[position]

        holder.categoryTextView.text = String(Character.toChars(categoryImage))

        if (lastCheckedPosition == position) {
            holder.categoryImageRootLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.custom_border_filled)
            lastCheckedPosition = position
        } else
            holder.categoryImageRootLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.custom_border_outline)

        holder.categoryImageRootLayout.setOnClickListener {
            if (lastCheckedPosition != LAST_CHECKED_POSITION_NOT_EXISTS)
                notifyItemChanged(lastCheckedPosition)
            lastCheckedPosition = position
            notifyItemChanged(lastCheckedPosition)
            onCategoryImageClickListener.onCLick(categoryImage = categoryImage)
        }
    }

    override fun getItemCount(): Int {
        return categoryImageList.size
    }
}