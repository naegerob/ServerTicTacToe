package com.example

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import model.GameField
import java.time.Duration
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/tictactoe") {
            println("onConnect")
            try {
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    val gameField = Json.decodeFromString<GameField>(text)
                    val randomNumber = Random.nextInt(0, 9)
                    val gameFieldList = gameField.gameField
                    // TODO: think about something else than string shit

                    println(gameField)
                    outgoing.send(Frame.Text(text))
                }
            } catch (e: SerializationException) {
                println("Failed to deserialize: ${e.message}")
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                e.printStackTrace()
            }

        }
    }
}

