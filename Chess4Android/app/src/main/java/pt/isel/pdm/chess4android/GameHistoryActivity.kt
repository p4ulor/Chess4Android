package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import pt.isel.pdm.chess4android.databinding.ActivityGameHistoryBinding
import pt.isel.pdm.chess4android.model.GameDTO
import pt.isel.pdm.chess4android.model.GameTuple
import pt.isel.pdm.chess4android.model.GamesDataBase
import pt.isel.pdm.chess4android.views.GameHistoryViewAdapter
private const val DB = "game-history"
class GameHistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGameHistoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.history)

        val historyDB: GamesDataBase by lazy {
            Room.databaseBuilder(this, GamesDataBase::class.java, DB).build()
        }

        binding.gameList.layoutManager = LinearLayoutManager(this)
        val gamesList: List<GameTuple> = historyDB.getHistory().getAll()
        /*                                               (                              */
        binding.gameList.adapter = GameHistoryViewAdapter(gamesList.map{ it.toGameDTO() })
        /*    listOf(
                GameDTO(
                    lichessGameOfTheDayPuzzle = null,
                    lichessGameOfTheDaySolution = null,
                    puzzleID = "ay",
                    date = "25/11/2021",
                )
            )
        )
        */
    }
}

fun GameTuple.toGameDTO() = GameDTO( //extension function
    lichessGameOfTheDayPuzzle = null,
    lichessGameOfTheDaySolution = null,
    puzzleID = this.id,
    date = this.date
)

class GameHistoryViewModel : ViewModel(){

}