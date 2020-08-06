package restdemo

import restdemo.repository.H2ContactRepository
import restdemo.repository.DatabaseFactory
import restdemo.route.contacts
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.*
import restdemo.repository.IContactRepository
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)

    DatabaseFactory.initDatabase()
    val database = H2ContactRepository()

//    routing {
//        contacts(database)
//    }
    routeModule(database)
}

@kotlin.jvm.JvmOverloads
fun Application.routeModule(db: IContactRepository) {

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    routing {
        contacts(db)
    }
}

