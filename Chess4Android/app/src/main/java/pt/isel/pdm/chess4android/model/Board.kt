package pt.isel.pdm.chess4android.model

import android.util.Log
import kotlin.math.abs

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

class Board {
    companion object { val companion_chessTable = chessPiecesTablePositions } //chessPiecesTablePositions will be read only for classes that want to acess it

    private var puzzle: String? = null
    constructor( puzzle: String) {
        this.puzzle = puzzle
    }

    fun getPieceColorAtPosition(position: Position) : Boolean? {
        chessPiecesTablePositions.forEach {
                piece -> if (piece.position.letter==position.letter && piece.position.number==position.number && piece.pieceLetter!=PIECES.EMPTY) return piece.isWhite
        }
        return null
    }

    fun isPositionEmpty(position: Position) : Boolean { //true = white, false = black
        chessPiecesTablePositions.forEach {
                piece -> if (piece.position.letter==position.letter && piece.position.number==position.number && piece.pieceLetter==PIECES.EMPTY) return false
        }
        return true
    }

    fun getIndexOfPieceWithPosition(position: Position) : Int {
        chessPiecesTablePositions.forEachIndexed {
                index, piece -> if (piece.position.letter==position.letter && piece.position.number==position.number ) return index
        }
        return -1
    }

    fun getPieceAtIndex(index: Int) : Piece? = if(index>=0) chessPiecesTablePositions[index] else null

    fun setPieceAtIndex(index: Int, piece: Piece) {
        if(index<0) throw IllegalArgumentException()
        chessPiecesTablePositions[index]=piece
    }

    fun switchPiecesAtIndexes(index1: Int, piece1: Piece, index2: Int, piece2: Piece){
        //todo
    }

    fun movePieceToAndLeaveEmptyBehind(indexDestination: Int, pieceOrigin: Piece){
        val auxPosition = pieceOrigin.position
        pieceOrigin.position = getPieceAtIndex(indexDestination)?.position!! //change the piece position to the destination that its going to
        setPieceAtIndex(indexDestination, pieceOrigin) //change the array
        setPieceAtIndex(positionToIndex(auxPosition), ChessPieces.Empty(auxPosition.letter, auxPosition.number))
    }

    private fun letterToColumn(char: Char) : Int {
        return when(char){
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            'h' -> 7
            else -> -1
        }
    }

    private fun positionToIndex(position: Position) : Int{
        log("position->$position")
        val res : String = (abs(8-(position.number)) * BOARDSIZE + letterToColumn(position.letter)).toString()
        log("to index -> $res")
        return abs(8-(position.number)) * BOARDSIZE + letterToColumn(position.letter)
        //return position.number * BOARDSIZE + letterToColumn(position.letter)
    }

    fun isNotEmptyPiece(index: Int) : Boolean {
        return getPieceAtIndex(index)?.pieceLetter!=PIECES.EMPTY
    }

    private fun log(s: String) = Log.i("MY_LOG_bruh", s)
}