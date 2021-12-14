package pt.isel.pdm.chess4android

import android.util.Log
import pt.isel.pdm.chess4android.model.GameDTO
import pt.isel.pdm.chess4android.model.GameTable
import pt.isel.pdm.chess4android.model.GameTableDAO
import pt.isel.pdm.chess4android.model.callbackAfterAsync

private const val TAG = "REPO"

class Chess4AndroidRepo(private val historyGameDAO: GameTableDAO) {

    fun getTodaysGame(todaysDate: String){

    }

    private fun asyncMaybeGetTodayQuoteFromDB(callback: (Result<GameTable?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyGameDAO.getLast(1).firstOrNull()
        }
    }

    private fun asyncGetTodayGameFromAPI(callback: (Result<GameDTO>) -> Unit) {

    }

    private fun asyncSaveToDB(dto: GameDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyGameDAO.insert(dto.toGameTable())
        }
    }

    fun fetchQuoteOfDay(mustSaveToDB: Boolean = false, callback: (Result<GameDTO>) -> Unit) {
        asyncMaybeGetTodayQuoteFromDB { maybeEntity ->
            val maybeQuote = maybeEntity.getOrNull()
            if (maybeQuote != null) {
                log(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from local DB")
                callback(Result.success(maybeQuote.toGameDTO()))
            }
            else {
                asyncGetTodayGameFromAPI { apiResult ->
                    apiResult.onSuccess { quoteDto ->
                        log(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from API")
                        asyncSaveToDB(quoteDto) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                log(TAG, "Thread ${Thread.currentThread().name}: Saved daily quote to local DB")
                                callback(Result.success(quoteDto))
                            }
                                .onFailure {
                                    Log.e(TAG, "Thread ${Thread.currentThread().name}: Failed to save daily quote to local DB", it)
                                    callback(if(mustSaveToDB) Result.failure(it) else Result.success(quoteDto))
                                }
                        }
                    }
                    callback(apiResult)
                }
            }
        }
    }
}

