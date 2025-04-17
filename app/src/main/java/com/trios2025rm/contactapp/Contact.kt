package com.trios2025rm.contactapp

data class Contact(
    var id: Long = -1,
    var name: String,
    var phone: String,
    var email: String = ""
) 