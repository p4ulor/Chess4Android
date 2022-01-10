package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.Chess4AndroidApp
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityChallengesListBinding
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.GameState
import pt.isel.pdm.chess4android.model.User
import pt.isel.pdm.chess4android.views.ChallengesListAdapter

private const val TAG = "ChallengesList"

class ChallengesListActivity : AppCompatActivity() {

    private val layout by lazy { ActivityChallengesListBinding.inflate(layoutInflater) }
    private val viewModel: ChallengesListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) { //Sets up the screen behaviour
        super.onCreate(savedInstanceState); setContentView(layout.root)

        layout.challengesList.setHasFixedSize(true) //hmmmmm
        layout.challengesList.layoutManager = LinearLayoutManager(this)

        viewModel.challenges.observe(this) { result ->
            result.onSuccess {
                layout.challengesList.adapter = ChallengesListAdapter(it, ::challengeSelected)
                layout.refreshLayout.isRefreshing = false
            }
            result.onFailure {
                Toast.makeText(this, R.string.errorChallenge, Toast.LENGTH_LONG).show()
            }
        }

        layout.refreshLayout.setOnRefreshListener { updateChallengesList() }

        layout.createChallengeButton.setOnClickListener {
            startActivity(Intent(this, CreateChallengeActivity::class.java))
        }

        viewModel.enrolmentResult.observe(this) {
            it?.onSuccess { createdGameInfo ->
                val intent = ChessGameActivity.buildIntent(
                    origin = this,
                    turn = User.firstToMove,
                    local = User.firstToMove.other,
                    challengeInfo = createdGameInfo.first
                )
                startActivity(intent)
            }
        }
    }

    override fun onStart() { //The screen is about to become visible: refresh its contents.
        super.onStart()
        updateChallengesList()
    }

    private fun updateChallengesList() { //Called whenever the challenges list is to be fetched again.
        layout.refreshLayout.isRefreshing = true
        viewModel.fetchChallenges()
    }

    private fun challengeSelected(challenge: ChallengeInfo) { //Action set when player selects a challenge. The player that accepts the challenge is the first to make a move
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.accept_challenge_dialog_title, challenge.challengerName))
            .setPositiveButton(R.string.ok) { _, _ -> viewModel.tryAcceptChallenge(challenge) }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }
}

class ChallengesListViewModel (app: Application) : AndroidViewModel(app) {
    private val app = getApplication<Chess4AndroidApp>()

    //Contains the result of the last attempt to fetch the challenges list
    private val _challenges: MutableLiveData<Result<List<ChallengeInfo>>> = MutableLiveData()
    val challenges: LiveData<Result<List<ChallengeInfo>>> = _challenges

    /**
     * Gets the challenges list by fetching them from the server. The operation's result is exposed
     * through [challenges]
     */
    fun fetchChallenges() =
        app.fireBase.fetchChallenges(onComplete = {
            _challenges.value = it
        })

    // Contains information about the enrolment in a game.
    private val _enrolmentResult: MutableLiveData<Result<Pair<ChallengeInfo, GameState>>?> = MutableLiveData()
    val enrolmentResult: LiveData<Result<Pair<ChallengeInfo, GameState>>?> = _enrolmentResult

    /**
     * Tries to accept the given challenge. The result of the asynchronous operation is exposed
     * through [enrolmentResult] LiveData instance.
     */
    fun tryAcceptChallenge(challengeInfo: ChallengeInfo) {
        val app = getApplication<Chess4AndroidApp>()
        Log.v(TAG, "Challenge accepted. Signalling by removing challenge from list")
        app.fireBase.deleteChallenge(
            challengeId = challengeInfo.id,
            onComplete = {
                it.onSuccess {
                    Log.v(TAG, "We successfully unpublished the challenge. Let's start the game")
                    app.fireBase.createGame(challengeInfo, onComplete = { game ->
                        _enrolmentResult.value = game
                    })
                }
            }
        )
    }
}