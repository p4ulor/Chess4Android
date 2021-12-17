package pt.isel.pdm.chess4android

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "PuzzleSolving"

class PuzzleSolvingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var puzzle: Array<String>? = null
    private var solution: Array<String>? = null

    private val thisViewModel: PuzzleSolvingActivityViewModel by viewModels()
    private lateinit var myView: BoardView
    private lateinit var soloPlaySwitch: SwitchCompat
    private lateinit var solutionSwitch: SwitchCompat
    private lateinit var currentColorPlaying: ToggleButton

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)

        myView = findViewById(R.id.boardView)
        soloPlaySwitch = findViewById(R.id.soloSwitch)
        solutionSwitch =  findViewById(R.id.solutionSwitch)
        currentColorPlaying = findViewById(R.id.toggleButton)

        val gameDTO: GameDTO? = intent.getParcelableExtra(GAME_DTO_KEY)
        if(gameDTO!=null) {
            thisViewModel.gameDTO = gameDTO
            puzzle = gameDTO.puzzle?.split(" ")?.toTypedArray()
            solution = gameDTO.solution?.split(" ")?.toTypedArray()
            if(gameDTO.isDone) solutionSwitch.visibility = View.VISIBLE
        } else toast(R.string.WTFerror, this)

        soloPlaySwitch.setOnClickListener {
            thisViewModel.soloPlay = !thisViewModel.soloPlay
        }


        solutionSwitch.setOnClickListener {
            thisViewModel.board = Board() //board.reset() and board.literalReset() weren't working, the pieces still containing the old Position values
            loadGame()
            if(!thisViewModel.isDone) {
                performSolution()
            }
            invalidateEverything()
            thisViewModel.isDone = !thisViewModel.isDone //makes so the user cant move pieces when viewing the solution as it's done in the first 'if' tileBehaviour
            thisViewModel.correctMovementsPerformed = 0
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
                invalidateEverything() //it's easier for us to invalidate everything when loading
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
            val thePieceToBeEaten = thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)
            val theNewPosition = thePieceToBeEaten.position
            val movement = pieceToMove.position.letterAndNumber()+theNewPosition.letterAndNumber()
            if(movement == solution?.get(thisViewModel.correctMovementsPerformed)) {
                thisViewModel.correctMovementsPerformed++
                toast(R.string.correctMove, this)
                play(R.raw.rareee, this)
            } else {
                play(R.raw.my_wrong_button_sound, this)
            }
            log("destination has index = $pieceThatWillBeEatenIndex and position = $theNewPosition ")
            if(pieceThatWillBeEatenIndex!=currentlySelectedPieceIndex) {
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
        myView.invalidate(pieceThatWillBeEatenIndex, thisViewModel.board.getPieceAtIndex(pieceThatWillBeEatenIndex)) //new pos
        myView.invalidate(currentlySelectedPieceIndex, thisViewModel.board.getPieceAtIndex(currentlySelectedPieceIndex)  ) //old pos
        invalidateEverything()
        log("moved")
        thisViewModel.isWhitesPlaying.value = !(thisViewModel.isWhitesPlaying.value)!!
        if(thisViewModel.correctMovementsPerformed==solution?.size) {
            thisViewModel.gameDTO?.isDone = true
            thisViewModel.isDone = true
            thisViewModel.setGameAsDoneInDB()
            snackBar(R.string.won)
            play(R.raw.kill_bill_siren, this)
            play(R.raw.gawdamn, this)
        } else if(thisViewModel.soloPlay){
            val x = solution?.get(thisViewModel.correctMovementsPerformed)
            if (x != null) {
                val initialIndex= Board.positionToIndex(Position(x.substring(0,2)))
                val destinationIndex = Board.positionToIndex(Position(x.substring(2,4)))
                thisViewModel.board.movePieceToAndLeaveEmptyBehind(initialIndex, destinationIndex)
                thisViewModel.isWhitesPlaying.value = !(thisViewModel.isWhitesPlaying.value)!!
                thisViewModel.correctMovementsPerformed++

                myView.invalidate(initialIndex, thisViewModel.board.getPieceAtIndex(initialIndex)) //new pos
                myView.invalidate(destinationIndex, thisViewModel.board.getPieceAtIndex(destinationIndex)  ) //old pos
            }
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
        thisViewModel.isWhitesPlaying.observe(this){
            if(it) currentColorPlaying.text = currentColorPlaying.textOn
            else currentColorPlaying.text = currentColorPlaying.textOff
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
        if(puzzle!=null){
            var isWhitesPlaying = true
            puzzle?.forEachIndexed { index, s ->
                isWhitesPlaying = index % 2 == 0
                if(!thisViewModel.board.interpretMove(s,isWhitesPlaying)) {
                    toast(R.string.interpretError, this)
                    log(getString(R.string.interpretError)+" at index $index")
                    return false
                }
                //if(index==14) return true //useful for testing index by index, movement by movement
            }
            thisViewModel.isWhitesPlaying.value = !isWhitesPlaying
            toast(R.string.loadSuccess, this)
            thisViewModel.isGameLoaded = true
            return true
        }
        toast(R.string.WTFerror, this)
        return false
    }

    private fun performSolution() {
        solution?.forEach { s ->
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
}

class PuzzleSolvingActivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
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

    fun setGameAsDoneInDB(){
        doAsyncWithResult {
            historyDB.setIsDone(gameDTO?.id.toString(), gameDTO?.isDone ?: false)
        }
    }
}