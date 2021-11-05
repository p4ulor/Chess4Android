package pt.isel.pdm.chess4android.model

data class Position(val letter: Char, val number: Byte)

abstract class Piece {
    abstract val pieceLetter: Char //lowercase
    abstract val possibleMovements: ArrayList<Direction>
    abstract val maxTravelDistanceX: Byte //positive value
    abstract val maxTravelDistanceY: Byte //positive value
    abstract fun isValidMovement(): Boolean
    var position: Position = Position(' ', -1 )
    var isWhite: Boolean = true
}

//todo rethink about these classes
sealed class ChessPieces (private val indicadedMovement: Direction) { //https://antonioleiva.com/sealed-classes-kotlin/
    data class Pawn (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = ' '
        override val possibleMovements = arrayListOf(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT) // TODO Do I need to include the inverse for black pieces?
        override val maxTravelDistanceX: Byte = 2
        override val maxTravelDistanceY: Byte = 1

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "Pawn"
    }

    data class Bishop (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = 'b'
        override val possibleMovements = arrayListOf(Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_RIGHT)
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "Bishop"
    }

    data class Knight (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = 'n'
        override val possibleMovements = arrayListOf(Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_RIGHT, Direction.DOWN_RIGHT)
        override val maxTravelDistanceX: Byte = 2
        override val maxTravelDistanceY: Byte = 2

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "Knight"
    }

    data class Rook (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = 'r'
        override val possibleMovements = arrayListOf(Direction.UP, Direction.DOWN,  Direction.LEFT, Direction.RIGHT)
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "Rook"
    }

    data class King (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = 'k'
        override val possibleMovements = arrayListOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT )
        override val maxTravelDistanceX: Byte = 1
        override val maxTravelDistanceY: Byte = 1

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "King"
    }

    data class Queen (private val indicadedMovement: Direction) : Piece() {
        override val pieceLetter = 'q'
        override val possibleMovements = arrayListOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT )
        override val maxTravelDistanceX: Byte = 7
        override val maxTravelDistanceY: Byte = 7

        constructor(letter: Char, number: Byte, white: Boolean) : this(Direction.STILL) {
            position = Position(letter, number)
            isWhite = white
        }

        override fun isValidMovement(): Boolean {
            if (possibleMovements.contains(indicadedMovement)) return true
            return false
        }

        override fun toString(): String = "Queen"
    }
}

enum class Direction (private var x: Byte, private var y: Byte) {
    UP(0,1),
    DOWN(0,-1),
    LEFT(-1,0),
    RIGHT(1,0),

    UP_LEFT(-1,1),
    UP_RIGHT(1,1),
    DOWN_LEFT(-1,-1),
    DOWN_RIGHT(1,-1),

    STILL(0,0);

    fun horizontalInverse(): Direction {
        return when(this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT

            UP_LEFT -> DOWN_LEFT
            UP_RIGHT -> DOWN_RIGHT
            DOWN_LEFT -> UP_LEFT
            DOWN_RIGHT -> UP_RIGHT
            else -> STILL
        }
    }
}