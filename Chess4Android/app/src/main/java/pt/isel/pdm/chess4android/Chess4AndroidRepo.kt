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
            historyGameDAO.insert(
                GameTable(
                    id = dto.id ?:"",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021",
                    isDone = false
                )
            )
        }
    }

    fun fetchQuoteOfDay(mustSaveToDB: Boolean = false, callback: (Result<GameDTO>) -> Unit) {
        asyncMaybeGetTodayQuoteFromDB { maybeEntity ->
            val maybeQuote = maybeEntity.getOrNull()
            if (maybeQuote != null) {
                Log.v(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from local DB")
                callback(Result.success(maybeQuote.toDTO()))
            }
            else {
                asyncGetTodayGameFromAPI { apiResult ->
                    apiResult.onSuccess { quoteDto ->
                        Log.v(TAG, "Thread ${Thread.currentThread().name}: Got daily quote from API")
                        asyncSaveToDB(quoteDto) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                Log.v(TAG, "Thread ${Thread.currentThread().name}: Saved daily quote to local DB")
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

fun GameTable.toDTO() = GameDTO( //extension function
    id = this.id,
    puzzle = this.puzzle,
    solution = this.solution,
    date = this.date,
    isDone = false
)
