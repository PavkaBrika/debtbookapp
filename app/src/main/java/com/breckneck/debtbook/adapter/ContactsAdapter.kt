package com.breckneck.debtbook.adapter

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
    ): ContactsAdapter.ContactsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ContactsViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.contactName.text = contact

        holder.itemView.setOnClickListener {
            contactClickListener.onContactClick(contact = contact, position = position)
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    fun replaceAll(contactsList: List<String>) {
        this.contactsList = contactsList
        notifyDataSetChanged()
    }
}