package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.log
import java.lang.IllegalArgumentException
import kotlin.math.abs
private const val TAG = "ChessPieces"
val validXPositions = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
val validYPositions = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
//                      column,           line
data class Position(var letter: Char, var number: Byte) {
    init { //is called after primary constructor
        if(!isValid()) throw IllegalArgumentException()
    }
    constructor(string: String) : this(string[0], string[1].digitToInt()?.toByte())

    private fun isValid() = validXPositions.contains(/*this.*/letter) && validYPositions.contains(/*this.*/number)
    private fun isValid(string: String) : Boolean {
        if(string.length!=2) return false
        if(validXPositions.contains(string[0]) && validYPositions.contains(string[1].code.toByte())) return true
        return false
    }

    fun horizontalyInvertPosition() : Position {
        number = (BOARD_SIDE_SIZE+1 - number).toByte() //https://stackoverflow.com/questions/16242259/reverse-number-in-a-range
        return this
    }

    override fun toString(): String = "Letter: $letter, Number: $number"
    fun letterAndNumber() : String ="$letter$number"
    fun isEqual(position: Position) : Boolean = this.letter==position.letter && this.number==position.number

    //these 2 methods bellow are useful for pieces that have specific and strict movement patterns, like pawns, rooks and bishops. Per example, kings and queens dont need these methods because they can move freely to any direction, but are only restricted by the movement distance. Thus, for these 2 examples, only isValidMovement is used.
    fun getXDiference(destination: Position) : Int = abs(this.letter-destination.letter) //int to make it (down casting to byte) easy 4 us
    fun getYDiference(destination: Position) : Int = abs(this.number-destination.number) //int to make it (down casting to byte) easy 4 us
    fun getYDiferenceNoAbs(destination: Position) : Int = (this.number-destination.number)

    fun isValidMovement(destination: Position, maxX: Byte, maxY: Byte) : Boolean {
        if(maxX<0 || maxY<0 || maxX >= BOARD_SIDE_SIZE || maxY >= BOARD_SIDE_SIZE) throw IllegalArgumentException()
        val movedInX: Boolean = getXDiference(destination) in 1..maxX //is false if not between range [1, maxX]
        val movedInY: Boolean = getYDiference(destination) in 1..maxY
        return !(!movedInX && !movedInY) //is both X and Y axis movements are false, it returns false, otherwise, return true
    }

    //note, when setting a companion function to another function, this function must have compatiable return types, and they tend to have the same parameters
    companion object {
        fun isValid(string: String) : Boolean = Position(string).isValid()
        fun convertToPosition(string: String) : Position? {
            if(string.length==2){
                try {
                    return Position(string[0], string[1].digitToInt().toByte())
                } catch (e: Exception) {
                    log(TAG, e.toString())
                }
            }
            return null
        }
    }
}

abstract class Piece (open var position: Position, open var isWhite: Boolean) {
    abstract val pieceType: PIECETYPE //lowercase
    abstract val maxTravelDistanceX: Byte //positive value
    abstract val maxTravelDistanceY: Byte //positive value
    abstract fun canMoveTo(destination: Position): Boolean

    constructor(letter: Char, number: Byte, isWhite: Boolean) : this(Position(letter, number), isWhite)

}

sealed class ChessPieces { //https://antonioleiva.com/sealed-classes-kotlin/ //maybe not needed here?

