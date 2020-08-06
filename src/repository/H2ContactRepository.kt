package com.wam.repository

import com.wam.model.Contact
import com.wam.model.Contacts
import com.wam.respository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import java.lang.IllegalArgumentException

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
                    it[firstName] = nameFirst
                    it[middleName] = nameMiddle
                    it[lastName] = nameLast
                    it[street] = addressStreet
                    it[city] = addressCity
                    it[state] = addressState
                    it[zip] = addressZip
                    it[phoneHome] = telephone["home"]       // indexing is .get() which can return nulls
                    it[phoneMobile] = telephone["mobile"]
                    it[phoneWork] = telephone["work"]
                }
            val result = insertStatement.resultedValues?.get(0)  // if we dont need new contact, just return Unit
            if (result != null) {
                serializeContact(result)
            } else {
                null
            }
        }


    override suspend fun getContactById(id: Int): Contact? =
        dbQuery {
            Contacts.select {
                (Contacts.id eq id)
            }.mapNotNull { serializeContact(it) }
                .singleOrNull()
        }

    override suspend fun getAllContacts(): List<Contact> {
        return dbQuery {
            Contacts
                .selectAll()
                .map { serializeContact(it) }
        }
    }

    override suspend fun updateContactById(id: Int,
                                           nameFirst: String,
                                           nameMiddle: String,
                                           nameLast: String,
                                           addressStreet: String,
                                           addressCity: String,
                                           addressState: String,
                                           addressZip: String,
                                           telephone: Map<String, String>) : Boolean =
         dbQuery {
            Contacts.update({ Contacts.id eq id }) {
                it[firstName] = nameFirst
                it[middleName] = nameMiddle
                it[lastName] = nameLast
                it[street] = addressStreet
                it[city] = addressCity
                it[state] = addressState
                it[zip] = addressZip
                //TODO phoneType, phoneNumber
                it[phoneType] = ""
                it[phoneNumber] = ""
            } > 0
        }

    override suspend fun deleteContactById(id: Int): Boolean {
        // if the contact doesn't exist, throw the warning in this layer instead of from e.g. the DB
        if (getContactById(id) == null) {
            throw IllegalArgumentException("No contact with id $id exists")
        }
        return dbQuery {
            Contacts.deleteWhere { (Contacts.id eq id) } > 0
        }
    }

    //serialize
    private fun serializeContact(row: ResultRow): Contact {

        //Add only valid phone numbers to map
        //find a cleaner / kotlin idiomatic way to do this
        val validPhonesMap = mutableMapOf<String, String>()

        if (row[Contacts.phoneHome] != null) {
            validPhonesMap["home"] = row[Contacts.phoneHome]!!
        }
        if (row[Contacts.phoneWork] != null) {
            validPhonesMap["work"] = row[Contacts.phoneWork]!!
        }
        if (row[Contacts.phoneMobile] != null) {
            validPhonesMap["mobile"] = row[Contacts.phoneMobile]!!
        }

        return Contact(
            contactId = row[Contacts.id].value,
            firstName = row[Contacts.firstName],
            middleName = row[Contacts.middleName],
            lastName = row[Contacts.lastName],
            street = row[Contacts.street],
            city = row[Contacts.city],
            state = row[Contacts.state],
            zip = row[Contacts.zip],
            phone = validPhonesMap
        )
    }
}