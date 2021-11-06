package pt.isel.pdm.chess4android.model

const val BOARDSIZE: Int = 8

private var chessPiecesTablePositions = arrayOf(

    ChessPieces.Rook('a', 8, false),
    ChessPieces.Knight('b', 8, false),
    ChessPieces.Bishop('c', 8, false),
    ChessPieces.King('d', 8, false),
    ChessPieces.Queen('e', 8,false),
    ChessPieces.Bishop('f', 8, false),
    ChessPieces.Knight('g', 8, false),
    ChessPieces.Rook('h', 8, false),

    ChessPieces.Pawn('a', 7, false),
    ChessPieces.Pawn('b', 7, false),
    ChessPieces.Pawn('c', 7, false),
    ChessPieces.Pawn('d', 7, false),
    ChessPieces.Pawn('e', 7, false),
    ChessPieces.Pawn('f', 7, false),
    ChessPieces.Pawn('g', 7, false),
    ChessPieces.Pawn('h', 7, false),

    ChessPieces.Empty('a', 6),
    ChessPieces.Empty('b', 6),
    ChessPieces.Empty('c', 6),
    ChessPieces.Empty('d', 6),
    ChessPieces.Empty('e', 6),
    ChessPieces.Empty('f', 6),
    ChessPieces.Empty('g', 6),
    ChessPieces.Empty('h', 6),

    ChessPieces.Empty('a', 5),
    ChessPieces.Empty('b', 5),
    ChessPieces.Empty('c', 5),
    ChessPieces.Empty('d', 5),
    ChessPieces.Empty('e', 5),
    ChessPieces.Empty('f', 5),
    ChessPieces.Empty('g', 5),
    ChessPieces.Empty('h', 5),

    ChessPieces.Empty('a', 4),
    ChessPieces.Empty('b', 4),
    ChessPieces.Empty('c', 4),
    ChessPieces.Empty('d', 4),
    ChessPieces.Empty('e', 4),
    ChessPieces.Empty('f', 4),
    ChessPieces.Empty('g', 4),
    ChessPieces.Empty('h', 4),

    ChessPieces.Empty('a', 3),
    ChessPieces.Empty('b', 3),
    ChessPieces.Empty('c', 3),
    ChessPieces.Empty('d', 3),
    ChessPieces.Empty('e', 3),
    ChessPieces.Empty('f', 3),
    ChessPieces.Empty('g', 3),
    ChessPieces.Empty('h', 3),

    ChessPieces.Pawn('a', 2, true),
    ChessPieces.Pawn('b', 2, true),
    ChessPieces.Pawn('c', 2, true),
    ChessPieces.Pawn('d', 2, true),
    ChessPieces.Pawn('e', 2, true),
    ChessPieces.Pawn('f', 2, true),
    ChessPieces.Pawn('g', 2, true),
    ChessPieces.Pawn('h', 2, true),

    ChessPieces.Rook('a', 1, true),
    ChessPieces.Knight('b', 1, true),
    ChessPieces.Bishop('c', 1, true),
    ChessPieces.King('d', 1, true),
    ChessPieces.Queen('e', 1,true),
    ChessPieces.Bishop('f', 1, true),
    ChessPieces.Knight('g', 1, true),
    ChessPieces.Rook('h', 1, true)
)

class Board (private val puzzle: String) {
    companion object { val companion_chessTable = chessPiecesTablePositions } //chessPiecesTablePositions will be read only for classes that want to acess it



    fun isSpotWithoutAPiece(letter: Char, number: Byte) : Boolean {
        chessPiecesTablePositions.forEach {
                piece -> if (piece.position.letter==letter && piece.position.number==number) return false
        }
        return true
    }
}