    data class Pawn (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.PAWN
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 2

        private var firstMoveUsed: Boolean = false

        override fun canMoveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){ //part checks logical board bounds and piece maxTravelDistance bounds
                if(!firstMoveUsed && isWhite && position.getYDiferenceNoAbs(destination)==-1 || position.getYDiferenceNoAbs(destination)==-2){ //kotlin ranges doesnt work with negative values... https://kotlinlang.org/docs/ranges.html
                    if(!firstMoveUsed) firstMoveUsed = true
                    return true
                } else if (!firstMoveUsed && !isWhite && (position.getYDiferenceNoAbs(destination) in 1..2)){
                    if(!firstMoveUsed) firstMoveUsed = true
                    return true
                } else if(isWhite && position.getYDiferenceNoAbs(destination) == -1){
                    return true
                } else if(!isWhite && position.getYDiferenceNoAbs(destination) == 1){ //the !isWhite && is actually necessary!
                    return true
                }
            }
            return false
        }

        fun movesDiagonally(destination: Position) : Boolean = position.getXDiference(destination)==1

        override fun toString(): String = "Pawn"
    }

    data class Bishop (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.BISHOP
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun canMoveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                if(position.getXDiference(destination)==position.getYDiference(destination)) {
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Bishop"
    }

    data class Knight (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.KNIGHT
        override val maxTravelDistanceX: Byte = 0 //Knight is an exception (we will do it hardcoded)
        override val maxTravelDistanceY: Byte = 0 //Knight is an exception (we will do it hardcoded)

        override fun canMoveTo(destination: Position) : Boolean {
            val x = position.getXDiference(destination)
            val y = position.getYDiference(destination)
            if(x==1 && y==2 || x==2 && y==1 ||x==2 && y==-1 || x==1 && y==-2 ||
               x==-1 && y==-2 || x==-2 && y==-1 || x==-2 && y==1 || x==-1 && y==2) { //honestly, a decent and simple approach, this replaces our "isValidMovement" method
                return true
            }
            return false
        }

        override fun toString(): String = "Knight"
    }

    data class Rook (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.ROOK
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        var firstMoveUsed: Boolean = false

        override fun canMoveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                if(position.getXDiference(destination)==0 && position.getYDiference(destination)!=0) {
                    if(!firstMoveUsed) firstMoveUsed = true
                    return true
                }
                else if (position.getXDiference(destination)!=0 && position.getYDiference(destination)==0) {
                    if(!firstMoveUsed) firstMoveUsed = true
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Rook"
    }

    data class King (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.KING
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 1

        var firstMoveUsed: Boolean = false

        override fun canMoveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                if(!firstMoveUsed) firstMoveUsed = true
                return true
            }
            return false
        }

        override fun toString(): String = "King"
    }

    data class Queen (val pos: Position, override var isWhite: Boolean) : Piece(pos, isWhite) {
        override val pieceType = PIECETYPE.QUEEN
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun canMoveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                return true
            }
            return false
        }

        override fun toString(): String = "Queen"
    }

    data class Empty (val pos: Position) : Piece(pos, false) {
        override val pieceType = PIECETYPE.EMPTY
        override val maxTravelDistanceX: Byte = 0
        override val maxTravelDistanceY: Byte = 0

        override fun canMoveTo(destination: Position) : Boolean = false
        override fun toString(): String = "Empty"
    }
}

enum class PIECETYPE {
    PAWN,
    BISHOP,
    KNIGHT,
    ROOK,
    KING,
    QUEEN,
    EMPTY
}

// UTILITY METHODS

fun letterToPieceType(char: Char) : PIECETYPE { //must be in uppercase just like in the json.
    return when(char) {
        'B' -> PIECETYPE.BISHOP
        'N' -> PIECETYPE.KNIGHT
        'R' -> PIECETYPE.ROOK
        'K' -> PIECETYPE.KING
        'Q' -> PIECETYPE.QUEEN
        else -> PIECETYPE.PAWN
    }
}

fun pieceToChessPieceCorrespondingToItsType(piece: Piece?, pieceType: PIECETYPE) : Piece? {
    if(piece==null) return null
    return when(pieceType){
        PIECETYPE.PAWN -> piece as ChessPieces.Pawn
        PIECETYPE.BISHOP -> piece as ChessPieces.Bishop
        PIECETYPE.KNIGHT -> piece as ChessPieces.Knight
        PIECETYPE.ROOK -> piece as ChessPieces.Rook
        PIECETYPE.KING -> piece as ChessPieces.King
        PIECETYPE.QUEEN -> piece as ChessPieces.Queen
        else -> null
    }
}

fun makePiece(index: Byte, pieceType: PIECETYPE, isWhite: Boolean) : Piece? {
    val position = Board.indexToPosition(index.toInt())
    return when(pieceType){
        PIECETYPE.PAWN ->  ChessPieces.Pawn(position, isWhite)
        PIECETYPE.BISHOP -> ChessPieces.Bishop(position, isWhite)
        PIECETYPE.KNIGHT -> ChessPieces.Knight(position, isWhite)
        PIECETYPE.ROOK -> ChessPieces.Rook(position, isWhite)
        PIECETYPE.KING -> ChessPieces.King(position, isWhite)
        PIECETYPE.QUEEN -> ChessPieces.Queen(position, isWhite)
        else -> null
    }
}