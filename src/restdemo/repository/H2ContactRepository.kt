package restdemo.repository

import restdemo.model.Contact
import restdemo.model.ContactsTable
import restdemo.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import restdemo.model.Phone
import java.lang.IllegalArgumentException

class H2ContactRepository : IContactRepository {
    override suspend fun addContact(nameFirst: String,
                                    nameMiddle: String,
                                    nameLast: String,
                                    addressStreet: String,
                                    addressCity: String,
                                    addressState: String,
                                    addressZip: String,
                                    telephone: List<Phone>) : Contact? {

        val homePhoneList = telephone.filter {it.type == "home"}
        val workPhoneList = telephone.filter {it.type == "work"}
        val mobilePhoneList = telephone.filter {it.type == "mobile"}

        val homePhone: String?
        val workPhone: String?
        val mobilePhone: String?

        homePhone = if (homePhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }
        workPhone = if (workPhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }
        mobilePhone = if (mobilePhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }

        return dbQuery {
            val insertStatement =
                ContactsTable.insert {
                    it[firstName] = nameFirst
                    it[middleName] = nameMiddle
                    it[lastName] = nameLast
                    it[street] = addressStreet
                    it[city] = addressCity
                    it[state] = addressState
                    it[zip] = addressZip
                    it[phoneHome] = homePhone
                    it[phoneWork] =  workPhone
                    it[phoneMobile] = mobilePhone
                }
            val result = insertStatement.resultedValues?.get(0)  // if we dont need new contact, just return Unit
            if (result != null) {
                serializeContact(result)
            } else {
                null
            }
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
                                           telephone: List<Phone>) : Boolean {

        val homePhoneList = telephone.filter {it.type == "home"}
        val workPhoneList = telephone.filter {it.type == "work"}
        val mobilePhoneList = telephone.filter {it.type == "mobile"}

        val homePhone: String?
        val workPhone: String?
        val mobilePhone: String?

        homePhone = if (homePhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }
        workPhone = if (workPhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }
        mobilePhone = if (mobilePhoneList.isNotEmpty()) {
            homePhoneList[0].number } else { null }

        return dbQuery {
            ContactsTable.update({ ContactsTable.id eq id }) {
                it[firstName] = nameFirst
                it[middleName] = nameMiddle
                it[lastName] = nameLast
                it[street] = addressStreet
                it[city] = addressCity
                it[state] = addressState
                it[zip] = addressZip
                it[phoneHome] = homePhone
                it[phoneMobile] = workPhone
                it[phoneWork] = mobilePhone
            } > 0
        }
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

        // add only valid (wrapped) phone numbers to list
        val validPhonesList = mutableListOf<Phone>()

        // refactor: more idiomatic to use ?.let {}
        if (row[ContactsTable.phoneHome] != null) {
            validPhonesList.add(Phone(type="home", number=row[ContactsTable.phoneHome]!!))
        }
        if (row[ContactsTable.phoneWork] != null) {
            validPhonesList.add(Phone(type="work", number=row[ContactsTable.phoneWork]!!))
        }
        if (row[ContactsTable.phoneMobile] != null) {
            validPhonesList.add(Phone(type="mobile", number=row[ContactsTable.phoneMobile]!!))
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
            phone = validPhonesList.toList()
        )
    }
}