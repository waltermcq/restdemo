package restdemo.route

import restdemo.repository.IContactRepository
import restdemo.request.ContactApiRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.document
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

const val CONTACTS = "/contacts"
const val CONTACTS_ID = "/contacts/{id}"

fun Route.contacts(db: IContactRepository) {

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
        try {
            val apiRequest = call.receive<ContactApiRequest>()

            // input validation and sanitizer could go here

            val newContact = db.addContact(
                apiRequest.firstName,
                apiRequest.middleName,
                apiRequest.lastName,
                apiRequest.street,
                apiRequest.city,
                apiRequest.state,
                apiRequest.zip,
                mapOf(apiRequest.phoneType to apiRequest.phoneNumber)
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
    put(CONTACTS_ID) {
        try {
            val id = call.request.document() // the last component after '/' in uri
            val apiRequest = call.receive<ContactApiRequest>()

            // input validation and sanitizer could go here

            val isUpdated = db.updateContactById(id.toInt(),
                apiRequest.firstName,
                apiRequest.middleName,
                apiRequest.lastName,
                apiRequest.street,
                apiRequest.city,
                apiRequest.state,
                apiRequest.zip,
                mapOf(apiRequest.phoneType to apiRequest.phoneNumber)
            )
            if (isUpdated) {
                call.respondText("Contact id $id updated", status = HttpStatusCode.OK)
            } else {
                call.respondText("Contact id $id update failed", status = HttpStatusCode.InternalServerError)
            }
        }
        catch (e: Throwable) {
            call.respondText("Error: could not update contact", status = HttpStatusCode.InternalServerError)
        }
    }

    /*
    Get a specific contact
    GET /contacts/:id
     */
    get(CONTACTS_ID) {
        try {
            val id = call.request.document()
            val existingContact = db.getContactById(id.toInt())
            if (existingContact != null) {
                call.respond(existingContact)
            } else {
                call.respondText("Error: could not get contact for id $id", status = HttpStatusCode.InternalServerError)
            }
        }
        catch (e: Throwable) {
            call.respondText("Error: could not get contact", status = HttpStatusCode.InternalServerError)
        }
    }

    /*
    Delete a contact
    DELETE /contacts/:id
     */
    delete(CONTACTS_ID) {
        try {
            val id = call.request.document()
            val isDeleted = db.deleteContactById(id.toInt())
            if (isDeleted) {
                call.respondText("Contact id $id deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("Contact id $id delete failed", status = HttpStatusCode.InternalServerError)
            }
        }
        catch (e: Throwable) {
            call.respondText("Error: Could not delete contact", status = HttpStatusCode.InternalServerError)
        }
    }
}