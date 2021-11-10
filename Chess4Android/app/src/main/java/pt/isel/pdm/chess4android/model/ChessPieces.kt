package pt.isel.pdm.chess4android.model

import java.lang.IllegalArgumentException

val validXPositions = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
val validYPositions = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
//                      column,           line
data class Position(var letter: Char, val number: Byte) {
    init { //is called after primary constructor
        if(!isValid()) throw IllegalArgumentException()
    }
    private fun isValid() = validXPositions.contains(letter) && validYPositions.contains(number)
    override fun toString(): String = "Letter: $letter, Number: $number"

    fun getXDiference(destination: Position) : Int = this.letter-destination.letter //int to make it easy 4 us
    fun getYDiference(destination: Position) : Int = this.number-destination.number //int to make it easy 4 us

    fun isValidMovement(destination: Position, maxX: Byte, maxY: Byte) : Boolean {
        if(maxX<0 || maxY<0 || maxX >= BOARDSIZE || maxY >= BOARDSIZE) throw IllegalArgumentException()
        val movedInX: Boolean = getXDiference(destination) in 1..maxX //is false if not between range [1, maxX]
        val movedInY: Boolean = getYDiference(destination) in 1..maxY
        return !(!movedInX && !movedInY) //is both X and Y axis movements are false, it returns false, otherwise, return true
    }
}

abstract class Piece (var position: Position, open var isWhite: Boolean) {
    abstract val pieceLetter: PIECES //lowercase
    abstract val maxTravelDistanceX: Byte //positive value
    abstract val maxTravelDistanceY: Byte //positive value
    abstract fun moveTo(destination: Position): Boolean

    constructor(letter: Char, number: Byte, isWhite: Boolean) : this(Position(letter, number), isWhite)

}

sealed class ChessPieces { //https://antonioleiva.com/sealed-classes-kotlin/ //maybe not needed here
    data class Pawn (var letter: Char, val number: Byte, override var isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = PIECES.PAWN
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
        override val pieceLetter = PIECES.BISHOP
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
        override val pieceLetter = PIECES.KNIGHT
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
        override val pieceLetter = PIECES.ROOK
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
        override val pieceLetter = PIECES.KING
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 1

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
        override val pieceLetter = PIECES.QUEEN
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
        override val pieceLetter = PIECES.EMPTY
        override val maxTravelDistanceX: Byte = 0
        override val maxTravelDistanceY: Byte = 0

        override fun moveTo(destination: Position) : Boolean = false
        override fun toString(): String = "Empty"
    }
}

enum class PIECES {
    PAWN,
    BISHOP,
    KNIGHT,
    ROOK,
    KING,
    QUEEN,
    EMPTY
}

/*
   UP(0,1),
    DOWN(0,-1),
    LEFT(-1,0),
    RIGHT(1,0),

    UP_LEFT(-1,1),
    UP_RIGHT(1,1),
    DOWN_LEFT(-1,-1),
    DOWN_RIGHT(1,-1),

    SPECIAL_KNIGHT_1OCLK(1,2),  SPECIAL_KNIGHT_2OCLK(2,1),  SPECIAL_KNIGHT_4OCLK(2,-1),  SPECIAL_KNIGHT_5OCLK(1,-2),
    SPECIAL_KNIGHT_7OCLK(-1,-2),  SPECIAL_KNIGHT_8OCLK(-2,-1),  SPECIAL_KNIGHT_10OCLK(-2,1),  SPECIAL_KNIGHT_11OCLK(-1,2),
 */

