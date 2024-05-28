package com.breckneck.debtbook.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.HumanDomain
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class GoalAdapter(
    goalListImmutable: List<Goal>,
    private val goalClickListener: OnGoalClickListener
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private val TAG = "GoalAdapter"
    private val decimalFormat = DecimalFormat("###,###,###.##")
    private val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
    private val goalList: MutableList<Goal> = goalListImmutable.toMutableList()

    interface OnGoalClickListener{
        fun onGoalClick(goal: Goal, position: Int)

        fun onAddButtonClick(goal: Goal, position: Int)
    }

    fun updateGoal(goal: Goal, position: Int) {
        goalList[position] = goal
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGoalList(goalList: List<Goal>) {
        this.goalList.clear()
        this.goalList.addAll(goalList)
        notifyDataSetChanged()
    }

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.goalNameTextView)
        val savedMoneyTextView: TextView = itemView.findViewById(R.id.goalSavedMoneyTextView)
        val sumTextView: TextView = itemView.findViewById(R.id.goalSumTextView)
        val goalRemainingMoneyTextView: TextView = itemView.findViewById(R.id.goalRemainingMoneyTextView)
        val goalImageView: ImageView = itemView.findViewById(R.id.goalImageView)

        val goalSavedMoneyCurrencyTextView: TextView = itemView.findViewById(R.id.goalSavedMoneyCurrencyTextView)
        val goalSumCurrencyTextView: TextView = itemView.findViewById(R.id.goalSumCurrencyTextView)
        val goalRemainingMoneyCurrencyTextView: TextView = itemView.findViewById(R.id.goalRemainingMoneyCurrencyTextView)

        val addButton: Button = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
        val goal = goalList[position]
        holder.nameTextView.text = goal.name
        holder.savedMoneyTextView.text = decimalFormat.format(goal.savedSum)
        holder.sumTextView.text = decimalFormat.format(goal.sum)
        holder.goalRemainingMoneyTextView.text = decimalFormat.format(goal.sum - goal.savedSum)

        holder.goalSavedMoneyCurrencyTextView.text = goal.currency
        holder.goalSumCurrencyTextView.text = goal.currency
        holder.goalRemainingMoneyCurrencyTextView.text = goal.currency

        if ((goal.photoPath != null) && (File(goal.photoPath!!).exists())) {
            holder.goalImageView.visibility = View.VISIBLE
            val result = Glide.with(holder.goalImageView.context)
                .load(goal.photoPath)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.goalImageView.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(holder.goalImageView)
        } else {
            holder.goalImageView.visibility = View.GONE
        }

        holder.addButton.setOnClickListener {
            goalClickListener.onAddButtonClick(goal = goal, position = position)
        }
    }

    override fun getItemCount(): Int {
        return goalList.size
    }
}