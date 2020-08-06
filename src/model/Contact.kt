package com.wam.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.io.Serializable

data class Contact(val contactId: Int,
                   val firstName: String,
                   val middleName: String,
                   val lastName: String,
                   val street: String,
                   val city: String,
                   val state: String,
                   val zip: String,
                   val phone: MutableMap<String, String>) : Serializable // consider using an enum or wrapper for type

object Contacts : IntIdTable() {
    val firstName = varchar("first_name", 255)
    val middleName= varchar("middle_name", 255)
    val lastName= varchar("last_name", 255)
    val street= varchar("street", 255)
    val city= varchar("city", 255)
    val state= varchar("state", 255)
    val zip = varchar("zip", 64)
    // nulls preferable to empty strings for phone #s
    val phoneHome = varchar("phone_home", 64).nullable()
    val phoneMobile = varchar("phone_mobile", 64).nullable()
    val phoneWork = varchar("phone_work", 64).nullable()
}