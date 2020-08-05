package com.wam.repository

import com.wam.model.Contact
import com.wam.model.Contacts
import com.wam.respository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class H2ContactRepository : ContactRepository {
    override suspend fun addContact(nameFirst: String,
                                    nameMiddle: String,
                                    nameLast: String,
                                    addressStreet: String,
                                    addressCity: String,
                                    addressState: String,
                                    addressZip: String,
                                    telephone: Map<String, String>) : Contact? =
        dbQuery {
            val insertStatement =
                Contacts.insert {
                    it[firstName] = firstName
                    it[middleName] = middleName
                    it[lastName] = lastName
                    it[street] = street
                    it[city] = city
                    it[state] = state
                    it[zip] = zip
                    //phone
                }
            val result = insertStatement.resultedValues?.get(0)
            if (result != null) {
                serializeContact(result)
            } else {
                null
            }
        }


    override suspend fun getContactById(id: Int): Contact? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllContacts(): List<Contact> {
        return dbQuery {
            Contacts
                .selectAll()
                .map { serializeContact(it) }
        }
    }

    override suspend fun updateContactById(id: Int): Contact {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContactById(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    //serialize
    private fun serializeContact(row: ResultRow): Contact =
        Contact(contactId = row[Contacts.id].value,
                firstName = row[Contacts.firstName],
                middleName = row[Contacts.middleName],
                lastName = row[Contacts.lastName],
                street = row[Contacts.street],
                city = row[Contacts.city],
                state = row[Contacts.state],
                zip = row[Contacts.zip],
                phone = mapOf(row[Contacts.phoneType] to row[Contacts.phoneNumber])
        )
}