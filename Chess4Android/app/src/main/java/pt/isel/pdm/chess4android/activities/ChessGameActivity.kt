package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.databinding.ActivityChessGameBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val GAME_EXTRA = "GameActivity.GameInfoExtra"
private const val LOCAL_PLAYER_EXTRA = "GameActivity.LocalPlayerExtra"

class ChessGameActivity : AppCompatActivity() { //CONTAINS REPETITIVE CODE, FIX SOON

    private val layout by lazy { ActivityChessGameBinding.inflate(layoutInflater) }

    companion object { //this buildIntent() is used on 2 cases/perpectives: when the user that created a challenge has it's challenge accepted, when the user accepts a challenge that was created
        fun buildIntent(context: Context, local: User, turn: User, challengeInfo: ChallengeInfo) =
            Intent(context, ChessGameActivity::class.java)
                //.putExtra(GAME_EXTRA, Board(turn = turn).toGameState(challengeInfo.id))
                .putExtra(LOCAL_PLAYER_EXTRA, local.name)
    }

    private val viewModel: PuzzleSolvingActivityViewModel by viewModels()
    private lateinit var myView: BoardView
    private lateinit var currentColorPlaying: ToggleButton

    private var currentlySelectedPieceIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("Created"); super.onCreate(savedInstanceState); setContentView(layout.root)

        myView = layout.boardView
        currentColorPlaying = layout.toggleColorButton

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener {
                tileBehaviour(tile)
            }
        }
    }

    private fun tileBehaviour(tile: Tile) {
        if(viewModel.isDone) return
        if(currentlySelectedPieceIndex==-1) {
            if(viewModel.board.getPieceAtIndex(tile.index).pieceType!= PIECETYPE.EMPTY){
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
        moveAndInvalidate(currentlySelectedPieceIndex, pieceThatWillBeEatenIndex)
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


    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, viewModel.board.getPieceAtIndex(it))
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

class ChessGameActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val historyDB : GameTableDAO by lazy {
        getApplication<Chess4AndroidApp>().historyDB.getDAO()
    }

    var winnerColor: Boolean? = null
    var isWhitesPlaying: MutableLiveData<Boolean> = MutableLiveData(true)
    var board: Board = Board()
}