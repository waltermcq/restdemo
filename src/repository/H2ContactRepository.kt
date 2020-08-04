package com.wam.repository

import com.wam.model.Contact

class H2ContactRepository : ContactRepository {
    override suspend fun addContact(newContact: String): Contact {
        TODO("Not yet implemented")
    }

    override suspend fun getContactById(id: Int): Contact? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllContacts(): List<Contact> {
        TODO("Not yet implemented")
    }

    override suspend fun updateContactById(id: Int): Contact {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContactById(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}