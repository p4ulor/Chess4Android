package pt.isel.pdm.chess4android

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import pt.isel.pdm.chess4android.databinding.ActivityGameHistoryBinding
import pt.isel.pdm.chess4android.model.GameDTO
import pt.isel.pdm.chess4android.model.GameTuple
import pt.isel.pdm.chess4android.model.GamesDataBase
import pt.isel.pdm.chess4android.model.doAsyncWithResult
import pt.isel.pdm.chess4android.views.GameHistoryViewAdapter
private const val DB = "game-history"
class GameHistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGameHistoryBinding.inflate(layoutInflater) }
    private val thisViewModel by viewModels<GameHistoryViewModel>()
    val historyDB: GamesDataBase by lazy {
        Room.databaseBuilder(this, GamesDataBase::class.java, DB).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.history)

        binding.gameListRecyclerView.layoutManager = LinearLayoutManager(this)

        val gamesList: List<GameTuple> = historyDB.getHistory().getAll()
        binding.gameListRecyclerView.adapter = GameHistoryViewAdapter(gamesList.map{ it.toGameDTO() })

        // load of history is not null
        thisViewModel.history ?: thisViewModel.loadHistory().observe(this){
            binding.gameListRecyclerView.adapter = GameHistoryViewAdapter(it)
        }
    }
}

fun GameTuple.toGameDTO() = GameDTO( //extension function
    lichessGameOfTheDayPuzzle = null,
    lichessGameOfTheDaySolution = null,
    puzzleID = this.id,
    date = this.date
)

class GameHistoryViewModel(application: Application) : AndroidViewModel(application){
    var history: LiveData<List<GameDTO>>? = null
        private set

    fun loadHistory() : LiveData<List<GameDTO>> {
        val dao = getApplication<Application>().historyDB //??????????
        val result = doAsyncWithResult { dao.getAll() }
        history = result
        return result
    }

}