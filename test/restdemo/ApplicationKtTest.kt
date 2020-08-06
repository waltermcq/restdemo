package restdemo

import io.ktor.application.call
import io.ktor.http.*
import io.ktor.response.respond
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.skyscreamer.jsonassert.JSONAssert
import restdemo.model.Address
import restdemo.model.Contact
import restdemo.model.ContactName
import restdemo.model.Phone
import restdemo.repository.IContactRepository

internal class ApplicationKtTest {

    @Test
    fun `GET _contacts returns list of contacts in dbRepo`() {
        // create mock repo
        val dbRepo = mockk<IContactRepository>()

        // coroutine version of Every
        coEvery { dbRepo.getAllContacts() } returns listOf(
            Contact(1,
                ContactName("bob", "herbert", "smith"),
                Address("123 Boylston St",
                    "Boston",
                    "MA",
                    "02120"),
                listOf(Phone("home", "1234567890"))
            )
        )

        withTestApplication({  //ktor mock startup
            routeModule(dbRepo)
        }) {
            with(handleRequest(HttpMethod.Get, "/contacts")) {  // test what the route returns
                assertEquals(HttpStatusCode.OK, response.status())
                JSONAssert.assertEquals("""
                    [
                      {
                        "contactId": 1,
                        "name": {
                          "first": "bob",
                          "middle": "herbert",
                          "last": "smith"
                        },
                        "address": {
                          "street": "123 Boylston St",
                          "city": "Boston",
                          "state": "MA",
                          "zip": "02120"
                        },
                        "phone": [
                          {
                            "type": "home",
                            "number": "1234567890"
                          }
                        ]
                      }
                    ]
                """.trimIndent(), response.content, false)  // strict=false is when order of keys not important
                coVerify(exactly = 1) {dbRepo.getAllContacts()}
            }
        }
    }

    @Test
    fun `GET _contacts_id returns specific contact in dbRepo`() {
        // create mock repo
        val dbRepo = mockk<IContactRepository>()

        // coroutine version of Every
        coEvery { dbRepo.getContactById(1) } returns Contact(1,
                ContactName("bob", "herbert", "smith"),
                Address("123 Boylston St",
                    "Boston",
                    "MA",
                    "02120"),
                listOf(Phone("home", "1234567890")))

        withTestApplication({  //ktor mock startup
            routeModule(dbRepo)
        }) {
            with(handleRequest(HttpMethod.Get, "/contacts/1")) {  // test what the route returns
                assertEquals(HttpStatusCode.OK, response.status())

                JSONAssert.assertEquals("""
                      {
                        "contactId": 1,
                        "name": {
                          "first": "bob",
                          "middle": "herbert",
                          "last": "smith"
                        },
                        "address": {
                          "street": "123 Boylston St",
                          "city": "Boston",
                          "state": "MA",
                          "zip": "02120"
                        },
                        "phone": [
                          {
                            "type": "home",
                            "number": "1234567890"
                          }
                        ]
                      }
                """.trimIndent(), response.content, false)  // order if keys not important
                coVerify(exactly = 1) {dbRepo.getContactById(1)}
            }
        }
    }

    @Test
    fun `POST _contacts adds and returns new contact from dbRepo`() {
        val dbRepo = mockk<IContactRepository>()

        coEvery { dbRepo.addContact(ContactName("bob", "herbert", "smith"),
                                    Address("123 Boylston St",
                                        "Boston",
                                        "MA",
                                        "02120"),
                                    listOf(Phone("home", "1234567890"))
        )} returns Contact(1,
                           ContactName("bob", "herbert", "smith"),
                           Address("123 Boylston St",
                               "Boston",
                               "MA",
                               "02120"),
                           listOf(Phone("home", "1234567890")))

        withTestApplication({  //ktor mock startup
            routeModule(dbRepo)
        }) {
            with(handleRequest(HttpMethod.Post, "/contacts") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody("""
                        {
                            "name": {
                              "first": "bob",
                              "middle": "herbert",
                              "last": "smith"
                            },
                            "address": {
                              "street": "123 Boylston St",
                              "city": "Boston",
                              "state": "MA",
                              "zip": "02120"
                            },
                            "phone": [
                              {
                                "type": "home",
                                "number": "1234567890"
                              }
                            ]
                        }
                        """)
            }) {  // test what the route returns
                assertEquals(HttpStatusCode.OK, response.status())

                JSONAssert.assertEquals("""
                      {
                        "contactId": 1,
                        "name": {
                          "first": "bob",
                          "middle": "herbert",
                          "last": "smith"
                        },
                        "address": {
                          "street": "123 Boylston St",
                          "city": "Boston",
                          "state": "MA",
                          "zip": "02120"
                        },
                        "phone": [
                          {
                            "type": "home",
                            "number": "1234567890"
                          }
                        ]
                      }
                """.trimIndent(), response.content, false)  // order if keys not important
                coVerify(exactly = 1) {dbRepo.addContact(
                    ContactName("bob", "herbert", "smith"),
                    Address("123 Boylston St",
                        "Boston",
                        "MA",
                        "02120"),
                    listOf(Phone("home", "1234567890"))
                )}
            }
        }
    }

    // test other routes

    // other things to test:
    // the persistence layer using mock database(s)

}
