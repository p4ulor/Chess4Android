package pt.isel.pdm.chess4android

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.GameState
import com.google.gson.Gson
import pt.isel.pdm.chess4android.model.FireBasePiece
import pt.isel.pdm.chess4android.model.PIECETYPE

private const val TAG = "FireBase"
// firebase collection and property names
private const val CHALLENGES_COLLECTION = "challenges"
private const val CHALLENGER_NAME = "challengerName"
private const val CHALLENGER_MESSAGE = "challengerMessage"
private const val GAMES_COLLECTION = "games"
private const val GAME_STATE_KEY = "game"

class FireBaseChallengesRepo {
    private val mapper: Gson by lazy { Gson() }
    private val fs = Firebase.firestore

    /* *** CHALLENGES METHODS *** */
    // Fetches the list of open challenges from firebase
    fun fetchChallenges(onComplete: (Result<List<ChallengeInfo>>) -> Unit) {
        val limit = 20 //Fetching ALL challenges is a bad design decision because the resulting data set size is unbounded
        fs.collection(CHALLENGES_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                log(TAG, "Repo got list from Firestore")
                for (document in result) {
                    log(TAG, "${document.id} => ${document.data}")
                }
                onComplete(Result.success(result.take(limit).map { it.toChallengeInfo() }))
            }
            .addOnFailureListener {
                log(TAG, "FireBaseRepo: Error {$it} occurred while fetching list from Firestore")
                onComplete(Result.failure(it))
            }
    }

    fun createChallenge(name: String, message: String, onComplete: (Result<ChallengeInfo>) -> Unit) {
        fs.collection(CHALLENGES_COLLECTION)
            .add(hashMapOf(CHALLENGER_NAME to name, CHALLENGER_MESSAGE to message))
            .addOnSuccessListener {
                onComplete(Result.success(ChallengeInfo(it.id, name, message)))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun deleteChallenge(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        fs.collection(CHALLENGES_COLLECTION).document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun listenToChallengeAcceptance(challengeId: String, onSubscriptionError: (Exception) -> Unit, onChallengeAccepted: () -> Unit): ListenerRegistration {
        return fs.collection(CHALLENGES_COLLECTION).document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener // local return to the caller of the lambda
                }

                if (snapshot?.exists() == false) { // Document has been removed, thereby signalling that someone accepted the challenge
                    onChallengeAccepted()
                }
            }
    }

    // Unsubscribes for changes in the challenge identified by [challengeId]
    fun cancelListeningToChallengeAcceptance(subscription: ListenerRegistration) {
        subscription.remove()
    }

    /* *** GAMES METHODS *** */

    // there's no "fetchGames()" because it's a private thing, the ongoing games shouldn't be public or provided anywhere

    fun createGame(challenge: ChallengeInfo, onComplete: (Result<Pair<ChallengeInfo, GameState>>) -> Unit) {
        val gameState = GameState(challenge.id, true, FireBasePiece(-1, PIECETYPE.EMPTY, false), FireBasePiece(-1, PIECETYPE.EMPTY, false), null)
        fs.collection(GAMES_COLLECTION).document(challenge.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(gameState)))
            .addOnSuccessListener { onComplete(Result.success(Pair(challenge, gameState))) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun updateGameState(gameState: GameState, onComplete: (Result<GameState>) -> Unit) {
        fs.collection(GAMES_COLLECTION).document(gameState.id)
            .set(hashMapOf(GAME_STATE_KEY to mapper.toJson(gameState)))
            .addOnSuccessListener { onComplete(Result.success(gameState)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun listenToGameStateChanges(gameID: String, onSubscriptionError: (Exception) -> Unit, onGameStateChange: (GameState) -> Unit): ListenerRegistration {
        return fs.collection(GAMES_COLLECTION).document(gameID)
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

    fun deleteGame(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        fs.collection(GAMES_COLLECTION).document(challengeId)
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
