package pt.isel.pdm.chess4android

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.room.Room
import pt.isel.pdm.chess4android.model.*
import java.text.DateFormat
import java.text.SimpleDateFormat

const val DATEPATTERN = "dd/M/yyyy"

private const val TAG = "MYLOG_"
private const val DB = "game-history" //DataBase key

var player: MediaPlayer = MediaPlayer()

class Chess4AndroidApp : Application() { //AppViewModel, SuperViewModel, SuperActivity, RootActivity,  omnipresent object throughout the app's lifecycle and in all activities, will be used as the means to access our local DB

    init {
        Log.i(TAG, "Chess4AndroidApp executed")
    }

    val historyDB: GamesDataBase by lazy {
        //Room.inMemoryDatabaseBuilder(this, GamesDataBase::class.java).build() works, but not what we want
        Room.databaseBuilder(this, GamesDataBase::class.java, DB).build()
    }

    override fun onCreate() {
        super.onCreate()
        doAsync {
            Log.v(TAG, "Initializing DB")
            // NOTE THAT WHEN YOU CHANGE THE TABLES COLUMNS YOU HAVE TO DELETE THE BD IN THE APPS FILES OR IT WONT APPEAR ANYTHING
            historyDB.getHistory().insert(
                GameTable(
                    id = "h34HkdjA",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021",
                    isDone = false
                )
            )
        }
    }
}

// UTILITY AND GLOBAL METHODS

fun play(id: Int, ctx: Context) {
    player = MediaPlayer.create(ctx, id)
    player.setOnCompletionListener { player.release() }
    player.start()
}

fun convertToDate(date: String?): java.sql.Date {
    val df: DateFormat = SimpleDateFormat(DATEPATTERN)
    return java.sql.Date(df.parse(date).time)
}

fun log(s: String) = Log.i(TAG, s)

fun log(t: String, s: String) =  Log.i(TAG+t, s)

fun log(arrayOfStrings: Array<String>?){
    val sb = StringBuilder()
    arrayOfStrings?.forEach { sb.append("$it ") }
    log(sb.toString())
}

fun toast(s: String, ctx: Context) = Toast.makeText(ctx, s, Toast.LENGTH_LONG).show()

fun toast(id: Int, ctx: Context) = toast(ctx.getString(id), ctx)

fun topToast(text: String, ctx: Context) {
    val toast = Toast.makeText(ctx, text, Toast.LENGTH_LONG)
    toast.setGravity(Gravity.TOP + Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.show()
}