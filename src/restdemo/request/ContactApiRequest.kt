package restdemo.request

import restdemo.model.Phone

data class ContactApiRequest(
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val phone: List<Phone>
)