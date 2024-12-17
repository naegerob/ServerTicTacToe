package model

import kotlinx.serialization.Serializable

@Serializable
data class GameField(val gameField : MutableList<String>)
enum class Player(val player: String) {
    Player1("O"), // player 0 starts
    Player2("X"),
}