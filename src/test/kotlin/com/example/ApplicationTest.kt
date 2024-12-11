package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

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
            val message = "Hello, WebSocket!"
            send(Frame.Text(message))
            println("Client: $message")

            delay(2000L)
            val gameField = listOf("A", "B", "C", "", "", "", "", "", "")
            for (field in gameField) {
                send(Frame.Text(field))
            }
            delay(2000L)
            val gameFieldSer = GameField(gameField)
            val jsonString = Json.encodeToString(gameFieldSer)
            send(Frame.Text(jsonString))
            println("Length: ${jsonString.length}")

            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val frameText = frame.readText()
                if(frameText.contains("YOU SAID")) {
                    println("Client: ${frame.readText()}")
                    break
                }
            }
            val byeMessage = "bye"
            send(Frame.Text(byeMessage))
            println("Client: $byeMessage")
        }
        client.close()
    }
}

@Serializable
data class GameField(val gameField : List<String>)