package pt.isel.pdm.chess4android.model

import android.util.Log

const val BOARD_SIDE_SIZE: Int = 8
const val BOARDLENGHT: Int = BOARD_SIDE_SIZE * BOARD_SIDE_SIZE
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
        assert(chessPiecesTablePositions.size == BOARDLENGHT)
    }

    // *** GETS ***

    // AUXILIARY METHODS TO GET's

    // BOOLEANS
    fun isNotEmptyPiece(index: Int) : Boolean = getPieceAtIndex(index).pieceType!=PIECETYPE.EMPTY

    fun isPositionWithPieceType(index: Int, pieceType: PIECETYPE) : Boolean {
        if(getPieceAtIndex(index).pieceType==pieceType) return true
        return false
    }

    // GET PIECE
    fun getPieceAtIndex(index: Int) : Piece {
        isOutOfBounds(index)
        return chessPiecesTablePositions[index]
    }

    // GET INDEX (and thus piece, and its properties), also can serve as method as isPositionWithPieceType : Boolean, per example

    // function use in a nutshell: if param is null, dont evaluate equality when searching for the index, otherwise, do evaluate.
    private fun getIndexOfPieceWithConditions(column: Char?, line: Byte?, pieceType: PIECETYPE?, isWhite: Boolean?) : Int { // all in one get function. To make it as flexible as possible, we decided to return index, and the calling code wants the piece or whatever property from it, it will get it with the getPieceAtIndex
        var boolColumn = column!=null
        val boolLine = line!=null
        var boolPosition = boolColumn && boolLine
        var boolType = pieceType!=null
        var boolIsWhite = isWhite!=null
        var position: Position? = null

        if(boolPosition){
            try {
                position = Position(column!!, line!!) //according to our validation above, !! is fine and has to be here
            } catch (e: IllegalArgumentException){
                return -1
            }

        } else if(boolColumn){
            if(!validXPositions.contains(column!!)) return -1
        } else if(boolLine){
            if(!validYPositions.contains(line!!)) return -1
        }

        var i = -1
        for(piece in chessPiecesTablePositions){
            i++
            if(boolPosition) {
                if(! piece.position.isEqual(position!!) ) continue
            }
            if(boolColumn){
                if(piece.position.letter!=column) continue
            }
            if(boolLine){
                if(piece.position.number!=line) continue
            }
            if(boolType){
                if(piece.pieceType!=pieceType) continue
            }
            if(boolIsWhite){
                if(piece.isWhite!=isWhite) continue
            }
            return i
        }
        return -1
    }

    // I had to add 2 to the end of the method, because if there are certain params that are null the compiler cannot know which overloaded method is to be called
    private fun getIndexOfPieceWithConditions2(column: Char?, line: Char?, pieceType: PIECETYPE?, isWhite: Boolean?) : Int = getIndexOfPieceWithConditions(column, line?.code?.toByte(), pieceType, isWhite )

    // *** SETS ***

    private fun setPieceAtIndex(index: Int, piece: Piece) {
        if(isOutOfBounds(index)) return
        chessPiecesTablePositions[index]=piece
    }

    fun setPieceAtPosition(position: Position, piece: Piece) = setPieceAtIndex(positionToIndex(position), piece)

    // *** MOVEMENTS ***

    fun switchPiecesAtIndexes(index1: Int, index2: Int){
        if(isOutOfBounds(index1) || isOutOfBounds(index2)) return
        val piece1 = getPieceAtIndex(index1)
        val piece2 = getPieceAtIndex(index2)
        val auxPosition = piece1?.position
        if(piece2?.position!=null && auxPosition!=null){
            piece1?.position = piece2?.position
            piece2?.position = auxPosition
            setPieceAtIndex(index2, piece1)
            setPieceAtIndex(index1, piece2)
        }
    }

    fun movePieceToAndLeaveEmptyBehind(indexDestination: Int, pieceOrigin: Piece){
        if(isOutOfBounds(indexDestination)) return
        val auxPosition = pieceOrigin.position
        pieceOrigin.position = getPieceAtIndex(indexDestination)?.position!! //change the position of the piece to the position of the destination that its going to (change the value the object has)
        setPieceAtIndex(indexDestination, pieceOrigin) //change the array at the index of destination (change the positions at which the objects are located in the array)
        setPieceAtIndex(positionToIndex(auxPosition), ChessPieces.Empty(auxPosition.letter, auxPosition.number)) //change
    }

    private fun movePieceToAndLeaveEmptyBehind(position: Position, pieceOrigin: Piece) = movePieceToAndLeaveEmptyBehind(positionToIndex(position), pieceOrigin)

    private fun positionToIndex(position: Position) : Int {
        log("position->$position")
        val res : String = ((BOARD_SIDE_SIZE-position.number) * BOARD_SIDE_SIZE + letterToColumn(position.letter)).toString()
        log("to index -> $res")
        return (BOARD_SIDE_SIZE-position.number) * BOARD_SIDE_SIZE + letterToColumn(position.letter)
    }

    fun interpretMove(move: String, isWhite: Boolean) { //very crucial, and complicated, since the movement registration in the json file are "compressed" in a sense. In other words given, a movement, we sometimes need to infer what piece can perform that move. Example: 2 knights: one in d2, another in g1, movement received: nc4. g1 can't perform that movement, thus, the knight to move is the one in d2. This usually happens when the pieces that can perform the movement are of the same type (same letter). In case there's no letter, it's a pawn and this also must be interpreted properly with logic.
        when (move.length){
            2, 3, 4 -> { //its to move a pawn, example: e4 c6 d4 e5. a move that doesn't kill a piece or it's king side castle, example: Nf6 O-O, //a move that doesn't kill a piece but can perform a check or check mate, or a movement that kills a piece, or a pawn move that checks the king or other thing Examples: Qa5+ bd8# dxe5, d2+ (if first letter is a valid chess X (letter) coordinate, then it was a pawn move, otherwise, it was another piece type//a move that doesn't kill a piece but can perform a check or check mate, or a movement that kills a piece, or a pawn move that checks the king or other thing Examples: Qa5+ bd8# dxe5, d2+ (if first letter is a valid chess X (letter) coordinate, then it was a pawn move, otherwise, it was another piece type
                if (move.contains('x')){

                } else if (letterToPieceType(move[0])==PIECETYPE.PAWN){
                    val position = Position.convertToPosition(move)
                    val thePawn : ChessPieces.Pawn? =  getPieceAtIndex(getIndexOfPieceWithConditions2(move[0], null, PIECETYPE.PAWN, isWhite)) as? ChessPieces.Pawn
                    if(position!=null && thePawn!=null) movePieceToAndLeaveEmptyBehind(position, thePawn)
                } else { //non pawn movement that doesn't kill a piece

                }
            }
            4, 5 -> { //it's 0-0-0 (queen castle)
                if(move=="O-O") {
                    //todo
                }
                if(move=="0-0-0") {
                    val thePiece =  getPieceAtIndex(getIndexOfPieceWithConditions(null, null, PIECETYPE.KING, isWhite))
                    val theKing : ChessPieces.King? = thePiece as ChessPieces.King
                    if(theKing!=null) { // I mean, is this even possible not to be true? hmm
                        val theRook = getPieceAtIndex(getIndexOfPieceWithConditions2(theKing.letter, theKing.letter, null, null)) as ChessPieces.Rook
                        if(!theKing.firstMoveUsed && theKing.position.number.toInt() == 1 && theRook.position.number.toInt()==1) { //check validity of the move according to the rules
                            //todo check if there are pieces in the way
                        }
                    }
                }
            }
            else -> log("Some interpretation failed")
        }
    }

    // UTILITY METHODS
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

    private fun isOutOfBounds (index: Int) : Boolean {
        if( !(0<=index && index< (BOARD_SIDE_SIZE * BOARD_SIDE_SIZE)) ) {
            throw IllegalArgumentException("Index must be greater or equal to zero and bellow $BOARD_SIDE_SIZE")
            return true
        }
        return false
    }

    private fun log(s: String) = Log.i("MY_LOG_Board", s)
}