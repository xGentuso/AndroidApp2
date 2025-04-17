package com.trios2025rm.contactapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : ComponentActivity() {
    private val contacts = mutableListOf<Contact>()
    private lateinit var adapter: ContactAdapter
    private lateinit var toolbar: MaterialToolbar
    private var tempImageResId: Int = -1

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // For this example, we'll use drawable resources instead of picked images
            // In a real app, you would handle the selected image URI here
        }
    }

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

        // Prepopulate contacts
        prepopulateContacts()

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

    private fun prepopulateContacts() {
        contacts.add(Contact(
            name = "Coyote",
            phone = "123-456-7890",
            email = "coyote@wb.com",
            imageResId = R.drawable.coyote
        ))
        contacts.add(Contact(
            name = "Fudd",
            phone = "098-765-4321",
            email = "fudd@wb.com",
            imageResId = R.drawable.fudd
        ))
        contacts.add(Contact(
            name = "Bugs Bunny",
            phone = "555-123-4567",
            email = "bugsbunny@wb.com",
            imageResId = R.drawable.bunny
        ))
        contacts.add(Contact(
            name = "Daffy Duck",
            phone = "555-987-6543",
            email = "daffyfuck@wb.com",
            imageResId = R.drawable.duck
        ))
    }

    private fun showContactDialog(contact: Contact?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.nameEdit)
        val phoneEdit = dialogView.findViewById<EditText>(R.id.phoneEdit)
        val emailEdit = dialogView.findViewById<EditText>(R.id.emailEdit)
        val imageButton = dialogView.findViewById<ImageView>(R.id.imageButton)

        tempImageResId = contact?.imageResId ?: -1

        if (contact != null) {
            nameEdit.setText(contact.name)
            phoneEdit.setText(contact.phone)
            emailEdit.setText(contact.email)
            if (contact.imageResId != -1) {
                imageButton.setImageResource(contact.imageResId)
            }
        }

        // Set up image selection
        imageButton.setOnClickListener {
            showImageSelectionDialog()
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
                        contacts.add(Contact(
                            name = name,
                            phone = phone,
                            email = email,
                            imageResId = tempImageResId
                        ))
                    } else {
                        val index = contacts.indexOf(contact)
                        contacts[index] = contact.copy(
                            name = name, 
                            phone = phone, 
                            email = email,
                            imageResId = tempImageResId
                        )
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

    private fun showImageSelectionDialog() {
        val images = arrayOf(
            "Coyote" to R.drawable.coyote,
            "Fudd" to R.drawable.fudd,
            "Bugs" to R.drawable.bunny,
            "Daffy" to R.drawable.duck
        )

        val names = images.map { it.first }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Avatar")
            .setItems(names) { _, which ->
                tempImageResId = images[which].second
                val dialogView = (currentFocus?.parent as? View)?.findViewById<ImageView>(R.id.imageButton)
                dialogView?.setImageResource(tempImageResId)
            }
            .show()
    }

    private fun updateContactCount() {
        toolbar.subtitle = "${contacts.size} Contact${if (contacts.size != 1) "s" else ""}"
    }
}