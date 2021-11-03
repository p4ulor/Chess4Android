package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.views.Tile.Type

//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import pt.isel.pdm.chess4android.model.Piece


/**
 * Custom view that implements a chess board.
 */
const val side = 8
var TileMatrix = arrayOfNulls<Tile>(side*side)
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {


    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private fun getPiece(piece: Piece) : VectorDrawableCompat? {
        return when(piece.pieceLetter){
            ' ' -> if(piece.isWhite) getIcon(R.drawable.ic_white_pawn) else getIcon(R.drawable.ic_black_pawn)
            'b' -> if(piece.isWhite) getIcon(R.drawable.ic_white_bishop) else getIcon(R.drawable.ic_black_bishop)
            'n' -> if(piece.isWhite) getIcon(R.drawable.ic_white_knight) else getIcon(R.drawable.ic_black_knight)
            'r' -> if(piece.isWhite) getIcon(R.drawable.ic_white_rook) else getIcon(R.drawable.ic_black_rook)
            'k' -> if(piece.isWhite) getIcon(R.drawable.ic_white_king) else getIcon(R.drawable.ic_black_king)
            'q' -> if(piece.isWhite) getIcon(R.drawable.ic_white_queen) else getIcon(R.drawable.ic_black_queen)
            else -> blankIcon
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
            TileMatrix[i]=tile
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