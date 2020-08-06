package com.wam

import com.wam.repository.H2ContactRepository
import com.wam.respository.DatabaseFactory
import com.wam.route.contacts
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.cio.websocket.FrameType.Companion.get
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    DatabaseFactory.initDatabase()
    val database = H2ContactRepository()

    routing {
//        get("/") {
//            call.respondText("hello from restdemo")
//        }
        contacts(database)
    }
}

