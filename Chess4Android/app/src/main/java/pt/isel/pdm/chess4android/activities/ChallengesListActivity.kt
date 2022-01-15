package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.databinding.ActivityChallengesListBinding
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.GameState
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
            result.onSuccess { listOfChallenges ->
                layout.challengesList.adapter = ChallengesListAdapter(listOfChallenges, ::popConfirmationBoxUponChallenge)
                layout.refreshLayout.isRefreshing = false
            }
            result.onFailure {
                toast(R.string.errorChallenge, this)
                // (and the refreshLayout keeps the spinning symbol rolling) because isRefreshing wasn't set to false
            }
        }

        layout.refreshLayout.setOnRefreshListener { updateChallengesList() }

        layout.createChallengeButton.setOnClickListener {
            startActivity(Intent(this, CreateChallengeActivity::class.java))
        }

        viewModel.enrolmentResult.observe(this) {
            it?.onSuccess { createdGameInfo ->
                val intent = ChessGameActivity.onlineIntentConstructor(
                    context = this,
                    isWhitesPlayer = false,
                    isWhitesPlaying = true,
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

    private fun popConfirmationBoxUponChallenge(challenge: ChallengeInfo) { //Action set when player selects a challenge. The player that accepts the challenge is the first to make a move
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.accept_challenge_dialog_title, challenge.challengerName))
            .setPositiveButton(R.string.ok) { _, _ -> viewModel.tryAcceptChallenge(challenge) }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }
}

class ChallengesListViewModel (app: Application) : AndroidViewModel(app) {

    private val fb: FireBaseChallengesRepo by lazy { getApplication<Chess4AndroidApp>().fireBase }

    //Contains the result of the last attempt to fetch the challenges list
    private val _challenges: MutableLiveData<Result<List<ChallengeInfo>>> = MutableLiveData()
    val challenges: LiveData<Result<List<ChallengeInfo>>> = _challenges


    fun fetchChallenges() { //Gets the challenges list from firebase. And assigns it to [challenges]
        fb.fetchChallenges(onComplete = {
            _challenges.value = it
        })
    }

    // Contains information about the enrolment in a game.
    private val _enrolmentResult: MutableLiveData<Result<Pair<ChallengeInfo, GameState>>?> = MutableLiveData()
    val enrolmentResult: LiveData<Result<Pair<ChallengeInfo, GameState>>?> = _enrolmentResult

    // Tries to accept the given challenge. The result of the asynchronous operation is exposed through [enrolmentResult] LiveData instance.
    fun tryAcceptChallenge(challengeInfo: ChallengeInfo) {
        log(TAG, "Challenge accepted. Signalling by removing challenge from list")
        fb.deleteChallenge(challengeInfo.id,
            onComplete = {
                it.onSuccess {
                    log(TAG, "We successfully deleted the challenge. Let's start the game")
                    fb.createGame(challengeInfo,
                        onComplete = { game ->
                        _enrolmentResult.value = game
                    })
                }
            }
        )
    }
}