package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import model.GameField

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
    fun testSocketV1() = testApplication {
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

            val gameFieldRef = mutableListOf("X", "O", "O", "X", "", "O", "X", "", "")
            val gameField = mutableListOf("X", "O", "O", "", "", "O", "X", "", "")
            val gameFieldSer = GameField(gameField)

            sendSerialized(gameFieldSer)
            val gamefieldSerReturn = receiveDeserialized<GameField>()
            assertEquals(gameFieldRef.toString(), gamefieldSerReturn.gameField.toString())
        }
        client.close()
    }

    @Test
    fun testSocketV2() = testApplication {
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

            val gameFieldRef = mutableListOf("X", "O", "O", "X", "X", "O", "X", "O", "")
            val gameField = mutableListOf("X", "O", "O", "X", "", "O", "X", "O", "")
            val gameFieldSer = GameField(gameField)

            sendSerialized(gameFieldSer)
            val gamefieldSerReturn = receiveDeserialized<GameField>()
            assertEquals(gameFieldRef.toString(), gamefieldSerReturn.gameField.toString())
        }
        client.close()
    }
}

