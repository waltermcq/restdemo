package restdemo.request

import restdemo.model.Address
import restdemo.model.ContactName
import restdemo.model.Phone

data class ContactApiRequest(
    val name: ContactName,
    val address: Address,
    val phone: List<Phone>
)