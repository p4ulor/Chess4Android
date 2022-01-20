package pt.isel.pdm.chess4android

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.serialization.json.Json
import pt.isel.pdm.chess4android.model.*
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

/*
 * Repository for the puzzle of the day. Design and purpose explained here: https://developer.android.com/jetpack/guide
 * The repository operations include I/O (network and/or DB accesses) and are therefore asynchronous.
 * NOTE: These async functions CANNOT have returns to return the resulting value. A callback MUST be used
 */

private const val TAG = "Repo"
private const val DATEFILE = "latest_data_fetch_date.txt"
const val LICHESSDAILYPUZZLEURL: String = "https://lichess.org/api/puzzle/daily"

class Chess4AndroidRepo(private val historyGameDAO: GameTableDAO) {

    private fun getLatestPuzzleFromDB(callback: (Result<GameTable?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyGameDAO.getLast(1).firstOrNull()
        }
    }

    private fun saveToDB(dto: GameDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyGameDAO.insert(dto.toGameTable())
        }
    }

    private fun getTodaysPuzzleFromAPI(context: Application?, callback: (Result<GameDTO?>) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val responseListener = Response.Listener<String> { response ->
            log(response.toString())
            val lichessGameOfTheDay = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(), response)
            val result = Result.success(lichessJSON_to_GameDTO(lichessGameOfTheDay))
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

    fun getTodaysGame(context: Application, callback: (Result<GameDTO?>) -> Unit) { //this method can either get the puzzle from the database if it's in the DB, if not, it makes a get request to the API
        getLatestPuzzleFromDB { maybeEntity ->
            val maybeGame = maybeEntity.getOrNull()
            if (maybeGame?.date == getTodaysDate()) {
                log(TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from local DB")
                callback(Result.success(maybeGame.toGameDTO()))
            } else {
                getTodaysPuzzleFromAPI(context) { apiResult ->
                    apiResult.onSuccess { gameDTO ->
                        log(TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from API")
                        saveToDB(gameDTO!!) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                log(TAG, "Thread ${Thread.currentThread().name}: Saved daily puzzle to local DB")
                                //writeDateOfTheLatestPuzzlePulled(context, getTodaysDate())
                                callback(Result.success(gameDTO))
                            }
                                .onFailure { Log.i(TAG, "Thread ${Thread.currentThread().name}: Failed to save daily puzzle to local DB", it)
                                    callback(Result.failure(it))
                                }
                        }
                    }
                    //callback(apiResult) //commenting this out fixed activity launching twice
                }
            }
        }
    }

    //Current date methods. No longer in use because it's unnecessary. But I keep it here so I don't forget how it's done.
    private fun writeDateOfTheLatestPuzzlePulled(context: Application, todaysDate: String) { //https://developer.android.com/training/data-storage/app-specific
        context.openFileOutput(DATEFILE, Context.MODE_PRIVATE).use {
            it.write(todaysDate.toByteArray())
            val filesDir = context.filesDir
            log("Saved $todaysDate to $filesDir/$DATEFILE") //data/user/0/pt.isel.pdm.chess4android/files/latest_data_fetch_data.txt
        }
    }

    fun todaysPuzzleWasNotPulled(context: Application) : Boolean = getTodaysDate() != readDateOfTheLatestPuzzlePull(context)

    private fun readDateOfTheLatestPuzzlePull(context: Application) : String { //https://developer.android.com/training/data-storage/app-specific
        var sb = StringBuilder()
        try {
            context.openFileInput(DATEFILE).bufferedReader().useLines { lines ->
                lines.forEach { line -> sb.append(line) }
            }
            log("$sb was read")
            return sb.toString()
        } catch (e: FileNotFoundException){
            log("$e creating new file...")
            context.openFileOutput(DATEFILE, AppCompatActivity.MODE_PRIVATE)
        }
        return ""
    }

    //Not used, maybe for now
    private fun readTXTFileFromAssets(
        context: Application,
        string: String
    ): String { //asset files are read only (by the app). And the user cannot access them through android's file explorer. https://stackoverflow.com/questions/10562904/is-asset-folder-read-only
        val i: InputStream = context.assets.open(string) //or ctx.resources.assets.open()
        val s: Scanner = Scanner(i).useDelimiter("\\A") //https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
        return if (s.hasNext()) s.next() else "" //reads only 1 line
    }
}

private fun lichessJSON_to_GameDTO(lichessJSON: LichessJSON) : GameDTO {
    val sb: StringBuilder = StringBuilder()
    for(i in lichessJSON.puzzle.solution){
        sb.append("$i ")
    }
    sb.deleteCharAt(sb.lastIndex)
    return GameDTO(lichessJSON.game.id, lichessJSON.game.pgn, sb.toString(), getTodaysDate(), false)
}

class Error(message: String = "", cause: Throwable? = null) : Exception(message, cause)

