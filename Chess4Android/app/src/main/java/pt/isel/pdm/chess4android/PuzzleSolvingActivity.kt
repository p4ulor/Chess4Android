package pt.isel.pdm.chess4android

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
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "PuzzleSolving"

class PuzzleSolvingActivity : AppCompatActivity() {

    private val thisViewModel: PuzzleSolvingActivityViewModel by viewModels()
    private lateinit var myView: BoardView
    private lateinit var soloPlaySwitch: SwitchCompat
    private lateinit var solutionSwitch: SwitchCompat
    private lateinit var currentColorPlaying: ToggleButton
    private lateinit var showHintButton: ToggleButton

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        myView = findViewById(R.id.boardView)
        soloPlaySwitch = findViewById(R.id.soloSwitch)
        solutionSwitch =  findViewById(R.id.solutionSwitch)
        currentColorPlaying = findViewById(R.id.toggleButton)
        showHintButton = findViewById(R.id.toggleShowHint)

        val gameDTO: GameDTO? = intent.getParcelableExtra(GAME_DTO_KEY)
        if(gameDTO!=null) {
            thisViewModel.gameDTO = gameDTO
            thisViewModel.puzzle = gameDTO.puzzle?.split(" ")?.toTypedArray()
            thisViewModel.solution = gameDTO.solution?.split(" ")?.toTypedArray()
            if(gameDTO.isDone) solutionSwitch.visibility = View.VISIBLE
            updateHint()

            soloPlaySwitch.setOnClickListener {
                thisViewModel.soloPlay = !thisViewModel.soloPlay
            }

            solutionSwitch.setOnClickListener {
                thisViewModel.board = Board() //board.reset() and board.literalReset() weren't working, the pieces still containing the old Position values, garbage collector's fault?
                loadGame()
                if(!thisViewModel.isDone) {
                    performSolution()
                }
                invalidateEverything()
                thisViewModel.isDone = !thisViewModel.isDone //makes so the user cant move pieces when viewing the solution as it's done in the first 'if' tileBehaviour
                thisViewModel.correctMovementsPerformed = 0
            }

            showHintButton.setOnClickListener {
                updateHint()
            }
        } else {
            soloPlaySwitch.visibility=View.INVISIBLE
            showHintButton.visibility=View.INVISIBLE
            thisViewModel.isWhitesPlaying.value=true
        }

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener {
                tileBehaviour(tile)
            }
        }

        if(gameDTO!=null){
            if(thisViewModel.isGameLoaded){
                invalidateEverything()
            } else if(loadGame()) {
                invalidateEverything() //it's easier for us to invalidate everything after all the pieces are set, instead of invalidating for every move. Every puzzle has 30+ moves. So that's 30*2 tile invalidates. So, its worth the simplicity (or cost) of invalidating everything (64 positions)
                play(R.raw.pictures_snare, this)
            }
        }
    }

    private fun tileBehaviour(tile: Tile) {
        if(thisViewModel.isDone) return
        if(currentlySelectedPieceIndex==-1) {
            if(thisViewModel.board.getPieceAtIndex(tile.index).pieceType!=PIECETYPE.EMPTY){
                currentlySelectedPieceIndex = tile.index
                val pieceColor = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).isWhite
                when {
                    thisViewModel.isWhitesPlaying.value==pieceColor -> {
                        val pieceType = thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex).pieceType
                        log("picked a $pieceType")
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
            val pieceThatWillBeEaten = thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)
            val theNewPosition = pieceThatWillBeEaten.position

            log("destination has index = $pieceThatWillBeEatenIndex and position = $theNewPosition ")
            if(pieceThatWillBeEatenIndex!=currentlySelectedPieceIndex) {
                if(thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).pieceType==PIECETYPE.EMPTY || pieceToMove.isWhite!=thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex).isWhite){
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
        if(thisViewModel.gameDTO==null){
            moveAndInvalidate(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
            thisViewModel.isWhitesPlaying.value = !(thisViewModel.isWhitesPlaying.value)!!
        } else if(movement == thisViewModel.solution?.get(thisViewModel.correctMovementsPerformed)) {
            moveAndInvalidate(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
            log("moved")
            thisViewModel.isWhitesPlaying.value = !(thisViewModel.isWhitesPlaying.value)!!
            thisViewModel.correctMovementsPerformed++
            toast(R.string.correctMove, this)
            play(R.raw.rareee, this)
            if(thisViewModel.correctMovementsPerformed==thisViewModel.solution?.size) finishedPuzzle()
            else if(thisViewModel.soloPlay) {
                val x = thisViewModel.solution?.get(thisViewModel.correctMovementsPerformed)
                if (x != null) {
                    val indexOrigin = Board.positionToIndex(Position(x.substring(0, 2)))
                    val indexDestination = Board.positionToIndex(Position(x.substring(2, 4)))
                    moveAndInvalidate(indexOrigin, indexDestination)
                    thisViewModel.isWhitesPlaying.value = !(thisViewModel.isWhitesPlaying.value)!!
                    thisViewModel.correctMovementsPerformed++
                    if(thisViewModel.correctMovementsPerformed==thisViewModel.solution?.size) finishedPuzzle()
                }
            }
            updateHint()
        } else play(R.raw.my_wrong_button_sound, this)
    }

    private fun finishedPuzzle(){
        thisViewModel.gameDTO?.isDone = true
        if(!thisViewModel.isDone) {
            thisViewModel.isDone = true
            thisViewModel.setGameAsDoneInDB()
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
        thisViewModel.isWhitesPlaying.observe(this){
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
        if(thisViewModel.gameDTO?.isDone==false) toast(R.string.progressLost, this)
        super.onBackPressed()
    }

    private fun loadGame() : Boolean {
        if(thisViewModel.puzzle!=null){
            var isWhitesPlaying = true
            thisViewModel.puzzle?.forEachIndexed { index, s ->
                isWhitesPlaying = index % 2 == 0
                if(!thisViewModel.board.interpretMove(s,isWhitesPlaying)) {
                    toast(R.string.interpretError, this)
                    log(getString(R.string.interpretError)+" at index $index")
                    return false
                }
                //if(index==14) return true //useful for testing index by index, movement by movement
            }
            thisViewModel.isWhitesPlaying.value = !isWhitesPlaying
            // toast(R.string.loadSuccess, this)
            thisViewModel.isGameLoaded = true
            return true
        }
        toast(R.string.WTFerror, this)
        return false
    }

    private fun performSolution() {
        thisViewModel.solution?.forEach { s ->
            val origin = Position.convertToPosition(s.subSequence(0,2).toString())
            val destination = Position.convertToPosition(s.subSequence(2,4).toString())
            if (origin != null && destination != null) {
                thisViewModel.board.movePieceToAndLeaveEmptyBehind(origin, destination)
            }
        }
    }

    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, thisViewModel.board.getPieceAtIndex(it))
        }
    }

    private fun updateHint() { //https://stackoverflow.com/questions/27999623/android-togglebutton-setontext-and-invalidate-doesnt-refresh-text
        if(showHintButton.isChecked && !thisViewModel.isDone) {
            showHintButton.textOn = thisViewModel.solution?.get(thisViewModel.correctMovementsPerformed)?.subSequence(0,2)
            showHintButton.isChecked = showHintButton.isChecked
        }
    }

    private fun moveAndInvalidate(indexOrigin: Int, indexDestination: Int) {
        thisViewModel.board.movePieceToAndLeaveEmptyBehind(indexOrigin, indexDestination)
        myView.invalidate(indexOrigin, thisViewModel.board.getPieceAtIndex(indexOrigin)) //new pos
        myView.invalidate(indexDestination, thisViewModel.board.getPieceAtIndex(indexDestination)) //old pos
    }
}

class PuzzleSolvingActivityViewModel(application: Application) : AndroidViewModel(application) {
    init {
        log("MainActivityViewModel.init()")
    }

    private val historyDB : GameTableDAO by lazy {
        getApplication<Chess4AndroidApp>().historyDB.getDAO()
    }

    var soloPlay: Boolean = false
    var isDone: Boolean = false
    var isGameLoaded: Boolean = false
    var isWhitesPlaying: MutableLiveData<Boolean> = MutableLiveData(false)
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