package com.wam.route

import io.ktor.routing.*

const val CONTACTS = "/contacts"
const val CONTACTS_ID = "/contacts/{id}"

fun Route.contacts() {

    /*
    List all contacts
    GET /contacts
     */
    get(CONTACTS) { TODO("implement")}

    /*
    Create a new contact
    POST /contacts
     */
    post(CONTACTS) { TODO("implement")}

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