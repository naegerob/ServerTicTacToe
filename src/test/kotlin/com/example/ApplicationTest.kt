package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import model.GameField
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testSocket() = testApplication {
        application {
            module()
        }
        val client = HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        client.webSocket(
            host = "localhost",
            port = 8080,
            path = "/tictactoe"
        ) {

            val gameField = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")
            val gameFieldSer = GameField(gameField)

            sendSerialized(gameFieldSer)

            val gamefieldReturn = receiveDeserialized<GameField>()
            assertEquals(gameFieldSer.toString(), gamefieldReturn.toString())
        }
        client.close()
    }
}

