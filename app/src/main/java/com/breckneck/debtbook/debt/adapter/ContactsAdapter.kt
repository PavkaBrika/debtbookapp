package com.breckneck.debtbook.debt.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R

class ContactsAdapter(
    private var contactsList: List<String>,
    val contactClickListener: OnContactClickListener
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    interface OnContactClickListener {
        fun onContactClick(contact: String, position: Int)
    }

    class ContactsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.contactName.text = contact

        holder.itemView.setOnClickListener {
            contactClickListener.onContactClick(contact = contact, position = position)
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceAll(contactsList: List<String>) {
        this.contactsList = contactsList
        notifyDataSetChanged()
    }
}