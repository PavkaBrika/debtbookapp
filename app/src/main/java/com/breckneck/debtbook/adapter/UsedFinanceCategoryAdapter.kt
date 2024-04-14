package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
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
import com.breckneck.debtbook.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.util.revenuesCategoryEnglishNameList
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class UsedFinanceCategoryAdapter(
    private val onUsedFinanceCategoryClickListener: OnUsedFinanceCategoryClickListener,
    private var currency: String
) : RecyclerView.Adapter<UsedFinanceCategoryAdapter.UsedFinanceCategoryViewHolder>() {

    private val decimalFormat = DecimalFormat("###,###,###.##")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val usedFinanceCategoryList: MutableList<FinanceCategoryWithFinances> = mutableListOf()
    val getFinanceCategoryNameInLocalLanguage = GetFinanceCategoryNameInLocalLanguage()

    interface OnUsedFinanceCategoryClickListener {
        fun onClick(usedFinance: FinanceCategoryWithFinances)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsedFinanceCategoryList(
        usedFinanceCategoryList: List<FinanceCategoryWithFinances>,
        currency: String
    ) {
        this.usedFinanceCategoryList.clear()
        this.usedFinanceCategoryList.addAll(usedFinanceCategoryList)
        this.currency = currency
        notifyDataSetChanged()
    }

    class UsedFinanceCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.usedCategoryNameTextView)
        val sumTextView: TextView = itemView.findViewById(R.id.usedCategorySumTextView)
        val percent: TextView = itemView.findViewById(R.id.usedCategoryPercent)
        val currency: TextView = itemView.findViewById(R.id.usedCategoryCurrencyTextView)
        val backgroundCardView: CardView = itemView.findViewById(R.id.categoryBackgroundCardView)
        val imageTextView: TextView = itemView.findViewById(R.id.categoryImageTextView)
        val rootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsedFinanceCategoryViewHolder {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_used_category, parent, false)
        return UsedFinanceCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsedFinanceCategoryViewHolder, position: Int) {
        val usedFinanceCategory = usedFinanceCategoryList[position]

        holder.nameTextView.text = getFinanceCategoryNameInLocalLanguage.execute(
            financeName = usedFinanceCategory.financeCategory.name,
            state = usedFinanceCategory.financeCategory.state,
            context = holder.itemView.context
        )

        holder.sumTextView.text = decimalFormat.format(usedFinanceCategory.categorySum)
        holder.percent.text = "${usedFinanceCategory.categoryPercentage}%"
        holder.currency.text = currency
        holder.backgroundCardView.setCardBackgroundColor(Color.parseColor(usedFinanceCategory.financeCategory.color))
        holder.imageTextView.text =
            String(Character.toChars(usedFinanceCategory.financeCategory.image))

        holder.rootLayout.setOnClickListener {
            onUsedFinanceCategoryClickListener.onClick(usedFinance = usedFinanceCategory)
        }
    }

    override fun getItemCount(): Int {
        return usedFinanceCategoryList.size
    }
}