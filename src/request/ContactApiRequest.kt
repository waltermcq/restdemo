package com.wam.request

data class ContactApiRequest(
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val phoneType: String,
    val phoneNumber : String
)