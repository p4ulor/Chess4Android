package pt.isel.pdm.chess4android.activities

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.android.material.snackbar.Snackbar
import pt.isel.pdm.chess4android.*
import java.io.*
import java.util.*
import kotlin.text.StringBuilder
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.*

private const val TAG = "MainActivity"
private const val DATEFILE = "latest_data_fetch_date.txt"
//LiveData and Intent data keys
const val GAME_DTO_KEY = "game"

class MainActivity : AppCompatActivity() {

    private val layout /*(binding)*/ by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var continueButton: Button? = null

    private val viewModel: MainActivityViewModel by viewModels()
    //alternative:
    //private val thisViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        log("created"); super.onCreate(savedInstanceState)
        setContentView(layout.root) //mandatory before referencing view's using findViewById or layout.
        //setContentView(R.layout.activity_main); //alternative to what's above

        supportActionBar?.title = getString(R.string.welcome) //alternative: resources.getText(R.string.welcome) //only activity where I have to do this or the app name will be "Welcome" if I set it in the xml..., for all the other activities, their action bar name is set in the Manifest.xml

        //SET VIEWS (text, buttons, etc)
        continueButton = layout.continueButton
        //continueButton = findViewById(R.id.continueButton) //alternative

        val didScreenRotate = viewModel.currentScreenOrientation.value != applicationContext.resources.configuration.orientation
        if(didScreenRotate) {
            viewModel.currentScreenOrientation.value = applicationContext.resources.configuration.orientation
        }

        //the following code will only execute if the request of the json was sucessful, good example of a good use of the observing capacity of LiveData
        viewModel.isGameReady.observe(this){
            log("observed")
            if(it){
                //if(applicationContext.resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE) toast(R.string.iSurvived)
                continueButton?.isEnabled=true
                if(didScreenRotate && viewModel.updateDisplayed.value == true) { //could be elvis operator, but intellij optimized
                    layout.root.postDelayed ({toast("R.string.iSurvived", this)}, 1000)
                } else {
                    //toast(R.string.puzzleUpdated, this)
                    log(viewModel.gameDTO?.puzzle.toString())
                    log(viewModel.gameDTO?.solution.toString())
                    viewModel.updateDisplayed.value=true
                    /*doAsyncWithResult {
                        thisViewModel.updateDB()
                    }*/
                }
            } else snackBar(R.string.WTFerror)
        }

        continueButton?.setOnClickListener { launchGame() }
    }

    // "on" overwritten methods

    override fun onDestroy() {
        log("destroyed")
        super.onDestroy()
    }

    override fun onBackPressed() { //https://stackoverflow.com/questions/5914040/onbackpressed-to-hide-not-destroy-activity
        moveTaskToBack(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //aula dia 20, 27 outubro
        menuInflater.inflate(R.menu.credits_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }

            R.id.history -> {
                startActivity(Intent(this, GameHistoryActivity::class.java))
                true
            }

            R.id.play_offline_item -> {
                startActivity(Intent(this, ChessGameActivity::class.java))
                true
            }

            R.id.play_online_item -> {
                startActivity(Intent(this, ChallengesListActivity::class.java))
                true
            }
            else -> true //super.onOptionsItemSelected(item)
        }
    }

    private fun snackBar(stringID: Int){ //https://material.io/components/snackbars/android#using-snackbars //or function: () -> (Unit) https://stackoverflow.com/a/44132689
        Snackbar.make(findViewById(R.id.continueButton),getString(stringID), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if(viewModel.isGameReady.value == false) viewModel.getTodaysGame {} //if the snackbar appears, and user pressed continue and the game is ready, the snackbar remains on the screen, so when the user clicks "retry" it wont make the volley request again.
            }
            .show()
    }

    //Launch next activity
    private fun launchGame() {
        viewModel.getTodaysGame {
            if(viewModel.isGameReady.value == true) {
                val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
                    putExtra(GAME_DTO_KEY, viewModel.gameDTO)
                }
                startActivity(intent)
            } else toast(R.string.WTFerror, this)
        }
    }
}

private const val IS_GAME_READY_LIVEDATA_KEY = "ready"

class MainActivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) { //using AndroidViewModel and not ViewModel since AndroidViewModel can receive application: Application which extends from Context. And we need it for the file I/O, including the volley request
    init { log("MainActivityViewModel.init()") }
    private val context = getApplication<Application>()

    //since we don't absolutely need to notify the Activity when the data changed, using LiveData isn't necessarily necessary
    var gameDTO: GameDTO = GameDTO(null, null, null, null, false)
    val isGameReady: LiveData<Boolean> = state.getLiveData(IS_GAME_READY_LIVEDATA_KEY)
    var currentScreenOrientation: MutableLiveData<Int> = MutableLiveData(context.resources.configuration.orientation)
    var updateDisplayed: MutableLiveData<Boolean> = MutableLiveData(false)

    private val repo: Chess4AndroidRepo by lazy{
        getApplication<Chess4AndroidApp>().repo
    }

    fun getTodaysGame(callback: (Result<GameDTO?>) -> Unit) { //request to get the json from the lichess API, //https://developer.android.com/training/volley/simple    |    //https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md
        log("Getting the json...")
        repo.getTodaysGame (context) { result ->
            result.onSuccess { lichessGameOfTheDay ->
                if(lichessGameOfTheDay!=null){
                    gameDTO = lichessGameOfTheDay
                    if(!isDataNullOrEmpty()) {
                        state.set(IS_GAME_READY_LIVEDATA_KEY, true) //when this code executes, the code in "thisActivityViewModel.isGameReady.observe(this)" is also executed
                        callback(Result.success(gameDTO))
                    } else state.set(IS_GAME_READY_LIVEDATA_KEY, false)
                }
            }
        }
    }

    private fun isDataNullOrEmpty() = gameDTO?.id.isNullOrEmpty() || gameDTO?.puzzle.isNullOrEmpty() || gameDTO?.solution.isNullOrEmpty() || gameDTO?.date.isNullOrEmpty()
}