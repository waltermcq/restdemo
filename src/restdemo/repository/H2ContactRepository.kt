package restdemo.repository

import restdemo.model.Contact
import restdemo.model.ContactsTable
import restdemo.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import java.lang.IllegalArgumentException

class H2ContactRepository : IContactRepository {
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
                ContactsTable.insert {
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
            ContactsTable.select {
                (ContactsTable.id eq id)
            }.mapNotNull { serializeContact(it) }
                .singleOrNull()
        }

    override suspend fun getAllContacts(): List<Contact> {
        return dbQuery {
            ContactsTable
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
            ContactsTable.update({ ContactsTable.id eq id }) {
                it[firstName] = nameFirst
                it[middleName] = nameMiddle
                it[lastName] = nameLast
                it[street] = addressStreet
                it[city] = addressCity
                it[state] = addressState
                it[zip] = addressZip
                //TODO phoneType, phoneNumber
//                it[phoneType] = ""
//                it[phoneNumber] = ""
            } > 0
        }

    override suspend fun deleteContactById(id: Int): Boolean {
        // if the contact doesn't exist, throw the warning in this layer instead of from e.g. the DB
        if (getContactById(id) == null) {
            throw IllegalArgumentException("No contact with id $id exists")
        }
        return dbQuery {
            ContactsTable.deleteWhere { (ContactsTable.id eq id) } > 0
        }
    }

    //serialize
    private fun serializeContact(row: ResultRow): Contact {

        // add only valid phone numbers to map
        // find a cleaner / kotlin idiomatic way to do this, probably with a data class
        val validPhonesMap = mutableMapOf<String, String>()

        if (row[ContactsTable.phoneHome] != null) {
            validPhonesMap["home"] = row[ContactsTable.phoneHome]!!
        }
        if (row[ContactsTable.phoneWork] != null) {
            validPhonesMap["work"] = row[ContactsTable.phoneWork]!!
        }
        if (row[ContactsTable.phoneMobile] != null) {
            validPhonesMap["mobile"] = row[ContactsTable.phoneMobile]!!
        }

        return Contact(
            contactId = row[ContactsTable.id].value,
            firstName = row[ContactsTable.firstName],
            middleName = row[ContactsTable.middleName],
            lastName = row[ContactsTable.lastName],
            street = row[ContactsTable.street],
            city = row[ContactsTable.city],
            state = row[ContactsTable.state],
            zip = row[ContactsTable.zip],
            phone = validPhonesMap.toMap()
        )
    }
}