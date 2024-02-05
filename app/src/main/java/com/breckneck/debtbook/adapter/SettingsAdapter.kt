package com.breckneck.debtbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R

class SettingsAdapter(
    private val settingsList: List<String>,
    private val selectedSetting: Int,
    val settingsClickListener: OnClickListener,
    val settingsSelectListener: OnSelectListener
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    interface OnClickListener {
        fun onClick(setting: String, position: Int)
    }

    interface OnSelectListener {
        fun onSelect()
    }

    class SettingsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val settingNameTextView: TextView = itemView.findViewById(R.id.settingNameTextView)
        val settingRadioButton: RadioButton = itemView.findViewById(R.id.settingRadioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val setting = settingsList[position]

        holder.settingNameTextView.text = setting
        if (selectedSetting == position)
            holder.settingRadioButton.isChecked = true

        holder.itemView.setOnClickListener {
            settingsSelectListener.onSelect()
            settingsClickListener.onClick(setting = setting, position = position)
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }
}