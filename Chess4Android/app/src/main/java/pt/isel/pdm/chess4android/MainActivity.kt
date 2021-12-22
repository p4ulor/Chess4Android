package pt.isel.pdm.chess4android

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
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.StringBuilder
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.*

private const val TAG = "MainActivity"
const val LICHESSDAILYPUZZLEURL: String = "https://lichess.org/api/puzzle/daily"
private const val DATEFILE = "latest_data_fetch_date.txt"
//LiveData and Intent data keys
const val GAME_DTO_KEY = "game"

class MainActivity : AppCompatActivity() {

    private var continueButton: Button? = null

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val thisViewModel: MainActivityViewModel by viewModels()
    //alternative:
    //private val thisViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        log("created")
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //mandatory before referencing view's using findViewById
        //setContentView(R.layout.activity_main); //alternative to what's above

        supportActionBar?.title = getString(R.string.welcome) //alternative: resources.getText(R.string.welcome) //only activity where I have to do this or the app name will be "Welcome" if I set it in the xml..., for all the other activities, their action bar name is set in the xml

        //SET VIEWS (text, buttons, etc)
        //continueButton = binding.continueButton //alternative
        continueButton = findViewById(R.id.continueButton)

        val didScreenRotate = thisViewModel.currentScreenOrientation.value != applicationContext.resources.configuration.orientation
        if(didScreenRotate) {
            thisViewModel.currentScreenOrientation.value = applicationContext.resources.configuration.orientation
        }

        //the following code will only execute if the request of the json was sucessful, good example of a good use of the observing capacity of LiveData
        thisViewModel.isGameReady.observe(this){
            log("observed")
            if(it){
                //if(applicationContext.resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE) toast(R.string.iSurvived)
                continueButton?.isEnabled=true
                if(didScreenRotate && thisViewModel.updateDisplayed.value == true) { //could be elvis operator, but intellij optimized
                    binding.root.postDelayed ({toast(R.string.iSurvived, this)}, 1000)
                } else {
                    toast(R.string.puzzleUpdated, this)
                    log(thisViewModel.gameDTO?.puzzle.toString())
                    log(thisViewModel.gameDTO?.solution.toString())
                    thisViewModel.updateDisplayed.value=true
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun snackBar(stringID: Int){ //https://material.io/components/snackbars/android#using-snackbars //or function: () -> (Unit) https://stackoverflow.com/a/44132689
        Snackbar.make(findViewById(R.id.continueButton),getString(stringID), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if(thisViewModel.isGameReady.value == false) thisViewModel.getTodaysGame {} //if the snackbar appears, and user pressed continue and the game is ready, the snackbar remains on the screen, so when the user clicks "retry" it wont make the volley request again.
            }
            .show()
    }

    //Launch next activity
    private fun launchGame() {
        thisViewModel.getTodaysGame {
            if(thisViewModel.isGameReady.value == true) {
                val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
                    putExtra(GAME_DTO_KEY, thisViewModel.gameDTO)
                }
                startActivity(intent)
            } else toast(R.string.WTFerror, this)
        }
    }
}

private const val IS_GAME_READY_LIVEDATA_KEY = "ready"

class MainActivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) { //using AndroidViewModel and not ViewModel since AndroidViewModel can receive application: Application which extends from Context. And we need it for the file I/O, including the volley request

    //since we don't absolutely need to notify the Activity when the data changed, using LiveData isn't necessarily necessary
    var gameDTO: GameDTO = GameDTO(null, null, null, null, false)
    val isGameReady: LiveData<Boolean> = state.getLiveData(IS_GAME_READY_LIVEDATA_KEY)
    val context = getApplication<Application>()
    var currentScreenOrientation: MutableLiveData<Int> = MutableLiveData(context.resources.configuration.orientation)
    var updateDisplayed: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        log("MainActivityViewModel.init()")
    }

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
                        writeDateOfTheLatestPuzzlePulled(getTodaysDate())
                        state.set(IS_GAME_READY_LIVEDATA_KEY, true) //when this code executes, the code in "thisActivityViewModel.isGameReady.observe(this)" is also executed
                        callback(Result.success(gameDTO))
                    } else state.set(IS_GAME_READY_LIVEDATA_KEY, false)
                }
            }
        }
    }

    fun isDataNullOrEmpty() = gameDTO?.id.isNullOrEmpty() || gameDTO?.puzzle.isNullOrEmpty() || gameDTO?.solution.isNullOrEmpty() || gameDTO?.date.isNullOrEmpty()

    //Current date methods

    fun todaysPuzzleWasNotPulled() : Boolean = !getTodaysDate().equals(readDateOfTheLatestPuzzlePull())

    private fun readDateOfTheLatestPuzzlePull() : String { //https://developer.android.com/training/data-storage/app-specific
        var sb = StringBuilder()
        try {
            context.openFileInput(DATEFILE).bufferedReader().useLines { lines ->
                lines.forEach { line -> sb.append(line) }
            }
            log("$sb was read")
            return sb.toString()
        } catch (e: FileNotFoundException){
            log("$e creating new file...")
            context.openFileOutput(DATEFILE, AppCompatActivity.MODE_PRIVATE)
            readDateOfTheLatestPuzzlePull() //could there be a problem by doing this recursively? hmmmm, likely not?
        }
        return ""
    }

    private fun writeDateOfTheLatestPuzzlePulled(string: String) { //same link as above
        context.openFileOutput(DATEFILE, Context.MODE_PRIVATE).use {
            it.write(string.toByteArray())
            val filesDir = context.filesDir
            log("Saved $string to $filesDir/$DATEFILE") //data/user/0/pt.isel.pdm.chess4android/files/latest_data_fetch_data.txt
        }
    }

    //Not used, maybe for now
    private fun readTXTFileFromAssets(string: String) : String { //asset files are read only (by the app). The user cannot access them through android's file explorer
        val i: InputStream = context.assets.open(string) //or ctx.resources.assets.open()
        val s: Scanner = Scanner(i).useDelimiter("\\A") //https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
        val result = if (s.hasNext()) s.next() else "" //reads only 1 line
        log(result)
        return result
    }
}