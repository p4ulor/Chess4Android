package pt.isel.pdm.chess4android.model

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import pt.isel.pdm.chess4android.Chess4AndroidApp
import pt.isel.pdm.chess4android.log

private const val TAG = "Worker"

class AutoGetPuzzleWorker(appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app : Chess4AndroidApp = applicationContext as Chess4AndroidApp
        val repo = app.repo

        log(TAG, "Thread ${Thread.currentThread().name}: Starting AutoGetPuzzleWorker")

        return CallbackToFutureAdapter.getFuture { completer ->
            repo.getTodaysGame { result ->
                result
                    .onSuccess {
                        log(TAG, "Thread ${Thread.currentThread().name}: DownloadDailyQuoteWorker succeeded")
                        completer.set(Result.success())
                    }
                    .onFailure {
                        log(TAG, "Thread ${Thread.currentThread().name}: DownloadDailyQuoteWorker failed")
                        completer.setException(it)
                    }
            }
        }
    }
}