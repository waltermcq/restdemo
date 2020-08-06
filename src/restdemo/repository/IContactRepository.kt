package restdemo.repository

import restdemo.model.Contact
import restdemo.model.Phone

interface IContactRepository {
    suspend fun addContact(nameFirst: String,
                           nameMiddle: String,
                           nameLast: String,
                           addressStreet: String,
                           addressCity: String,
                           addressState: String,
                           addressZip: String,
                           telephone: List<Phone>) : Contact?
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
                                  telephone: List<Phone>) : Boolean
    suspend fun deleteContactById(id: Int): Boolean
}