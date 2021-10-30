package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import android.widget.TextView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.views.Tile.Type
import android.widget.Toast

//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


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

    private val readyChessTable = arrayOf(
        Position('a',8, R.drawable.ic_black_rook),
        Position('b',8, R.drawable.ic_black_knight),
        Position('c',8, R.drawable.ic_black_bishop),
        Position('d',8, R.drawable.ic_black_king),
        Position('e',8, R.drawable.ic_black_queen),
        Position('f',8, R.drawable.ic_black_bishop),
        Position('g',8, R.drawable.ic_black_knight),
        Position('h',8, R.drawable.ic_black_rook),

        Position('a',7, R.drawable.ic_black_pawn),
        Position('b',7, R.drawable.ic_black_pawn),
        Position('c',7, R.drawable.ic_black_pawn),
        Position('d',7, R.drawable.ic_black_pawn),
        Position('e',7, R.drawable.ic_black_pawn),
        Position('f',7, R.drawable.ic_black_pawn),
        Position('g',7, R.drawable.ic_black_pawn),
        Position('h',7, R.drawable.ic_black_pawn),

        Position('a',2, R.drawable.ic_white_pawn),
        Position('b',2, R.drawable.ic_white_pawn),
        Position('c',2, R.drawable.ic_white_pawn),
        Position('d',2, R.drawable.ic_white_pawn),
        Position('e',2, R.drawable.ic_white_pawn),
        Position('f',2, R.drawable.ic_white_pawn),
        Position('g',2, R.drawable.ic_white_pawn),
        Position('h',2, R.drawable.ic_white_pawn),

        Position('a',1, R.drawable.ic_white_rook),
        Position('b',1, R.drawable.ic_white_knight),
        Position('c',1, R.drawable.ic_white_bishop),
        Position('d',1, R.drawable.ic_white_king),
        Position('e',1, R.drawable.ic_white_queen),
        Position('f',1, R.drawable.ic_white_bishop),
        Position('g',1, R.drawable.ic_white_knight),
        Position('h',1, R.drawable.ic_white_rook),

    )

    private fun getIcon(xmlID: Int): VectorDrawableCompat? {
        return VectorDrawableCompat.create(ctx.resources, xmlID, null)
    }

    private val icon = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_blank, null)

    init {
        rowCount = side
        columnCount = side
        var i = 0
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(ctx, if((row + column) % 2 == 0) Type.WHITE else Type.BLACK, side,
                (if(row==1 || row==0 || row==6 || row==7) getIcon(readyChessTable[i++].icon)!!
                else icon)!!
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