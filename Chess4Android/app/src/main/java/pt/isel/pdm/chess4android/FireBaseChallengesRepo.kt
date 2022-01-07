package pt.isel.pdm.chess4android

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.GameState
import com.google.gson.Gson

private const val TAG = "FireBase"
// firebase props
private const val CHALLENGES_COLLECTION = "challenges"
private const val CHALLENGER_NAME = "challengerName"
private const val CHALLENGER_MESSAGE = "challengerMessage"

private const val GAMES_COLLECTION = "games"
private const val GAME_STATE_KEY = "game"


class FireBaseChallengesRepo(private val mapper: Gson) {
/**
 * Fetches the list of open challenges from the backend
 *
 * Implementation note: We limit the maximum number of obtained challenges. Fetching ALL
 * challenges is a bad design decision because the resulting data set size is unbounded!
 */
fun fetchChallenges(onComplete: (Result<List<ChallengeInfo>>) -> Unit) {
    val limit = 30
    Firebase.firestore.collection(CHALLENGES_COLLECTION)
        .get()
        .addOnSuccessListener { result ->
            Log.v(TAG, "Repo got list from Firestore")
            onComplete(Result.success(result.take(limit).map { it.toChallengeInfo() }))
        }
        .addOnFailureListener {
            Log.e(TAG, "Repo: An error occurred while fetching list from Firestore")
            Log.e(TAG, "Error was $it")
            onComplete(Result.failure(it))
        }
}

    /**
     * Publishes a challenge with the given [name] and [message].
     */
    fun publishChallenge(
        name: String,
        message: String,
        onComplete: (Result<ChallengeInfo>) -> Unit
    ) {
        Firebase.firestore.collection(CHALLENGES_COLLECTION)
            .add(hashMapOf(CHALLENGER_NAME to name, CHALLENGER_MESSAGE to message))
            .addOnSuccessListener {
                onComplete(Result.success(ChallengeInfo(it.id, name, message)))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    /**
     * Withdraw the challenge with the given identifier.
     */
    fun withdrawChallenge(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    /**
     * Subscribes for changes in the challenge identified by [challengeId]
     */
    fun subscribeToChallengeAcceptance(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onChallengeAccepted: () -> Unit
    ): ListenerRegistration {

        return Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == false) {
                    // Document has been removed, thereby signalling that someone accepted
                    // the challenge
                    onChallengeAccepted()
                }
            }
    }

    /**
     * Unsubscribes for changes in the challenge identified by [challengeId]
     */
    fun unsubscribeToChallengeAcceptance(subscription: ListenerRegistration) {
        subscription.remove()
    }








    // GAMES IN PROGRESS

    /**
     * Creates the game for the given challenge ID
     */
    fun createGame(
        challenge: ChallengeInfo,
        onComplete: (Result<Pair<ChallengeInfo, GameState>>) -> Unit
    ) {
        /*val gameState = Board().toGameState(challenge.id)
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challenge.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(gameState)))
            .addOnSuccessListener { onComplete(Result.success(Pair(challenge, gameState))) }
            .addOnFailureListener { onComplete(Result.failure(it)) }*/
    }

    /**
     * Updates the shared game state
     */
    fun updateGameState(gameState: GameState, onComplete: (Result<GameState>) -> Unit) {

        /*Firebase.firestore.collection(GAMES_COLLECTION)
            .document(gameState.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(gameState)))
            .addOnSuccessListener { onComplete(Result.success(gameState)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }*/
    }

    /**
     * Subscribes for changes in the challenge identified by [challengeId]
     */
    fun subscribeToGameStateChanges(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onGameStateChange: (GameState) -> Unit
    ): ListenerRegistration {

        return Firebase.firestore
            .collection(GAMES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    val gameState = mapper.fromJson(
                        snapshot.get(GAME_STATE_KEY) as String,
                        GameState::class.java
                    )
                    onGameStateChange(gameState)
                }
            }
    }

    /**
     * Deletes the shared game state for the given challenge.
     */
    fun deleteGame(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }
}

/**
 * Extension function used to convert createdChallenge documents stored in the Firestore DB into
 * [ChallengeInfo] instances
 */
private fun QueryDocumentSnapshot.toChallengeInfo() =
    ChallengeInfo(
        id,
        data[CHALLENGER_NAME] as String,
        data[CHALLENGER_MESSAGE] as String
    )
