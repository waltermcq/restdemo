package com.wam.route

import com.wam.repository.ContactRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

const val CONTACTS = "/contacts"
const val CONTACTS_ID = "/contacts/{id}"

fun Route.contacts(db: ContactRepository) {

    /*
    List all contacts
    GET /contacts
     */
    get(CONTACTS) {
        call.respond(db.getAllContacts())
    }

    /*
    Create a new contact
    POST /contacts
     */
    post(CONTACTS) {
        val params = call.receiveParameters()

        val firstName = params["firstName"] ?: throw IllegalArgumentException("Missing param: firstName")
        val middleName = params["middleName"] ?: throw IllegalArgumentException("Missing param: middleName")
        val lastName = params["lastName"] ?: throw IllegalArgumentException("Missing param: lastName")
        val street = params["street"] ?: throw IllegalArgumentException("Missing param: street")
        val city = params["city"] ?: throw IllegalArgumentException("Missing param: city")
        val state = params["state"] ?: throw IllegalArgumentException("Missing param: state")
        val zip = params["zip"] ?: throw IllegalArgumentException("Missing param: zip")
        val phoneNumber = params["phoneNumber"] ?: throw IllegalArgumentException("Missing param: phoneNumber")
        val phoneType = params["lastName"] ?: throw IllegalArgumentException("Missing param: phoneType")

        // input validation and sanitizer goes here
        try {
            val newContact = db.addContact(
                firstName,
                middleName,
                lastName,
                street,
                city,
                state,
                zip,
                mapOf(phoneType to phoneNumber)
            )
            if (newContact != null) {
                call.respond(newContact)
            } else {
                call.respondText("Error: could not create contact", status = HttpStatusCode.InternalServerError)
            }
        }
        catch (e: Throwable) {
            call.respondText("Error: could not create contact", status = HttpStatusCode.InternalServerError)
        }
    }

    /*
    Update a contact
    PUT /contacts/:id
     */
    put(CONTACTS_ID) { TODO("implement")}

    /*
    Get a specific contact
    GET /contacts/:id
     */
    get(CONTACTS_ID) { TODO("implement")}

    /*
    Delete a contact
    DELETE /contacts/:id
     */
    delete(CONTACTS_ID) { TODO("implement")}
}