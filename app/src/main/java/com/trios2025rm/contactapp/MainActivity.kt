package com.trios2025rm.contactapp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : ComponentActivity() {
    private val contacts = mutableListOf<Contact>()
    private lateinit var adapter: ContactAdapter
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val addButton = findViewById<ExtendedFloatingActionButton>(R.id.addButton)

        adapter = ContactAdapter(contacts) { contact ->
            showContactDialog(contact)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            showContactDialog(null)
        }

        // Update contact count in toolbar
        updateContactCount()
    }

    private fun showContactDialog(contact: Contact?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.nameEdit)
        val phoneEdit = dialogView.findViewById<EditText>(R.id.phoneEdit)
        val emailEdit = dialogView.findViewById<EditText>(R.id.emailEdit)

        if (contact != null) {
            nameEdit.setText(contact.name)
            phoneEdit.setText(contact.phone)
            emailEdit.setText(contact.email)
        }

        AlertDialog.Builder(this)
            .setTitle(if (contact == null) "Add Contact" else "Edit Contact")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEdit.text.toString()
                val phone = phoneEdit.text.toString()
                val email = emailEdit.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    if (contact == null) {
                        contacts.add(Contact(name = name, phone = phone, email = email))
                    } else {
                        val index = contacts.indexOf(contact)
                        contacts[index] = contact.copy(name = name, phone = phone, email = email)
                    }
                    adapter.updateContacts(contacts.toList())
                    updateContactCount()
                }
            }
            .setNegativeButton("Cancel", null)
            .apply {
                if (contact != null) {
                    setNeutralButton("Delete") { _, _ ->
                        contacts.remove(contact)
                        adapter.updateContacts(contacts.toList())
                        updateContactCount()
                    }
                }
            }
            .show()
    }

    private fun updateContactCount() {
        toolbar.subtitle = "${contacts.size} Contact${if (contacts.size != 1) "s" else ""}"
    }
}