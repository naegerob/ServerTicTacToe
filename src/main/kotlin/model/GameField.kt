package model

import kotlinx.serialization.Serializable

@Serializable
data class GameField(val gameField : List<String>)