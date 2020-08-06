package com.wam.repository

import com.wam.model.Contact
import com.wam.model.Contacts

interface ContactRepository {
    suspend fun addContact(nameFirst: String,
                           nameMiddle: String,
                           nameLast: String,
                           addressStreet: String,
                           addressCity: String,
                           addressState: String,
                           addressZip: String,
                           telephone: Map<String, String>) : Contact?
    suspend fun getContactById(id: Int): Contact?
    suspend fun getAllContacts(): List<Contact?>
    suspend fun updateContactById(id: Int,
                                  nameFirst: String,
                                  nameMiddle: String,
                                  nameLast: String,
                                  addressStreet: String,
                                  addressCity: String,
                                  addressState: String,
                                  addressZip: String,
                                  telephone: Map<String, String>) : Boolean
    suspend fun deleteContactById(id: Int): Boolean
}