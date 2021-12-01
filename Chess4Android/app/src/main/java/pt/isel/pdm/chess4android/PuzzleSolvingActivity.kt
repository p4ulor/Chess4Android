package pt.isel.pdm.chess4android

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null

    private lateinit var myView: BoardView

    private var currentlySelectedPieceIndex: Int = -1

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val thisViewModel: PuzzleSolvingAcitivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        supportActionBar?.title=getString(R.string.attempt)

        val gameDTO: GameDTO? = intent.getParcelableExtra(GAME_DTO_KEY)
        if(gameDTO!=null) {
            thisViewModel.gameDTO = gameDTO
            lichessGameOfTheDayPuzzle = gameDTO.puzzle?.split(" ")?.toTypedArray()
            lichessGameOfTheDaySolution = gameDTO.solution?.split(" ")?.toTypedArray()
        } else toast(R.string.WTFerror)


        myView = findViewById(R.id.boardView)

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener {
                tileBehaviour(tile)
            }
        }
        if(gameDTO!=null){
            if(thisViewModel.isGameLoaded){
                invalidateEverything()
            } else if(loadGame()) invalidateEverything() //it's easier for us to invalidate everything when loading
        }
    }

    private fun tileBehaviour(tile: Tile) {
        if(currentlySelectedPieceIndex==-1) {
            if(thisViewModel.board.getPieceAtIndex(tile.index).pieceType!=PIECETYPE.EMPTY){
                currentlySelectedPieceIndex = tile.index
                val pieceColor = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).isWhite
                when {
                    thisViewModel.isWhitesPlaying==pieceColor -> {
                        val piecetype = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).pieceType
                        log("picked a $piecetype")
                    }
                    pieceColor -> {
                        log("hey! the black pieces are playing")
                        currentlySelectedPieceIndex = -1
                    }
                    else -> {
                        log("hey! the white pieces are playing")
                        currentlySelectedPieceIndex = -1
                    }
                }
            } else {
                log("you picked a empty spot...")
                currentlySelectedPieceIndex = -1
            }
        }
        else {
            log("analysing movement validity:")
            val pieceToMove = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)
            val pieceThatWillBeEatenIndex = tile.index
            val thePieceToBeEaten = thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)
            val theNewPosition = thePieceToBeEaten?.position
            val movement = pieceToMove.position.letterAndNumber()+theNewPosition.letterAndNumber()
            if(movement == lichessGameOfTheDaySolution?.get(thisViewModel.correctMovementsPerformed)) {
                thisViewModel.correctMovementsPerformed++
                toast(R.string.correctMove)
            }
            log("destination has index = $pieceThatWillBeEatenIndex and position = $theNewPosition ")
            if(theNewPosition!=null && pieceToMove!=null && pieceThatWillBeEatenIndex!=currentlySelectedPieceIndex) {
                if(thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).pieceType==PIECETYPE.EMPTY || pieceToMove.isWhite!=thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).isWhite){
                    if(pieceToMove.canMoveTo(theNewPosition)){
                        if(pieceToMove.pieceType==PIECETYPE.PAWN) {
                            val thePawn = pieceToMove as ChessPieces.Pawn
                            if(thePawn.movesDiagonally(theNewPosition) && thePieceToBeEaten.pieceType==PIECETYPE.EMPTY){
                                log("the pawn can only move diagonally when it will eat a piece")
                            } else moveIt(pieceThatWillBeEatenIndex, pieceToMove)
                        } else {
                            moveIt(pieceThatWillBeEatenIndex, pieceToMove)
                        }
                    } else log("the piece cant move to selected position")
                } else log("the pieces are of the same color!")
            } else log("the indexes of the pieces to move are the same or some values are null")
            currentlySelectedPieceIndex = -1
        }
    }

    private fun moveIt(pieceThatWillBeEatenIndex: Int, pieceToMove: Piece) {
        thisViewModel.board.movePieceToAndLeaveEmptyBehind(pieceThatWillBeEatenIndex, pieceToMove)
        myView.invalidate(pieceThatWillBeEatenIndex, thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)!!) //new pos
        myView.invalidate(currentlySelectedPieceIndex, thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)!!  ) //old pos
        log("moved")
        thisViewModel.isWhitesPlaying=!thisViewModel.isWhitesPlaying
        if(thisViewModel.correctMovementsPerformed==lichessGameOfTheDaySolution?.size) {
            thisViewModel.gameDTO?.isDone = true
            snackBar(R.string.won)
        }
    }

    private fun snackBar(stringID: Int){ //https://material.io/components/snackbars/android#using-snackbars //or function: () -> (Unit) https://stackoverflow.com/a/44132689
        Snackbar.make(findViewById(R.id.boardView),getString(stringID), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok) {
                super.onBackPressed()
            }
            .show()
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
            thisViewModel.isGameLoaded = true
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

}

private fun log(s: String) = Log.i(TAG, s) //since both of the classes on this file use log, I put it here

class PuzzleSolvingAcitivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    init {
        log("MainActivityViewModel.init()")
    }
    var isGameLoaded: Boolean = false
    var board: Board = Board()
    var correctMovementsPerformed: Int = 0
    var isWhitesPlaying: Boolean = true
    var gameDTO: GameDTO? = null
}