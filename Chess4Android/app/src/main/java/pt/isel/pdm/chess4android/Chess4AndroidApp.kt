package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import androidx.room.Room
import pt.isel.pdm.chess4android.model.GameTable
import pt.isel.pdm.chess4android.model.GamesDataBase
import pt.isel.pdm.chess4android.model.doAsync

private const val TAG = "MY_LOG_APPLICATION"
private const val DB = "game-history"
class Chess4AndroidApp : Application() { //AppViewModel, SuperViewModel, SuperActivity, RootActivity,  omnipresent object throughout the app's lifecycle and in all activities, will be used as the means to access our local DB

    init {
        Log.i(TAG, "Chess4AndroidApp executed")
    }

    val historyDB: GamesDataBase by lazy {
        Room.inMemoryDatabaseBuilder(this, GamesDataBase::class.java).build()
    }

    override fun onCreate() {
        super.onCreate()
        doAsync {
            Log.v(TAG, "Initializing DB")

            historyDB.getHistory().insert( //DISPLAYS IN REVERSED ORDER
                GameTable(
                    id = "2456",
                    date = "25/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "1111",
                    date = "26/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "ay",
                    date = "27/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "ayyyy",
                    date = "27/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "whaaaaaaaaat",
                    date = "20/11/2021"
                )
            )
        }
    }
}