package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.databinding.ActivityGameHistoryBinding
import pt.isel.pdm.chess4android.model.*
import pt.isel.pdm.chess4android.views.GameHistoryViewAdapter

private const val TAG = "GameHistory"

class GameHistoryActivity : AppCompatActivity(), OnItemClickListener {

    private val layout by lazy { ActivityGameHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<GameHistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(layout.root)

        layout.gameListRecyclerView.layoutManager = LinearLayoutManager(this)

        log(TAG, "onCreate")
    }

    override fun onResume() { //I wasn't able to do or find any better solutions in order to update the recycler view in order for it to update the checkBox after successfully completing the puzzle and going back to this activity
        log(TAG,"onResume")
        viewModel.loadHistory()
        viewModel.history?.observe(this){
            layout.gameListRecyclerView.adapter = GameHistoryViewAdapter(it, this)
        }
        /*
        if(thisViewModel.position!=-1) { //doesnt work because I need to access and change the List<GameDTO>! in GameHistoryViewAdapter, and I wasn't able to figure out how to do it
            thisViewModel.loadHistory()
            binding.gameListRecyclerView.adapter?.notifyItemChanged(thisViewModel.position)
        } else thisViewModel.position = -1
         */
        super.onResume()
    }

    override fun onItemClicked(gameDTO: GameDTO, holderPosition: Int) {
        topToast(getString(R.string.youSelected)+gameDTO.id, this)
        viewModel.gameSelected = holderPosition
        launchGame(gameDTO)
    }

    private fun launchGame(gameDTO: GameDTO) {
        val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
            putExtra(GAME_DTO_KEY, gameDTO)
        }
        startActivity(intent)
    }

    override fun onCheckBoxClicked(isChecked: Boolean) {
        if(isChecked) toast(R.string.youCantAlter, this)
        else {
            play(R.raw.se_isto_volta_a_acontecer, this)
            toast(R.string.clickedBox, this)
        }
    }
}

class GameHistoryViewModel(application: Application) : AndroidViewModel(application){
    var history: LiveData<List<GameDTO>>? = null
        private set

    var gameSelected: Int = -1

    private val historyDB: GameTableDAO by lazy {
        getApplication<Chess4AndroidApp>().historyDB.getDAO()
    }

    fun loadHistory() : LiveData<List<GameDTO>> {
        val result = doAsyncWithResult {
            historyDB.getAll().map {
                it.toGameDTO()
            }
        }
        history = result
        return result
    }
}

interface OnItemClickListener{
    fun onItemClicked(gameDTO: GameDTO, holderPosition: Int)
    fun onCheckBoxClicked(isChecked: Boolean)
}