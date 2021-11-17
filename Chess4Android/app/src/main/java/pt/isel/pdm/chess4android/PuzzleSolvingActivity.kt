package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pt.isel.pdm.chess4android.model.BOARDLENGHT
import pt.isel.pdm.chess4android.model.BOARD_SIDE_SIZE
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null

    private lateinit var myView: BoardView
    private lateinit var board: Board

    private var isWhitesPlaying: Boolean = true

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        lichessGameOfTheDayPuzzle = intent.getStringArrayExtra(PUZZLE)
        lichessGameOfTheDaySolution = intent.getStringArrayExtra(SOLUTION)

        board = Board()

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
            }
        }

        if(loadGame()) invalidateEverything() //it's easier for us to invalidate everything when loading
    }

    override fun onStart() {
        log("Started")
        super.onStart()
    }

    override fun onResume() {
        log("Resumed")
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        log("State saved")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        log("State restored")
    }

    private fun loadGame() : Boolean {
        if(lichessGameOfTheDayPuzzle!=null){
            lichessGameOfTheDayPuzzle!!.forEachIndexed { index, s ->
                isWhitesPlaying = index % 2 == 0
                if(!board.interpretMove(s,isWhitesPlaying)) {
                    toast(R.string.interpretError)
                    log(getString(R.string.interpretError)+" at index $index")
                    return false
                }
                //if(index==14) return true //useful for testing index by index, movement by movement
            }
            toast(R.string.loadSuccess)
            return true
        }
        toast(R.string.WTFerror)
        return false
    }

    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, board.getPieceAtIndex(it))
        }
    }

    override fun onPause() { //runs after onBackBackPressed
        log("paused")
        super.onPause()
    }

    override fun onBackPressed() {
        //board.reset()
        toast(R.string.progressLost)
        log("progress lost")
        super.onBackPressed()
    }

    // UTILITY METHODS
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

    private fun log(s: String) = Log.i(TAG, s)
}