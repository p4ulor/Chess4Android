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
    var isWhitePlaying: Boolean,
    var changedPos1: FireBasePiece?,
    var changedPos2: FireBasePiece?,
    var winnerColor: Boolean?
    //var board: Array<FireBasePiece>
) : Parcelable

@Parcelize
data class FireBasePiece(
    val index: Byte,
    val pieceType: PIECETYPE,
    val isWhite: Boolean
) : Parcelable

fun initBoard() : Array<FireBasePiece> {
    return arrayOf(
        FireBasePiece(0, PIECETYPE.ROOK, false),
        FireBasePiece(1, PIECETYPE.KNIGHT, false),
        FireBasePiece(2, PIECETYPE.BISHOP, false),
        FireBasePiece(3, PIECETYPE.QUEEN, false),
        FireBasePiece(4, PIECETYPE.KING, false),
        FireBasePiece(5, PIECETYPE.BISHOP, false),
        FireBasePiece(6, PIECETYPE.KNIGHT, false),
        FireBasePiece(7, PIECETYPE.ROOK, false),

        FireBasePiece(8, PIECETYPE.PAWN, false),
        FireBasePiece(9, PIECETYPE.PAWN, false),
        FireBasePiece(10, PIECETYPE.PAWN, false),
        FireBasePiece(11, PIECETYPE.PAWN, false),
        FireBasePiece(12, PIECETYPE.PAWN, false),
        FireBasePiece(13, PIECETYPE.PAWN, false),
        FireBasePiece(14, PIECETYPE.PAWN, false),
        FireBasePiece(15, PIECETYPE.PAWN, false),

        FireBasePiece(16, PIECETYPE.EMPTY, false),
        FireBasePiece(17, PIECETYPE.EMPTY, false),
        FireBasePiece(18, PIECETYPE.EMPTY, false),
        FireBasePiece(19, PIECETYPE.EMPTY, false),
        FireBasePiece(20, PIECETYPE.EMPTY, false),
        FireBasePiece(21, PIECETYPE.EMPTY, false),
        FireBasePiece(22, PIECETYPE.EMPTY, false),
        FireBasePiece(23, PIECETYPE.EMPTY, false),

        FireBasePiece(24, PIECETYPE.EMPTY, false),
        FireBasePiece(25, PIECETYPE.EMPTY, false),
        FireBasePiece(26, PIECETYPE.EMPTY, false),
        FireBasePiece(27, PIECETYPE.EMPTY, false),
        FireBasePiece(28, PIECETYPE.EMPTY, false),
        FireBasePiece(29, PIECETYPE.EMPTY, false),
        FireBasePiece(30, PIECETYPE.EMPTY, false),
        FireBasePiece(31, PIECETYPE.EMPTY, false),

        FireBasePiece(32, PIECETYPE.EMPTY, false),
        FireBasePiece(33, PIECETYPE.EMPTY, false),
        FireBasePiece(34, PIECETYPE.EMPTY, false),
        FireBasePiece(35, PIECETYPE.EMPTY, false),
        FireBasePiece(36, PIECETYPE.EMPTY, false),
        FireBasePiece(37, PIECETYPE.EMPTY, false),
        FireBasePiece(38, PIECETYPE.EMPTY, false),
        FireBasePiece(39, PIECETYPE.EMPTY, false),

        FireBasePiece(40, PIECETYPE.EMPTY, false),
        FireBasePiece(41, PIECETYPE.EMPTY, false),
        FireBasePiece(42, PIECETYPE.EMPTY, false),
        FireBasePiece(43, PIECETYPE.EMPTY, false),
        FireBasePiece(44, PIECETYPE.EMPTY, false),
        FireBasePiece(45, PIECETYPE.EMPTY, false),
        FireBasePiece(46, PIECETYPE.EMPTY, false),
        FireBasePiece(47, PIECETYPE.EMPTY, false),

        FireBasePiece(48, PIECETYPE.PAWN, true),
        FireBasePiece(49, PIECETYPE.PAWN, true),
        FireBasePiece(50, PIECETYPE.PAWN, true),
        FireBasePiece(51, PIECETYPE.PAWN, true),
        FireBasePiece(52, PIECETYPE.PAWN, true),
        FireBasePiece(53, PIECETYPE.PAWN, true),
        FireBasePiece(54, PIECETYPE.PAWN, true),
        FireBasePiece(55, PIECETYPE.PAWN, true),

        FireBasePiece(56, PIECETYPE.ROOK, true),
        FireBasePiece(57, PIECETYPE.KNIGHT, true),
        FireBasePiece(58, PIECETYPE.BISHOP, true),
        FireBasePiece(59, PIECETYPE.QUEEN, true),
        FireBasePiece(60, PIECETYPE.KING, true),
        FireBasePiece(61, PIECETYPE.BISHOP, true),
        FireBasePiece(62, PIECETYPE.KNIGHT, true),
        FireBasePiece(63, PIECETYPE.ROOK, true)
    )
}

//Extension to create a [Board] instance from this [GameState].
fun GameState.toBoard() : Board {
    val board = Board()
    changedPos1?.let { board.setPieceAtIndex(it) }
    changedPos2?.let { board.setPieceAtIndex(it) }
    return board
}

   /* turn = if (turn != null) User.valueOf(turn) else null,
    board = board.toBoardContents()*/

