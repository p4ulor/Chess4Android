package pt.isel.pdm.chess4android.model

import android.util.Log
import java.lang.IllegalArgumentException

val validXPositions = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
val validYPositions = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
//                      column,           line
data class Position(var letter: Char, val number: Byte) {
    init { //is called after primary constructor
        if(!isValid()) throw IllegalArgumentException()
    }
    private fun isValid() = validXPositions.contains(/*this.*/letter) && validYPositions.contains(/*this.*/number)
    private fun isValid(string: String) : Boolean { //probably useless, but if needed, I would need to include O, -, +, # and other special chars
        string.forEach { c ->
            if(c.isDigit()){
                if(!validYPositions.contains(c.code.toByte())) return false
            }
            else if(!validXPositions.contains(c)) return false
        }
        return true
    }

    override fun toString(): String = "Letter: $letter, Number: $number"
    fun isEqual(position: Position) : Boolean = this.letter==position.letter && this.number==position.number


    fun getXDiference(destination: Position) : Int = this.letter-destination.letter //int to make it (down casting to byte) easy 4 us
    fun getYDiference(destination: Position) : Int = this.number-destination.number //int to make it (down casting to byte) easy 4 us

    fun isValidMovement(destination: Position, maxX: Byte, maxY: Byte) : Boolean {
        if(maxX<0 || maxY<0 || maxX >= BOARD_SIDE_SIZE || maxY >= BOARD_SIDE_SIZE) throw IllegalArgumentException()
        val movedInX: Boolean = getXDiference(destination) in 1..maxX //is false if not between range [1, maxX]
        val movedInY: Boolean = getYDiference(destination) in 1..maxY
        return !(!movedInX && !movedInY) //is both X and Y axis movements are false, it returns false, otherwise, return true
    }

    //note, when setting a companion function to another function, this function must have the same parameters and compatiable return types
    companion object {
        fun isValid(string: String) : Boolean = isValid(string)
        fun convertToPosition(string: String) : Position? {
            if(string.length==2){
                try {
                    val position = Position(string[0], string[1].digitToInt().toByte())
                    return position
                } catch (e: Exception){
                    log(e.toString())
                }
            }
            return null
        }
    }
}

abstract class Piece (var position: Position, open var isWhite: Boolean) {
    abstract val pieceType: PIECETYPE //lowercase
    abstract val maxTravelDistanceX: Byte //positive value
    abstract val maxTravelDistanceY: Byte //positive value
    abstract fun moveTo(destination: Position): Boolean

    constructor(letter: Char, number: Byte, isWhite: Boolean) : this(Position(letter, number), isWhite)
}

sealed class ChessPieces { //https://antonioleiva.com/sealed-classes-kotlin/ //maybe not needed here?
    data class Pawn (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.PAWN
        override val maxTravelDistanceX: Byte = 2
        override val maxTravelDistanceY: Byte = 1

        private var firstMoveUsed: Boolean = false

         override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){ //part checks logical board bounds and piece maxTravelDistance bounds
                if(!firstMoveUsed && position.getYDiference(destination)<=2) {                //part that checks piece movement *logic*, and its this format for every moveTo method
                    /*this.*/position = destination
                    return true
                } else if(position.getYDiference(destination) == 1){
                    position = destination
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Pawn"
    }

    data class Bishop (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.BISHOP
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                if(position.getXDiference(destination) / position.getYDiference(destination)==0) {
                    position = destination
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Bishop"
    }

    data class Knight (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.KNIGHT
        override val maxTravelDistanceX: Byte = 0 //Knight is an exception
        override val maxTravelDistanceY: Byte = 0 //Knight is an exception

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                val x = position.getXDiference(destination)
                val y = position.getXDiference(destination)
                if(x==1 && y==2 || x==2 && y==1 ||x==2 && y==-1 || x==1 && y==-2 ||
                   x==-1 && y==-2 || x==-2 && y==-1 || x==-2 && y==1 || x==-1 && y==2) {
                    position = destination
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Knight"
    }

    data class Rook (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.ROOK
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                if(position.getXDiference(destination)==0) {
                    if (position.getYDiference(destination)!=0) {
                        position = destination
                        return true
                    }
                }
                else if (position.getYDiference(destination)==0) {
                    position = destination
                    return true
                }
            }
            return false
        }

        override fun toString(): String = "Rook"
    }

    data class King (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.KING
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 1

        var firstMoveUsed: Boolean = false

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                position = destination
                return true
            }
            return false
        }

        override fun toString(): String = "King"
    }

    data class Queen (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceType = PIECETYPE.QUEEN
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination, maxTravelDistanceX, maxTravelDistanceY)){
                position = destination
                return true
            }
            return false
        }

        override fun toString(): String = "Queen"
    }

    data class Empty (var letter: Char, val number: Byte) : Piece(letter, number, false) {
        override val pieceType = PIECETYPE.EMPTY
        override val maxTravelDistanceX: Byte = 0
        override val maxTravelDistanceY: Byte = 0

        override fun moveTo(destination: Position) : Boolean = false
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

fun letterToPieceType(char: Char) : PIECETYPE {
    return when(char) {
        'b' -> PIECETYPE.BISHOP
        'n' -> PIECETYPE.KNIGHT
        'r' -> PIECETYPE.ROOK
        'k' -> PIECETYPE.KING
        'q' -> PIECETYPE.QUEEN
        else -> PIECETYPE.PAWN
    }
}

private fun log(s: String) = Log.i("MY_LOG_ChessPieces", s)