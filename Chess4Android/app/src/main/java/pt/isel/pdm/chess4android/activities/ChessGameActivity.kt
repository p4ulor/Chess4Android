package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.databinding.ActivityChessGameBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.tileMatrix

private const val TAG = "ChessGameActivity"
private const val IS_WHITES_PLAYER = "isWhitesPlayer"
private const val IS_WHITES_PLAYING = "isWhitesPlaying"
private const val CHALLENGE_INFO = "challengeInfo"

class ChessGameActivity : AppCompatActivity() { //CONTAINS REPETITIVE CODE, FIXME: SOON

    private val layout by lazy { ActivityChessGameBinding.inflate(layoutInflater) }

    private val intentIsWhitesPlayer by lazy { intent.getBooleanExtra(IS_WHITES_PLAYER, true) }
    private val intentWhitesPlaying by lazy { intent.getBooleanExtra(IS_WHITES_PLAYING, true) }
    private val intentChallengeInfo: ChallengeInfo by lazy { intent.getParcelableExtra(CHALLENGE_INFO)!! }

    private val viewModel: ChessGameActivityViewModel by viewModels {
        @Suppress("UNCHECKED_CAST")
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChessGameActivityViewModel(application, intentIsWhitesPlayer, intentWhitesPlaying, intentChallengeInfo) as T
            }
        }
    }

    private lateinit var myView: BoardView
    private lateinit var currentColorPlaying: ToggleButton
    private var currentlySelectedPieceIndex: Int = -1

    companion object { //this buildIntent() is used on 2 cases/perpectives: when the user that created a challenge has it's challenge accepted, when the user accepts a challenge that was created
        fun buildIntent(context: Context, isWhitesPlayer: Boolean, isWhitesPlaying: Boolean, challengeInfo: ChallengeInfo) =
            Intent(context, ChessGameActivity::class.java)
                .putExtra(IS_WHITES_PLAYER, isWhitesPlayer)
                .putExtra(IS_WHITES_PLAYING, isWhitesPlaying)
                .putExtra(CHALLENGE_INFO, challengeInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        log(TAG, "Created"); super.onCreate(savedInstanceState); setContentView(layout.root)

        myView = layout.boardView
        currentColorPlaying = layout.toggleColorButton

        tileMatrix.forEach { tile ->
            tile?.setOnClickListener {
                tileBehaviour(tile)
            }
        }

        /*viewModel._game.observe(this) {
            //updateDataAndUI(it)
            log(TAG,"I observed _game")
        }*/
        currentColorPlaying.isChecked = intentWhitesPlaying
        viewModel.isWhitesPlaying.observe(this){
            currentColorPlaying.isChecked = it
            log(TAG, "color playing is now: $it. My color: ${viewModel.myColor}")
        }
        /*layout.forfeitButton.setOnClickListener {
            // TODO
        }*/

        invalidateEverything() //cuz of screen rotation
    }

    private fun tileBehaviour(tile: Tile) {
        if(viewModel.winnerColor!=null) return
        if(viewModel.myColor!=viewModel.isWhitesPlaying.value) return
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

    private fun moveAndInvalidate(indexOrigin: Int, indexDestination: Int) {
        viewModel.board.movePieceToAndLeaveEmptyBehind(indexOrigin, indexDestination)
        myView.invalidate(indexOrigin, viewModel.board.getPieceAtIndex(indexOrigin)) //new pos
        myView.invalidate(indexDestination, viewModel.board.getPieceAtIndex(indexDestination)) //old pos
        updateGameState()
        //viewModel.game.value?.getOrNull()?.isWhitePlaying = viewModel.isWhitesPlaying.value == true
        viewModel.publishGameStateChangesToFireBase()
    }

    // Used to update de board and the boardView according to the current state of the game
    private fun updateDataAndUI(result: Result<GameState>) {
        if(viewModel.game.value?.getOrNull()==null) return
        //FIXME: REPETITIVE CODE
        result.onSuccess {
            val piece1 = it.changedPos1
            val piece2 = it.changedPos2
            if(piece1!=null && piece2!=null){
                viewModel.board.setPieceAtIndex(piece1)
                viewModel.board.setPieceAtIndex(piece2)
                myView.invalidate(piece1.index.toInt(), viewModel.board.getPieceAtIndex(piece1.index.toInt())) //new pos
                myView.invalidate(piece2.index.toInt(), viewModel.board.getPieceAtIndex(piece2.index.toInt())) //old pos
            }
        }
        result.onFailure { toast(R.string.errorChallenge, this) }
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
        //if(viewModel.myColor) viewModel.publishGameStateChangesToFireBase()
        /*if(viewModel.myColor==false) {
            viewModel.board.reverseBoard()
            invalidateEverything()
        }*/
        super.onStart()
    }

    override fun onResume() {
        log("Resumed")
        //currentColorPlaying.isChecked = viewModel.isWhitesPlaying.value == true
        super.onResume()
    }

    override fun onPause() { //runs after onBackBackPressed
        log("paused")
        super.onPause()
    }

    override fun onBackPressed() {
        //close game //todo
        super.onBackPressed()
    }

    private fun invalidateEverything() {
        repeat(BOARDLENGHT) {
            myView.invalidate(it, viewModel.board.getPieceAtIndex(it))
        }
    }

    private fun updateGameState() {
        viewModel._game.value?.onSuccess {
            it.isWhitePlaying = !viewModel.isWhitesPlaying.value!!
        }
        log(TAG,"updatedGameState")
    }
}

class ChessGameActivityViewModel(application: Application, intentIsWhitesPlayer: Boolean, intentWhitesPlaying: Boolean, intentChallengeInfo: ChallengeInfo?) : AndroidViewModel(application) {

    private val fb: FireBaseChallengesRepo by lazy { getApplication<Chess4AndroidApp>().fireBase }

    var myColor: Boolean = intentIsWhitesPlayer
    var isWhitesPlaying: MutableLiveData<Boolean> = MutableLiveData(intentWhitesPlaying)
    var winnerColor: Boolean? = null
    var board: Board = Board()

    val _game: MutableLiveData<Result<GameState>> by lazy {
        MutableLiveData(Result.success(GameState(intentChallengeInfo?.id!! , intentWhitesPlaying, FireBasePiece(-1, PIECETYPE.EMPTY, false), FireBasePiece(-1, PIECETYPE.EMPTY, false), null)))
    }
    val game: LiveData<Result<GameState>> = _game

    // LISTENS, EVEN TO THIS PHONE'S PUBLICATION TO THE FIREBASE
    private val gameSubscription = fb.listenToGameStateChanges(_game.value?.getOrNull()?.id!!,
            onSubscriptionError = { _game.value = Result.failure(it) },
            onGameStateChange = {
                log(TAG, "Latest GameState says that the color playing is ${it.isWhitePlaying}")
                if(isWhitesPlaying.value==it.isWhitePlaying){ //If I passed the turn to the other color, I'm not the one that needs to update my GameState, since I already have it applied
                    log(TAG, "I just published a new GameState and/or I'm synced with the server. Update color's turn")
                } else {
                    log(TAG, "Woah, I gotta the GameState, update color's turn")
                    _game.value = Result.success(it)
                }
                isWhitesPlaying.value = it.isWhitePlaying //this is done here, so that the UI will only display that the other color will play only if this device is connected and got a response from the server
            }
    )

    fun publishGameStateChangesToFireBase(){
        if(_game.value?.getOrNull()==null) return
        fb.updateGameState(
            _game.value?.getOrNull()!!,
            onComplete = { result ->
                result.onFailure { _game.value = Result.failure(it)
                }
                log(TAG,"I published game state changes")
            })
    }

    override fun onCleared() { //View model is destroyed
        super.onCleared()
        fb.deleteGame(
            challengeId = _game.value?.getOrNull()?.id!!,
            onComplete = { }
        )
        gameSubscription.remove()
    }
}