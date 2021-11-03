package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R

/**
 * Custom view that implements a chess board tile.
 * Tiles are either black or white and can they can be empty or occupied by a chess piece.
 *
 * Implementation note: This view is not to be used with the designer tool.
 * You need to adapt this view to suit your needs. ;)
 *
 * @property type           The tile's type (i.e. black or white)
 * @property tilesPerSide   The number of tiles in each side of the chess board
 */
@SuppressLint("ViewConstructor")
class Tile( private val ctx: Context, public val type: Type, private val tilesPerSide: Int, private val icon: VectorDrawableCompat) : View(ctx) {

    public val padding = 6 //the bigger this value, the smaller the chess-piece icon inside each Tile (square)

    //private val icon = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_knight, null)

    enum class Type { WHITE, BLACK }

    private val brush = Paint().apply {
        color = ctx.resources.getColor(if (type == Type.WHITE) R.color.chess_board_white else R.color.chess_board_black, null)
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = Integer.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(side / tilesPerSide, side / tilesPerSide)
    }

    override fun onDraw(canvas: Canvas){
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), brush)
        icon?.setBounds(padding, padding, width-padding, height-padding)
        icon?.draw(canvas)
    }
}