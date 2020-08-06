package restdemo.repository

import restdemo.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import restdemo.model.*
import java.lang.IllegalArgumentException

class H2ContactRepository : IContactRepository {
    override suspend fun addContact(name: ContactName,
                                    address: Address,
                                    telephone: List<Phone>) : Contact? {

        // find cleaner / idiomatic way to do this

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
                    it[firstName] = name.first
                    it[middleName] = name.middle
                    it[lastName] = name.last
                    it[street] = address.street
                    it[city] = address.city
                    it[state] = address.state
                    it[zip] = address.zip
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
                                           name: ContactName,
                                           address: Address,
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
                it[firstName] = name.first
                it[middleName] = name.middle
                it[lastName] = name.last
                it[street] = address.street
                it[city] = address.city
                it[state] = address.state
                it[zip] = address.zip
                it[phoneHome] = homePhone
                it[phoneWork] = mobilePhone
                it[phoneMobile] = workPhone
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

        // refactor: it's more idiomatic to use ?.let {}
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
            name = ContactName(row[ContactsTable.firstName],
                               row[ContactsTable.middleName],
                               row[ContactsTable.lastName]),
            address = Address(row[ContactsTable.street],
                              row[ContactsTable.city],
                              row[ContactsTable.state],
                              row[ContactsTable.zip]),
            phone = validPhonesList.toList()
        )
    }
}