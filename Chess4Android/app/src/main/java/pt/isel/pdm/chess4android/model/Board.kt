package pt.isel.pdm.chess4android.model

import android.util.Log

const val BOARD_SIDE_SIZE: Int = 8
const val BOARDLENGHT: Int = BOARD_SIDE_SIZE * BOARD_SIDE_SIZE
private var chessPiecesTablePositions = arrayOf(

    ChessPieces.Rook('a', 8, false),
    ChessPieces.Knight('b', 8, false),
    ChessPieces.Bishop('c', 8, false),
    ChessPieces.Queen('d', 8,false),
    ChessPieces.King('e', 8, false),
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
    ChessPieces.Queen('d', 1,true),
    ChessPieces.King('e', 1, true),
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

    // BOOLEANS
    fun isNotEmptyPiece(index: Int) : Boolean = getPieceAtIndex(index).pieceType!=PIECETYPE.EMPTY

    fun isPositionWithPieceType(index: Int, pieceType: PIECETYPE) : Boolean {
        if(getPieceAtIndex(index).pieceType==pieceType) return true
        return false
    }

    fun areTherePiecesInTheWayBetween(){

    }

    // GET PIECE
    fun getPieceAtIndex(index: Int) : Piece {
        isOutOfBounds(index)
        return chessPiecesTablePositions[index]
    }

    // GET INDEX (and thus piece, and its properties), also can serve as method as isPositionWithPieceType : Boolean, per example

    // function use in a nutshell: if param is null, dont evaluate equality when searching for the index, otherwise, do evaluate.
    private fun getIndexesOfPieceWithConditions(column: Char?, line: Byte?, pieceType: PIECETYPE?, isWhite: Boolean?) : IntArray { // all in one get function. To make it as flexible as possible, we decided to return index, and the calling code wants the piece or whatever property from it, it will get it with the getPieceAtIndex
        var boolColumn = column!=null
        val boolLine = line!=null
        var boolPosition = boolColumn && boolLine
        var boolType = pieceType!=null
        var boolIsWhite = isWhite!=null
        var position: Position? = null
        var arrayOfMaxingIndexes = IntArray(BOARD_SIDE_SIZE) { -1 }
        if(boolPosition){
            try {
                position = Position(column!!, line!!) //according to our validation above, !! is fine and has to be here
            } catch (e: IllegalArgumentException){
                return arrayOfMaxingIndexes
            }

        } else if(boolColumn){
            if(!validXPositions.contains(column!!)) return arrayOfMaxingIndexes
        } else if(boolLine){
            if(!validYPositions.contains(line!!)) return arrayOfMaxingIndexes
        }

        var i = -1
        var index = 0
        for(piece in chessPiecesTablePositions){
            i++
            if(boolPosition) {
                if(! piece.position.isEqual(position!!) ) continue //according to our validation above, !! is fine and has to be here
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
            arrayOfMaxingIndexes[index++] = i
        }
        return arrayOfMaxingIndexes
    }

    // I had to add 2 to the end of the method, because if there are certain params that are null the compiler cannot know which overloaded method is to be called
    private fun getIndexOfPieceWithConditions2(column: Char?, line: Char?, pieceType: PIECETYPE?, isWhite: Boolean?) : IntArray = getIndexesOfPieceWithConditions(column, line?.digitToInt()?.toByte(), pieceType, isWhite )

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

    //returns false on fail, true on success
    /*
   * examples:
   * pawns: e4, exf5
   * others: Nf3, Nxg5, Nfg5 (on "conflict" of possibilities), Nfxg5 (on "conflict" of possibilities AND kills piece)
   * specials: O-O, O-O-O
   * at the end of these strings, ignores symbols like + and #
   */
    fun interpretMove(move: String, isWhite: Boolean) : Boolean { //very crucial, and complicated, since the movement registration in the json file are "compressed" in a sense. In other words, given a movement, we sometimes need to infer what piece can perform that move. Example: 2 knights: one in d2, another in g1, movement received: Nc4. g1 can't perform that movement, thus, the knight to move is the one in d2. This happens when there are pieces that can perform the movement are of the same type (same letter). This must be interpreted properly with logic.
        var move = move //since parameters are readonly, we can "shadow" the variable, make it like we can edit it
        if (move.length<7){ //if the length is not inferior to 7, it has to be a bug
            val pieceTypeToMove = letterToPieceType(move[0])
            if(move=="O-O") { //king castle
                //todo
            } else if(move=="0-0-0") { //queen castle
                /*val thePiece = getPieceAtIndex(getIndexesOfPieceWithConditions(null, null, PIECETYPE.KING, isWhite))
                val theKing: ChessPieces.King? = thePiece as? ChessPieces.King
                if(theKing!=null) { // I mean, is this even possible not to be true? hmm
                    val theRook: ChessPieces.Rook? = getPieceAtIndex(getIndexOfPieceWithConditions2(theKing.letter, theKing.letter, null, null)) as? ChessPieces.Rook
                    if(!theKing.firstMoveUsed && theKing.position.number.toInt() == 1 && theRook?.position?.number?.toInt()==1) { //check validity of the move according to the rules
                        //todo check if there are pieces in the way
                        areTherePiecesInTheWayBetween()
                    }
                }*/
            } else { //since we do the check for "O-O" before calling letterToPieceType, we're safe from executing this "If" if it's a "O-O", //If first letter is a valid chess X (letter) coordinate, then it was a pawn move, otherwise, it was another piece type
                if(pieceTypeToMove==PIECETYPE.PAWN){
                    if(false){ // and check lenght, move[2].isLetter()
                    //todo
                    } else {
                        val position = Position.convertToPosition(move)
                        if (position != null) {
                            val thePawn: ChessPieces.Pawn? = getPieceThatCanMoveTo(position, getIndexOfPieceWithConditions2(move[0], null, PIECETYPE.PAWN, isWhite)) as? ChessPieces.Pawn
                            if (thePawn != null) {
                                movePieceToAndLeaveEmptyBehind(position, thePawn)
                                return true
                            }
                        }
                    }
                } else { // other piece movement other than pawn
                    if(move[1]=='x'){
                        //todo
                    } else if(pieceTypeToMove==PIECETYPE.KNIGHT){
                        var column: Char? = null
                        if(move[2].isLetter()) {
                            column = move[1]
                            move = move.replaceFirst(move[1]+"", "", true)
                        }

                        val position = Position.convertToPosition(move.replaceFirstChar { "" })
                        if (position != null) {
                            val theKnight: ChessPieces.Knight? = getPieceThatCanMoveTo(position, getIndexOfPieceWithConditions2(column, null, PIECETYPE.KNIGHT, isWhite)) as? ChessPieces.Knight
                            if (theKnight != null) {
                                movePieceToAndLeaveEmptyBehind(position, theKnight)
                                return true
                            }
                        }
                    } else if(pieceTypeToMove==PIECETYPE.BISHOP){
                        val position = Position.convertToPosition(move.replaceFirstChar { "" })
                        if (position != null) {
                            val theBishop: ChessPieces.Bishop? = getPieceThatCanMoveTo(position, getIndexOfPieceWithConditions2(null, null, PIECETYPE.BISHOP, isWhite)) as? ChessPieces.Bishop
                            if (theBishop != null) {
                                movePieceToAndLeaveEmptyBehind(position, theBishop)
                                return true
                            }
                        }
                    }
                }

            }
        }
        log("Some interpretation failed")
        return false
    }

    private fun getPieceThatCanMoveTo(position: Position, array: IntArray) : Piece? {
        var piece: Piece?
        for(i in array){
            if(i==-1) break
            piece = getPieceAtIndex(i)
            if(piece.canMoveTo(position)) return piece //if the piece can perform the move, return it
        }
        return null
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

    private fun positionToIndex(position: Position) : Int {
        log("position->$position")
        val res : String = ((BOARD_SIDE_SIZE-position.number) * BOARD_SIDE_SIZE + letterToColumn(position.letter)).toString()
        log("to index -> $res")
        return (BOARD_SIDE_SIZE-position.number) * BOARD_SIDE_SIZE + letterToColumn(position.letter)
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