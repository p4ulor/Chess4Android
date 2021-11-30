package pt.isel.pdm.chess4android

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.databinding.ActivityGameHistoryBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.GameHistoryViewAdapter

class GameHistoryActivity : AppCompatActivity(), OnItemClickListener {

    private val binding by lazy { ActivityGameHistoryBinding.inflate(layoutInflater) }
    private val thisViewModel by viewModels<GameHistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.history)

        binding.gameListRecyclerView.layoutManager = LinearLayoutManager(this)

        // load of history of games if not null
        (thisViewModel.history ?: thisViewModel.loadHistory()).observe(this){
            binding.gameListRecyclerView.adapter = GameHistoryViewAdapter(it, this)
        }
    }

    override fun onItemClicked(gameDTO: GameDTO) {
        toast(gameDTO.toString())
        launchGame(gameDTO)
    }

    private fun launchGame(gameDTO: GameDTO) {
        val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
            putExtra(GAME_DTO_KEY, gameDTO)
        }
        startActivity(intent)
    }

    override fun onCheckBoxClicked() = toast(R.string.clickedBox)

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))
}

fun GameTable.toGameDTO() = GameDTO( //extension function
    id = this.id,
    puzzle = this.puzzle,
    solution = this.solution,
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
                    id = it.id,
                    puzzle = it.puzzle,
                    solution = it.solution,
                    date = it.date,
                )
            }
        }
        history = result
        return result
    }
}

interface OnItemClickListener{
    fun onItemClicked(gameDTO: GameDTO)
    fun onCheckBoxClicked()
}