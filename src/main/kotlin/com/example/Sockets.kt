package com.example

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.Identity.encode
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.GameField
import java.time.Duration
import kotlin.random.Random

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
                    var numberOfXPlayed = 0
                    var numberOfOPlayed = 0
                    gameFieldList.forEachIndexed { index, entry ->
                        if (entry == "X") {
                            numberOfXPlayed++
                        } else if (entry == "O") {
                            numberOfOPlayed++
                        }
                    }
                    val firstEmptyOccurence = gameFieldList.indexOf("")
                    if (!firstEmptyOccurence.equals(-1))      {
                        gameFieldList[firstEmptyOccurence] = "X"
                    }
                    // PC plays player 2
                    println(text)
                    println("X: $numberOfXPlayed")
                    println("O: $numberOfOPlayed")
                    println(gameFieldList.toString())
                    val gameListReturn = GameField(gameFieldList)
                    println(gameListReturn)
                    val modifiedGameFieldJson  = Json.encodeToString(gameListReturn)
                    println(modifiedGameFieldJson)
                    outgoing.send(Frame.Text(modifiedGameFieldJson ))
                    //outgoing.send(Frame.Text(randomNumber.toString()))
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

