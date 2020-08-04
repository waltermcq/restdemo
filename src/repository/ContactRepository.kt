package com.wam.repository

import com.wam.model.Contact

interface ContactRepository {
    suspend fun addContact(newContact: String): Contact
    suspend fun getContactById(id: Int): Contact?
    suspend fun getAllContacts(): List<Contact>
    suspend fun updateContactById(id: Int): Contact
    suspend fun deleteContactById(id: Int): Boolean
}