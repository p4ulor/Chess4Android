package pt.isel.pdm.chess4android

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.model.BOARDLENGHT
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.chess4android.model.PIECETYPE
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null
    private var lichessIsWhitesOnTop: Boolean = false

    private lateinit var myView: BoardView

    private var currentlySelectedPieceIndex: Int = -1

    private val thisViewModel: PuzzleSolvingAcitivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        supportActionBar?.title=getString(R.string.attempt)

        lichessGameOfTheDayPuzzle = intent.getStringArrayExtra(PUZZLE)
        lichessGameOfTheDaySolution = intent.getStringArrayExtra(SOLUTION)
        lichessIsWhitesOnTop = intent.getBooleanExtra(ISWHITES, false)

        myView = findViewById(R.id.boardView)

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener{
                if(currentlySelectedPieceIndex==-1) {
                    if(thisViewModel.board.getPieceAtIndex(tile.index).pieceType!=PIECETYPE.EMPTY){
                        currentlySelectedPieceIndex = tile.index
                        val pieceColor = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).isWhite
                        if(thisViewModel.isWhitesPlaying==pieceColor){
                            val piecetype = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).pieceType
                            log("picked a $piecetype")
                        } else if(pieceColor){
                            log("hey! the black pieces are playing")
                            currentlySelectedPieceIndex = -1
                        } else {
                            log("hey! the white pieces are playing")
                            currentlySelectedPieceIndex = -1
                        }
                    } else {
                        log("you picked a empty spot...")
                        currentlySelectedPieceIndex = -1
                    }
                }
                else {
                    log("analysing movement validity:")
                    val pieceToMove = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)
                    val pieceThatWillBeOverwrittenIndex = tile.index
                    val theNewPosition = thisViewModel.board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex)?.position
                    log("destination has index = $pieceThatWillBeOverwrittenIndex and position = $theNewPosition ")
                    if(theNewPosition!=null && pieceToMove!=null && pieceThatWillBeOverwrittenIndex!=currentlySelectedPieceIndex) {
                        if(thisViewModel.board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex).pieceType==PIECETYPE.EMPTY || pieceToMove.isWhite!=thisViewModel.board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex).isWhite){
                            if(pieceToMove.canMoveTo(theNewPosition)){
                                thisViewModel.board.movePieceToAndLeaveEmptyBehind(pieceThatWillBeOverwrittenIndex, pieceToMove)
                                myView.invalidate(pieceThatWillBeOverwrittenIndex, thisViewModel.board.getPieceAtIndex(pieceThatWillBeOverwrittenIndex)!!) //new pos
                                myView.invalidate(currentlySelectedPieceIndex, thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)!!  ) //old pos
                                log("moved")
                                thisViewModel.isWhitesPlaying=!thisViewModel.isWhitesPlaying
                            } else log("the piece cant move to selected position")
                        } else log("the pieces are of the same color!")
                    } else log("the indexes of the pieces to move are the same or some values are null")
                    currentlySelectedPieceIndex = -1
                }
            }
        }

        if(lichessIsWhitesOnTop) {
            thisViewModel.board.reverseBoard()
            //invalidateEverything()
        }

        if(thisViewModel.isGameLoaded.value==true){
            invalidateEverything()
        } else if(loadGame()) invalidateEverything() //it's easier for us to invalidate everything when loading
    }

    override fun onStart() {
        log("Started")
        super.onStart()
    }

    override fun onResume() {
        log("Resumed")
        super.onResume()
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

    private fun loadGame() : Boolean {
        if(lichessGameOfTheDayPuzzle!=null){
            var isWhitesPlaying: Boolean
            lichessGameOfTheDayPuzzle!!.forEachIndexed { index, s ->
                isWhitesPlaying = index % 2 == 0
                if(!thisViewModel.board.interpretMove(s,isWhitesPlaying)) {
                    toast(R.string.interpretError)
                    log(getString(R.string.interpretError)+" at index $index")
                    return false
                }
                //if(index==14) return true //useful for testing index by index, movement by movement
            }
            toast(R.string.loadSuccess)
            thisViewModel.isGameLoaded.value = true
            return true
        }
        toast(R.string.WTFerror)
        return false
    }

    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, thisViewModel.board.getPieceAtIndex(it))
        }
    }

    // UTILITY METHODS
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

    private fun log(s: String) = Log.i(TAG, s)
}

private fun log(s: String) = Log.i(TAG, s) //since both of the classes on this file use log, I put it here

class PuzzleSolvingAcitivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    init {
        log("MainActivityViewModel.init()")
    }
    var isGameLoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    var board: Board = Board()
    var isWhitesPlaying: Boolean = true
}