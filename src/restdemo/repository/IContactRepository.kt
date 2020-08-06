package restdemo.repository

import restdemo.model.Address
import restdemo.model.Contact
import restdemo.model.ContactName
import restdemo.model.Phone

interface IContactRepository {
    suspend fun addContact(name: ContactName,
                           address: Address,
                           telephone: List<Phone>) : Contact?
    suspend fun getContactById(id: Int): Contact?
    suspend fun getAllContacts(): List<Contact?>
    suspend fun updateContactById(id: Int,
                                  name: ContactName,
                                  address: Address,
                                  telephone: List<Phone>) : Boolean
    suspend fun deleteContactById(id: Int): Boolean
}