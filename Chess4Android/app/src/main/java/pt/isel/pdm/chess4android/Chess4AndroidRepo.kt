package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.serialization.json.Json
import pt.isel.pdm.chess4android.model.*

/*
 * Repository for the puzzle of the day. Design and purpose explained here: https://developer.android.com/jetpack/guide
 * The repository operations include I/O (network and/or DB accesses) and are therefore asynchronous.
 * NOTE: These async functions CANNOT have returns to return the resulting value. A callback MUST be used
 */

private const val TAG = "Repo"

class Chess4AndroidRepo(private val historyGameDAO: GameTableDAO) {

    private fun getLatestPuzzleFromDB(callback: (Result<GameTable?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyGameDAO.getLast(1).firstOrNull()
        }
    }

    fun getTodaysPuzzleFromAPI(context: Application?, callback: (Result<GameDTO?>) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val responseListener = Response.Listener<String> { response ->
            log(response.toString())
            val lichessGameOfTheDay = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(),response)
            val result = Result.success(LichessJSON_to_GameDTO(lichessGameOfTheDay))
            callback(result)
            log("Response received")
        }
        val errorListener = Response.ErrorListener {
            log("Connection error, cant perform getTodaysGame()")
            val result = Result.failure<GameDTO?>(Error())
            callback(result)
        }

        val stringRequest = StringRequest(Request.Method.GET, LICHESSDAILYPUZZLEURL, responseListener, errorListener)
        queue.add(stringRequest)
        log("Request finished")
    }

    private fun saveToDB(dto: GameDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyGameDAO.insert(dto.toGameTable())
        }
    }

    fun getTodaysGame(callback: (Result<GameDTO?>) -> Unit) {
        getLatestPuzzleFromDB { maybeEntity ->
            val maybeQuote = maybeEntity.getOrNull()
            if (maybeQuote != null) {
                log(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from local DB")
                callback(Result.success(maybeQuote.toGameDTO()))
            }
            else {
               getTodaysPuzzleFromAPI (null) { apiResult ->
                    apiResult.onSuccess { gameDTO ->
                        log(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from API")
                        saveToDB(gameDTO!!) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                log(TAG, "Thread ${Thread.currentThread().name}: Saved daily quote to local DB")
                                callback(Result.success(gameDTO))
                            }
                                .onFailure {
                                    Log.i(TAG, "Thread ${Thread.currentThread().name}: Failed to save daily quote to local DB", it)
                                    callback(Result.failure(it))
                                }
                        }
                    }
                    callback(apiResult)
                }
            }
        }
    }
}

fun LichessJSON_to_GameDTO(lichessJSON: LichessJSON) : GameDTO {
    val sb: StringBuilder = StringBuilder()
    for(i in lichessJSON.puzzle.solution){
        sb.append("$i ")
    }
    sb.deleteCharAt(sb.lastIndex)
    return GameDTO(lichessJSON.game.id, lichessJSON.game.pgn, sb.toString(), getTodaysDate(), false)
}

class Error(message: String = "", cause: Throwable? = null) : Exception(message, cause)

