package com.wam

import com.wam.respository.DatabaseFactory
import com.wam.route.contacts
import io.ktor.application.*
import io.ktor.http.cio.websocket.FrameType.Companion.get
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.initDatabase()

    routing {
        get("/") {
            call.respondText("hello from restdemo")
        }
        contacts()
    }
}

