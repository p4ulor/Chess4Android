package pt.isel.pdm.chess4android.activities

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import pt.isel.pdm.chess4android.Chess4AndroidApp
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityCreateChallengeBinding
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.User
import pt.isel.pdm.chess4android.toast

private const val TAG = "CreateChallenge"

class CreateChallengeActivity : AppCompatActivity() {

    private val layout by lazy { ActivityCreateChallengeBinding.inflate(layoutInflater) }
    private val viewModel: CreateChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(layout.root)

        viewModel.created.observe(this) {
            if (it == null) displayCreateChallenge()
            else it.onFailure { displayError() }.onSuccess {
                displayWaitingForChallenger()
            }
        }

        viewModel.accepted.observe(this) {
            if (it == true) {
                Log.v(TAG, "Someone accepted our challenge")
                viewModel.created.value?.onSuccess { challenge ->
                    val intent = ChessGameActivity.buildIntent(
                        origin = this,
                        turn = User.firstToMove,
                        local = User.firstToMove,
                        challengeInfo = challenge
                    )
                    startActivity(intent)
                }
            }
        }

        layout.makeChallengeButton.setOnClickListener {
            if (viewModel.created.value == null)
                viewModel.createChallenge(
                    layout.name.text.toString(),
                    layout.message.text.toString()
                )
            else viewModel.removeChallenge()
        }
    }

    private fun displayCreateChallenge() { //visually represent a state of readyness to create a challenge
        layout.makeChallengeButton.text = getString(R.string.makeChallenge)
        with(layout.name) { text.clear(); isEnabled = true }
        with(layout.message) { text.clear(); isEnabled = true }
        layout.loading.isVisible = false
        layout.waitingMessage.isVisible = false
    }

    private fun displayWaitingForChallenger() { //visually show it's waiting for a challenge
        layout.makeChallengeButton.text = getString(R.string.cancel)
        layout.name.isEnabled = false
        layout.message.isEnabled = false
        layout.loading.isVisible = true
        layout.waitingMessage.isVisible = true
    }

    /**
     * Displays the screen in its error creating challenge state
     */
    private fun displayError() {
        displayCreateChallenge()
        toast(R.string.errorChallenge,this)
    }
}

// Challenges are created by participants and are posted on the server, awaiting acceptance.
class CreateChallengeViewModel(app: Application) : AndroidViewModel(app) {

    //Used to publish the result of the challenge creation operation. Null if no challenge is currently published.
    private val _created: MutableLiveData<Result<ChallengeInfo>?> = MutableLiveData(null)
    val created: LiveData<Result<ChallengeInfo>?> = _created

    //Used to publish the acceptance state of the challenge
    private val _accepted: MutableLiveData<Boolean> = MutableLiveData(false)
    val accepted: LiveData<Boolean> = _accepted

    //Creates a challenge with the given arguments. The result is placed in [created]
    fun createChallenge(name: String, message: String) {
        getApplication<Chess4AndroidApp>().fireBase.createChallenge(name, message,
            onComplete = {
                _created.value = it
                it.onSuccess(::waitForAcceptance)
            }
        )
    }

    /**
     * Withdraws the current challenge from the list of available challenges.
     * @throws IllegalStateException if there's no challenge currently published
     */
    fun removeChallenge() {
        val currentChallenge = created.value
        check(currentChallenge != null && currentChallenge.isSuccess)
        val fb = getApplication<Chess4AndroidApp>().fireBase
        subscription?.let { fb.unsubscribeToChallengeAcceptance(it) }
        currentChallenge.onSuccess {
            fb.deleteChallenge(
                challengeId = it.id,
                onComplete = { _created.value = null }
            )
        }
    }

    override fun onCleared() { //Lets cleanup. The view model is about to be destroyed.
        if (created.value != null && created.value?.isSuccess == true)
            removeChallenge()
    }

    private var subscription: ListenerRegistration? = null

    private fun waitForAcceptance(challengeInfo: ChallengeInfo) {
        subscription = getApplication<Chess4AndroidApp>().fireBase.subscribeToChallengeAcceptance(
            challengeId = challengeInfo.id,
            onSubscriptionError = { _created.value = Result.failure(it) },
            onChallengeAccepted = { _accepted.value = true },
        )
    }
}