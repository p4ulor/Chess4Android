package pt.isel.pdm.chess4android

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.databinding.ActivityGameHistoryBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.GameHistoryViewAdapter

class GameHistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGameHistoryBinding.inflate(layoutInflater) }
    private val thisViewModel by viewModels<GameHistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.history)

        binding.gameListRecyclerView.layoutManager = LinearLayoutManager(this)

        // load of history of games fs not null
        (thisViewModel.history ?: thisViewModel.loadHistory()).observe(this){
            binding.gameListRecyclerView.adapter = GameHistoryViewAdapter(it)
        }
    }
}

fun GameTable.toGameDTO() = GameDTO( //extension function
    lichessGameOfTheDayPuzzle = null,
    lichessGameOfTheDaySolution = null,
    puzzleID = this.id,
    date = this.date
)

class GameHistoryViewModel(application: Application) : AndroidViewModel(application){
    var history: LiveData<List<GameDTO>>? = null
        private set

    private val historyDB : GameTableDAO by lazy {
        getApplication<Chess4AndroidApp>().historyDB.getHistory()
    }

    fun loadHistory() : LiveData<List<GameDTO>> {
        val result = doAsyncWithResult {
            historyDB.getAll().map {
                GameDTO(
                    lichessGameOfTheDayPuzzle = null,
                    lichessGameOfTheDaySolution = null,
                    puzzleID = it.id,
                    date = it.date,
                )
            }
        }
        history = result
        return result
    }

}