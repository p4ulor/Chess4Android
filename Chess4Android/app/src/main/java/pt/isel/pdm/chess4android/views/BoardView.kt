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
import pt.isel.pdm.chess4android.model.BOARDSIZE
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.PIECES

//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import pt.isel.pdm.chess4android.model.Piece


/**
 * Custom view that implements a chess board.
 */
const val side = BOARDSIZE
var tileMatrix = arrayOfNulls<Tile>(side*side)
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private val blankIcon = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_blank, null)

    init {
        rowCount = side
        columnCount = side
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(ctx, (row + column) % 2 == 0, side,
                ((if(row==1 || row==0 || row==6 || row==7) getDrawablePiece(Board.companion_chessTable[it]) //the if and else is not strictly necessary but its a little optimization
                else blankIcon)!!),
                it
            )
            tileMatrix[it] = tile
            addView(tile)
        }
    }

    private fun getDrawablePiece(piece: Piece) : VectorDrawableCompat? {
        return when(piece.pieceLetter){
            PIECES.PAWN -> if(piece.isWhite) getIcon(R.drawable.ic_white_pawn) else getIcon(R.drawable.ic_black_pawn)
            PIECES.BISHOP -> if(piece.isWhite) getIcon(R.drawable.ic_white_bishop) else getIcon(R.drawable.ic_black_bishop)
            PIECES.KNIGHT -> if(piece.isWhite) getIcon(R.drawable.ic_white_knight) else getIcon(R.drawable.ic_black_knight)
            PIECES.ROOK -> if(piece.isWhite) getIcon(R.drawable.ic_white_rook) else getIcon(R.drawable.ic_black_rook)
            PIECES.KING -> if(piece.isWhite) getIcon(R.drawable.ic_white_king) else getIcon(R.drawable.ic_black_king)
            PIECES.QUEEN -> if(piece.isWhite) getIcon(R.drawable.ic_white_queen) else getIcon(R.drawable.ic_black_queen)
            else -> blankIcon
        }
    }

    fun invalidate(index: Int){
        if(index<0 || index >= tileMatrix.size) throw IllegalArgumentException()
        val row = index / side
        val column = index % side
        val tile = Tile(ctx, (row + column) % 2 == 0, side, getDrawablePiece(Board.companion_chessTable[index]) ?: blankIcon!!, index)
        tileMatrix[index] = tile
        addView(tile)
        tile.invalidate()
        log("invalidated")
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

    private fun log(s: String) = Log.i("MY_LOG_AY", s)
}