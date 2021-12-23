package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.log
import pt.isel.pdm.chess4android.model.BOARD_SIDE_SIZE
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.PIECETYPE

//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import pt.isel.pdm.chess4android.model.Piece


/**
 * Custom view that implements a chess board.
 */
const val SIDE = BOARD_SIDE_SIZE
const val TAG = "BoardView"
var tileMatrix = arrayOfNulls<Tile>(SIDE*SIDE)
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private val blankIcon = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_blank, null)

    init { //will always read the startingChessPiecesTablePositions (because the companion object references it)
        rowCount = SIDE
        columnCount = SIDE
        repeat(SIDE * SIDE) {
            val row = it / SIDE
            val column = it % SIDE
            val piece = Board.companion_chessTable[it]
            val tile = Tile(ctx, (row + column) % 2 == 0, SIDE,
                ((if(row==1 || row==0 || row==6 || row==7) getDrawablePiece(piece.pieceType, piece.isWhite) //the if and else is not strictly necessary but its a little optimization
                else blankIcon)!!), if(row==7) indexToColumn(column) else null, null,
                it
            )
            tileMatrix[it] = tile
            addView(tile)
        }
    }

    private fun indexToColumn(it: Int): VectorDrawableCompat? {
        return when(it){
            0 -> getIcon(R.drawable.letter_a)
            1 -> getIcon(R.drawable.letter_b)
            2 -> getIcon(R.drawable.letter_c)
            3 -> getIcon(R.drawable.letter_d)
            4 -> getIcon(R.drawable.letter_e)
            5 -> getIcon(R.drawable.letter_f)
            6 -> getIcon(R.drawable.letter_g)
            7 -> getIcon(R.drawable.letter_h)
            else -> blankIcon
        }
    }

    private fun getDrawablePiece(pieceType: PIECETYPE, isWhite: Boolean) : VectorDrawableCompat? {
        return when(pieceType){
            PIECETYPE.PAWN -> if(isWhite) getIcon(R.drawable.ic_white_pawn) else getIcon(R.drawable.ic_black_pawn)
            PIECETYPE.BISHOP -> if(isWhite) getIcon(R.drawable.ic_white_bishop) else getIcon(R.drawable.ic_black_bishop)
            PIECETYPE.KNIGHT -> if(isWhite) getIcon(R.drawable.ic_white_knight) else getIcon(R.drawable.ic_black_knight)
            PIECETYPE.ROOK -> if(isWhite) getIcon(R.drawable.ic_white_rook) else getIcon(R.drawable.ic_black_rook)
            PIECETYPE.KING -> if(isWhite) getIcon(R.drawable.ic_white_king) else getIcon(R.drawable.ic_black_king)
            PIECETYPE.QUEEN -> if(isWhite) getIcon(R.drawable.ic_white_queen) else getIcon(R.drawable.ic_black_queen)
            else -> blankIcon
        }
    }

    fun invalidate(index: Int, piece: Piece){
        if(index<0 || index >= tileMatrix.size) throw IllegalArgumentException()
        val img = getDrawablePiece(piece.pieceType, piece.isWhite)
        if(img!=null){
            tileMatrix[index]?.setIcon(img)?.invalidate()
            //log("invalidated")
        } else log(TAG,"invalidate failed")
    }

    private fun getIcon(xmlID: Int): VectorDrawableCompat? = VectorDrawableCompat.create(ctx.resources, xmlID, null)

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }
}
