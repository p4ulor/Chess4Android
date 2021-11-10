package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.ChessPieces
import pt.isel.pdm.chess4android.model.PIECES
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null
    private lateinit var myView: BoardView

    private lateinit var board: Board

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        lichessGameOfTheDayPuzzle = intent.getStringArrayExtra(PUZZLE)
        lichessGameOfTheDaySolution = intent.getStringArrayExtra(SOLUTION)

        board = Board(lichessGameOfTheDayPuzzle.toString())

        myView = findViewById(R.id.boardView)

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener{
                if(currentlySelectedPieceIndex==-1) {
                    currentlySelectedPieceIndex = tile.index
                    log("picked")
                }
                else {
                    log("preparing to move a piece")
                    val pieceToMove = board.getPieceAtIndex(currentlySelectedPieceIndex)
                    val pieceThatWillBeOverwrittenIndex = tile.index
                    val theNewPosition = board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex)?.position

                    if(theNewPosition!=null && pieceToMove!=null && board.isNotEmptyPiece(currentlySelectedPieceIndex) && pieceThatWillBeOverwrittenIndex!=currentlySelectedPieceIndex) {
                        board.movePieceToAndLeaveEmptyBehind(pieceThatWillBeOverwrittenIndex, pieceToMove)
                        myView.invalidate(pieceThatWillBeOverwrittenIndex, board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex)!!) //new pos
                        myView.invalidate(currentlySelectedPieceIndex, board.getPieceAtIndex(currentlySelectedPieceIndex)!!  ) //old pos
                        log("moved")
                    }
                    currentlySelectedPieceIndex = -1
                }
                //val name: String = tile.piece?.toString() ?: "Empty"
                //val color: String =if(tile.piece?.isWhite!!) "White" else "Black"
                //Log.i(TAG, color+" "+name+ " in "+tile.piece?.position?.toString()) //very interesting, kotlin. tile.type.toString().lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }

    }

    override fun onBackPressed() {
        toast(R.string.progressLost)
        super.onBackPressed()
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

    private fun log(s: String) = Log.i(TAG, s)

}