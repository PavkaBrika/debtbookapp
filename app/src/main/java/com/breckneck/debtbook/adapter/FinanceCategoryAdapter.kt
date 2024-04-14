package com.breckneck.debtbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.LAST_CHECKED_POSITION_NOT_EXISTS
import com.breckneck.deptbook.domain.util.revenuesCategoryEnglishNameList

class FinanceCategoryAdapter(
    private val financeCategoryList: List<FinanceCategory>,
    private val onFinanceCategoryClickListener: OnFinanceCategoryClickListener
) : RecyclerView.Adapter<FinanceCategoryAdapter.FinanceCategoryViewHolder>() {

    var lastCheckedPosition = LAST_CHECKED_POSITION_NOT_EXISTS
    val getFinanceCategoryNameInLocalLanguage = GetFinanceCategoryNameInLocalLanguage()

    interface OnFinanceCategoryClickListener {
        fun onCategoryClick(financeCategory: FinanceCategory)

        fun onCategoryLongClick(financeCategory: FinanceCategory)

        fun onAddCategoryClick()
    }

    class FinanceCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryRootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
        val categoryBackgroundCardView: CardView =
            itemView.findViewById(R.id.categoryBackgroundCardView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val categoryImageTextView: TextView = itemView.findViewById(R.id.categoryImageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_finance_category, parent, false)
        return FinanceCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanceCategoryViewHolder, position: Int) {
        if (position == financeCategoryList.size) {
            holder.categoryTextView.text = getString(holder.itemView.context, R.string.add)
            holder.categoryBackgroundCardView.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
            holder.categoryImageTextView.text = String(Character.toChars(0x2795))
            holder.categoryRootLayout.setOnClickListener {
                onFinanceCategoryClickListener.onAddCategoryClick()
            }
        } else {
            val financeCategory = financeCategoryList[position]

            holder.categoryTextView.text = getFinanceCategoryNameInLocalLanguage.execute(
                financeName = financeCategory.name,
                state = financeCategory.state,
                context = holder.itemView.context
            )

            holder.categoryBackgroundCardView.setCardBackgroundColor(
                Color.parseColor(
                    financeCategory.color
                )
            )
            holder.categoryImageTextView.text = String(Character.toChars(financeCategory.image))

            if (financeCategory.isChecked) {
                holder.categoryRootLayout.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.custom_border_filled
                )
                lastCheckedPosition = position
            } else
                holder.categoryRootLayout.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.custom_border_outline
                )

            holder.categoryRootLayout.setOnClickListener {
                if (lastCheckedPosition != LAST_CHECKED_POSITION_NOT_EXISTS) {
                    financeCategoryList[lastCheckedPosition].isChecked = false
                    notifyItemChanged(lastCheckedPosition)
                }
                lastCheckedPosition = position
                notifyItemChanged(lastCheckedPosition)
                financeCategory.isChecked = true
                onFinanceCategoryClickListener.onCategoryClick(financeCategory = financeCategory)
            }

            holder.categoryRootLayout.setOnLongClickListener {
                onFinanceCategoryClickListener.onCategoryLongClick(financeCategory = financeCategory)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return financeCategoryList.size + 1
    }
}