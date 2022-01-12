package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.databinding.ActivityPuzzleSolvingBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "PuzzleSolving"

class PuzzleSolvingActivity : AppCompatActivity() {

    private val layout by lazy { ActivityPuzzleSolvingBinding.inflate(layoutInflater) }

    private val viewModel: PuzzleSolvingActivityViewModel by viewModels()
    private lateinit var myView: BoardView
    private lateinit var autoOpponentSwitch: SwitchCompat
    private lateinit var solutionSwitch: SwitchCompat
    private lateinit var currentColorPlaying: ToggleButton
    private lateinit var showHintButton: ToggleButton

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created"); super.onCreate(savedInstanceState); setContentView(layout.root)

        myView = layout.boardView
        autoOpponentSwitch = layout.autoOpponent
        solutionSwitch =  layout.solutionSwitch
        currentColorPlaying = layout.toggleColorButton
        showHintButton = layout.toggleShowHintButton!! //!! cuz IDE was complaining which is weird I think

        val gameDTO: GameDTO? = intent.getParcelableExtra(GAME_DTO_KEY)
        if(gameDTO!=null) {
            viewModel.gameDTO = gameDTO
            viewModel.puzzle = gameDTO.puzzle?.split(" ")?.toTypedArray()
            viewModel.solution = gameDTO.solution?.split(" ")?.toTypedArray()
            if(gameDTO.isDone) solutionSwitch.visibility = View.VISIBLE
            updateHint()

            autoOpponentSwitch.setOnClickListener {
                viewModel.soloPlay = !viewModel.soloPlay
            }

            solutionSwitch.setOnClickListener {
                viewModel.board = Board() //board.reset() and board.literalReset() weren't working, the pieces still containing the old Position values, garbage collector's fault?
                loadGame()
                if(!viewModel.isDone) {
                    performSolution()
                }
                invalidateEverything()
                viewModel.isDone = !viewModel.isDone //makes so the user cant move pieces when viewing the solution as it's done in the first 'if' tileBehaviour
                viewModel.correctMovementsPerformed = 0
            }

            showHintButton.setOnClickListener {
                updateHint()
            }
        } else {
            autoOpponentSwitch.visibility=View.INVISIBLE
            showHintButton.visibility=View.INVISIBLE
        }

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener {
                tileBehaviour(tile)
            }
        }

        if(gameDTO!=null){
            if(viewModel.isGameLoaded){
                invalidateEverything()
            } else if(loadGame()) {
                invalidateEverything() //it's easier for us to invalidate everything after all the pieces are set, instead of invalidating for every move. Every puzzle has 30+ moves. So that's 30*2 (2 for old and new position) tile invalidates. So, its worth the simplicity (or cost) of invalidating everything (64 positions)
                play(R.raw.pictures_snare, this)
            }
        }
    }

    private fun tileBehaviour(tile: Tile) {
        if(viewModel.isDone) return
        if(currentlySelectedPieceIndex==-1) {
            if(viewModel.board.getPieceAtIndex(tile.index).pieceType!=PIECETYPE.EMPTY){
                val pieceColor = viewModel.board.getPieceAtIndex(tile.index).isWhite
                when {
                    viewModel.isWhitesPlaying.value==pieceColor -> {
                        currentlySelectedPieceIndex = tile.index
                        val pieceType = viewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).pieceType
                        log("picked a $pieceType")
                    }
                    pieceColor -> log("hey! the black pieces are playing")
                    else -> log("hey! the white pieces are playing")
                }
            } else log("you picked a empty spot...")
        }
        else {
            log("analysing movement validity:")
            val pieceToMove = viewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)
            val pieceThatWillBeEatenIndex = tile.index
            val pieceThatWillBeEaten = viewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)
            val theNewPosition = pieceThatWillBeEaten.position

            log("destination has index = $pieceThatWillBeEatenIndex and position = $theNewPosition ")
            if(pieceThatWillBeEatenIndex!=currentlySelectedPieceIndex) {
                if(viewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).pieceType==PIECETYPE.EMPTY || pieceToMove.isWhite!=viewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).isWhite){
                    if(pieceToMove.canMoveTo(theNewPosition)){
                        if(pieceToMove.pieceType==PIECETYPE.PAWN) {
                            val thePawn = pieceToMove as ChessPieces.Pawn
                            if(thePawn.movesDiagonally(theNewPosition) && pieceThatWillBeEaten.pieceType==PIECETYPE.EMPTY){
                                log("the pawn can only move diagonally when it will eat a piece")
                            } else moveIt(pieceThatWillBeEatenIndex)
                        } else {
                            moveIt(pieceThatWillBeEatenIndex)
                        }
                    } else log("the piece cant move to selected position")
                } else log("the pieces are of the same color!")
            } else log("the indexes of the pieces to move are the same")
            currentlySelectedPieceIndex = -1
        }
    }

    private fun moveIt(pieceThatWillBeEatenIndex: Int) {
        val movement = Board.movementToPositionString(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
        if(viewModel.gameDTO==null){
            moveAndInvalidate(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
            switchColorTurn()
        } else if(movement == viewModel.solution?.get(viewModel.correctMovementsPerformed)) {
            moveAndInvalidate(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
            log("moved")
            viewModel.correctMovementsPerformed++
            toast(R.string.correctMove, this)
            play(R.raw.rareee, this)
            if(viewModel.correctMovementsPerformed==viewModel.solution?.size) finishedPuzzle()
            else if(viewModel.soloPlay) {
                val x = viewModel.solution?.get(viewModel.correctMovementsPerformed)
                if (x != null) {
                    val indexOrigin = Board.positionToIndex(Position(x.substring(0, 2)))
                    val indexDestination = Board.positionToIndex(Position(x.substring(2, 4)))
                    moveAndInvalidate(indexOrigin, indexDestination)
                    viewModel.correctMovementsPerformed++
                    if(viewModel.correctMovementsPerformed==viewModel.solution?.size) finishedPuzzle()
                }
            }
            updateHint()
        } else play(R.raw.my_wrong_button_sound, this)
    }

    private fun finishedPuzzle(){
        viewModel.gameDTO?.isDone = true
        if(!viewModel.isDone) {
            viewModel.isDone = true
            viewModel.setGameAsDoneInDB()
        }
        snackBar(R.string.won)
        play(R.raw.kill_bill_siren, this)
        play(R.raw.gawdamn, this)
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
        viewModel.isWhitesPlaying.observe(this){
            currentColorPlaying.isChecked = it
        }
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
        //board.reset() // because we destroy the activity with the onBackPressed, no need to reset
        if(viewModel.gameDTO?.isDone==false) toast(R.string.progressLost, this)
        super.onBackPressed()
    }

    private fun loadGame() : Boolean {
        if(viewModel.puzzle!=null){
            viewModel.isWhitesPlaying.value = true
            viewModel.puzzle?.forEachIndexed { index, move ->
                viewModel.isWhitesPlaying.value = index % 2 == 0
                if(!viewModel.board.interpretMove(move, viewModel.isWhitesPlaying.value!!)) {
                    toast(R.string.interpretError, this)
                    log(getString(R.string.interpretError)+" at index $index")
                    return false
                }
            }
            switchColorTurn()
            // toast(R.string.loadSuccess, this)
            viewModel.isGameLoaded = true
            return true
        }
        toast(R.string.WTFerror, this)
        return false
    }

    private fun performSolution() {
        viewModel.solution?.forEach { s ->
            val origin = Position.convertToPosition(s.subSequence(0,2).toString())
            val destination = Position.convertToPosition(s.subSequence(2,4).toString())
            if (origin != null && destination != null) {
                viewModel.board.movePieceToAndLeaveEmptyBehind(origin, destination)
            }
        }
    }

    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, viewModel.board.getPieceAtIndex(it))
        }
    }

    private fun updateHint() { //https://stackoverflow.com/questions/27999623/android-togglebutton-setontext-and-invalidate-doesnt-refresh-text
        if(showHintButton.isChecked && !viewModel.isDone) {
            showHintButton.textOn = viewModel.solution?.get(viewModel.correctMovementsPerformed)?.subSequence(0,2)
            showHintButton.isChecked = showHintButton.isChecked
        }
    }

    private fun moveAndInvalidate(indexOrigin: Int, indexDestination: Int) {
        viewModel.board.movePieceToAndLeaveEmptyBehind(indexOrigin, indexDestination)
        myView.invalidate(indexOrigin, viewModel.board.getPieceAtIndex(indexOrigin)) //new pos
        myView.invalidate(indexDestination, viewModel.board.getPieceAtIndex(indexDestination)) //old pos
        switchColorTurn()
    }

    private fun switchColorTurn() {
        viewModel.isWhitesPlaying.value = !viewModel.isWhitesPlaying.value!!
    }
}

class PuzzleSolvingActivityViewModel(application: Application) : AndroidViewModel(application) {
    init { log("MainActivityViewModel.init()") }

    private val historyDB : GameTableDAO by lazy {
        getApplication<Chess4AndroidApp>().historyDB.getDAO()
    }

    var soloPlay: Boolean = false
    var isDone: Boolean = false
    var isGameLoaded: Boolean = false
    var isWhitesPlaying: MutableLiveData<Boolean> = MutableLiveData(true)
    var board: Board = Board()
    var correctMovementsPerformed: Int = 0
    var gameDTO: GameDTO? = null
    var puzzle: Array<String>? = null
    var solution: Array<String>? = null

    fun setGameAsDoneInDB(){
        doAsyncWithResult {
            historyDB.setIsDone(gameDTO?.id.toString(), gameDTO?.isDone ?: false)
        }
    }
}