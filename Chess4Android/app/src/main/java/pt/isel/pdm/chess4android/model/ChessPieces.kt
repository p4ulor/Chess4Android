package pt.isel.pdm.chess4android.model

import java.lang.IllegalArgumentException

val validXPositions = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
val validYPositions = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

data class Position(public var letter: Char, public val number: Byte) {
    init {
        if(!isValid()) throw IllegalArgumentException()
    }
    private fun isValid() = validXPositions.contains(letter) && validYPositions.contains(number)
    override fun toString(): String = "Letter: $letter, Number: $number"

    fun getXDiference(destination: Position) : Int = this.letter-destination.letter //int to make it easy 4 us
    fun getYDiference(destination: Position) : Int = this.number-destination.number //int to make it easy 4 us

    fun isValidMovement(destination: Position) : Boolean = getXDiference(destination) in 1..7 && getYDiference(destination) in 1..7

}

abstract class Piece (var position: Position, open val isWhite: Boolean) {
    abstract val pieceLetter: Char //lowercase
    abstract val maxTravelDistanceX: Byte //positive value
    abstract val maxTravelDistanceY: Byte //positive value
    abstract fun moveTo(destination: Position): Boolean

    constructor(letter: Char, number: Byte, isWhite: Boolean) : this(Position(letter, number), isWhite)
}

sealed class ChessPieces { //https://antonioleiva.com/sealed-classes-kotlin/
    data class Pawn (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = ' '
        override val maxTravelDistanceX: Byte = 2
        override val maxTravelDistanceY: Byte = 1

        var firstMoveUsed: Boolean = false

        override fun moveTo(destination: Position) : Boolean {
            if(position.isValidMovement(destination)){
                if(!firstMoveUsed && position.getYDiference(destination)<=2) {
                    position = destination
                } else if(position.getYDiference(destination) == 1){
                    position = destination
                }
            }
            return false
        }

        override fun toString(): String = "Pawn"
    }

    data class Bishop (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = 'b'
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "Bishop"
    }

    data class Knight (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = 'n'
        override val maxTravelDistanceX: Byte = 0 //Knight is an exception
        override val maxTravelDistanceY: Byte = 0 //Knight is an exception

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "Knight"
    }

    data class Rook (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = 'r'
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "Rook"
    }

    data class King (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = 'k'
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 1

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "King"
    }

    data class Queen (var letter: Char, val number: Byte, override val isWhite: Boolean) : Piece(letter, number, isWhite) {
        override val pieceLetter = 'q'
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "Queen"
    }

    data class Empty (var letter: Char, val number: Byte) : Piece(letter, number, false/*Irrelevant*/) {
        override val pieceLetter = '-'
        override val maxTravelDistanceX: Byte = 0
        override val maxTravelDistanceY: Byte = 0

        override fun moveTo(destination: Position) : Boolean {
            if(this.position.isValidMovement(destination)){
                //todo
            }
            return false
        }

        override fun toString(): String = "Empty"
    }
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

