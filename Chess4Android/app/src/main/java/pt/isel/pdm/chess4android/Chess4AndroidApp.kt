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
        //Room.inMemoryDatabaseBuilder(this, GamesDataBase::class.java).build() works, but not what we want
        Room.databaseBuilder(this, GamesDataBase::class.java, DB).build()
    }

    override fun onCreate() {
        super.onCreate()
        doAsync {
            Log.v(TAG, "Initializing DB")
            // NOTE THAT WHEN YOU CHANGE THE TABLES COLUMNS YOU HAVE TO DELETE THE BD IN THE APPS FILES OR IT WONT APPEAR ANYTHING
            historyDB.getHistory().insert( //kinda of displays in reverse order, except the 1st insert or something like that
                GameTable(
                    id = "h34HkdjA",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "AAAAA",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "BBBBB",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "CCCCCC",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "DDDDDDDD",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "EEEEEEEE",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )

            historyDB.getHistory().insert(
                GameTable(
                    id = "FFFF",
                    puzzle = "e4 e5 Nf3 Nc6 Bb5 a6 Ba4 b5 Bb3 Nf6 O-O Be7 Re1 O-O c3 d6 h3 Na5 Bc2 c5 d4 Qc7 Nbd2 h6 dxe5 dxe5 a4 Rd8 Qe2 b4 Bd3 Qd6 Nc4 Qxd3 Qxd3 Rxd3 Nxa5 bxc3 bxc3 Rxc3 Nc6 Bf8 Nfxe5 Bb7 f3 Re8 Rb1 Ba8 Bb2 Rc2 Kf1 Bd6 Nc4 Rxc4 e5 Bxc6 exf6 Rxe1+ Rxe1 Bg3 Rd1 Rc2",
                    solution = "d1d8 g8h7 f6g7 c2c1 b2c1",
                    date = "30/11/2021"
                )
            )
        }
    }


}