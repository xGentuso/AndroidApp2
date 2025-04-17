package com.trios2025rm.contactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onContactClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage: ShapeableImageView = view.findViewById(R.id.avatarImage)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val phoneText: TextView = view.findViewById(R.id.phoneText)
        val emailText: TextView = view.findViewById(R.id.emailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameText.text = contact.name
        holder.phoneText.text = contact.phone
        holder.emailText.text = contact.email

        // Set contact image or default avatar
        if (contact.imageResId != -1) {
            holder.avatarImage.setImageResource(contact.imageResId)
        } else {
            // Set a colored circle with the first letter of the name
            holder.avatarImage.setImageDrawable(null)
            holder.avatarImage.setBackgroundColor(getColorForName(contact.name))
            // Get first letter of name
            val initial = contact.name.firstOrNull()?.uppercase() ?: ""
            holder.avatarImage.contentDescription = "Contact avatar for ${contact.name}"
        }

        holder.itemView.setOnClickListener { onContactClick(contact) }
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    private fun getColorForName(name: String): Int {
        val colors = listOf(
            android.graphics.Color.parseColor("#FF6200EE"), // Purple
            android.graphics.Color.parseColor("#FF03DAC5"), // Teal
            android.graphics.Color.parseColor("#FF018786"), // Dark Teal
            android.graphics.Color.parseColor("#FFBB86FC")  // Light Purple
        )
        // Use the name's hashcode to pick a consistent color for each name
        return colors[Math.abs(name.hashCode()) % colors.size]
    }
} 