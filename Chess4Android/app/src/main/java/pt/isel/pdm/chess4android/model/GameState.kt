package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data type used to represent the game state externally, that is, when the game state crosses
 * process boundaries and device boundaries.
 */
@Parcelize
data class GameState(
    val id: String,
    val colorPlaying: Boolean,
    val board: Array<FireBasePiece>
) : Parcelable

@Parcelize
data class FireBasePiece(
    val index: Byte,
    val isWhite: Boolean,
    val pieceType: PIECETYPE
) : Parcelable

/**
 * Extension to create a [GameState] instance from this [Board].
 */
/*fun Board.toGameState(gameId: String): GameState {
    val moves: String = toList().map {
        when(it) {
            null -> ' '
            User.CROSS -> 'X'
            User.CIRCLE -> 'O'
        }
    }.joinToString(separator = "")

    return GameState(id = gameId, turn = turn?.name, board = moves)
}

*//**
 * Extension to create a [Board] instance from this [GameState].
 *//*
fun GameState.toBoard() = Board(
    turn = if (turn != null) User.valueOf(turn) else null,
    board = board.toBoardContents()
)

*//**
 * Extension to create a list of moves from this string
 *//*
private fun String.toBoardContents(): List<Player?> = this.map {
    when(it) {
        'X' -> User.CROSS
        'O' -> User.CIRCLE
        else -> null
    }
}*/
