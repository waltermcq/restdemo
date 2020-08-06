package restdemo

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.skyscreamer.jsonassert.JSONAssert
import restdemo.model.Contact
import restdemo.repository.IContactRepository

internal class ApplicationKtTest {

    @Test
    fun `GET _contacts returns list of contacts in dbRepo`() {
        val dbRepo = mockk<IContactRepository>()

        coEvery { dbRepo.getAllContacts() } returns listOf(
            Contact(1, "bob", "herbert", "smith", "123 Boylston St", "Boston", "MA", "02120", mutableMapOf("home" to "1234567890"))
        )    //coroutine every(); dbRepo always returns this contact

        withTestApplication({  //ktor mock startup
            routeModule(dbRepo)
        }) {
            with(handleRequest(HttpMethod.Get, "/contacts")) {  // test what the route returns
                assertEquals(HttpStatusCode.OK, response.status())

                JSONAssert.assertEquals("""
                    [
                        {
                            "contactId": 1,
                            "firstName": "bob",
                            "middleName": "herbert",
                            "lastName": "smith",
                            "street": "123 Boylston St",
                            "city": "Boston",
                            "state": "MA",
                            "zip": "02120",
                            "phone": {
                              "home": "1234567890"
                            }
                        }
                    ]
                """.trimIndent(), response.content, false)  // order if keys not important
                coVerify(exactly = 1) {dbRepo.getAllContacts()}
            }
        }
    }

    // test other routes

    // other things to test:

}

///*
//List all contacts
//GET /contacts
// */
//get(CONTACTS) {
//    call.respond(db.getAllContacts())
//}