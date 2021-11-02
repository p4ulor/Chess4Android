package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.views.Tile.Type

//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import pt.isel.pdm.chess4android.model.Piece


/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private val side = 8

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    data class Position(val letter: Char, val number: Byte, val icon: Int)



    private fun getPiece(piece: Piece) : VectorDrawableCompat? {
        if(piece.isWhite){
            return when(piece.pieceLetter){
                ' ' -> getIcon(R.drawable.ic_white_pawn)
                'b' -> getIcon(R.drawable.ic_white_bishop)
                'n' -> getIcon(R.drawable.ic_white_knight)
                'r' -> getIcon(R.drawable.ic_white_rook)
                'k' -> getIcon(R.drawable.ic_white_king)
                'q' -> getIcon(R.drawable.ic_white_queen)
                else -> blankIcon
            }
        } else {
            return when(piece.pieceLetter){
                ' ' -> getIcon(R.drawable.ic_black_pawn)
                'b' -> getIcon(R.drawable.ic_black_bishop)
                'n' -> getIcon(R.drawable.ic_black_knight)
                'r' -> getIcon(R.drawable.ic_black_rook)
                'k' -> getIcon(R.drawable.ic_black_king)
                'q' -> getIcon(R.drawable.ic_black_queen)
                else -> blankIcon
            }
        }
    }

    private fun getIcon(xmlID: Int): VectorDrawableCompat? {
        return VectorDrawableCompat.create(ctx.resources, xmlID, null)
    }

    private val blankIcon = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_blank, null)

    init {
        rowCount = side
        columnCount = side
        var i = 0
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(ctx, if((row + column) % 2 == 0) Type.WHITE else Type.BLACK, side,
                (if(row==1 || row==0 || row==6 || row==7) getPiece(Board.companion_chessTable[i++])!!
                else blankIcon)!!
            )
            addView(tile)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }
}