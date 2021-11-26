package pt.isel.pdm.chess4android

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.StringBuilder
import kotlinx.serialization.json.Json
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.model.LichessJSON

private const val TAG = "MY_LOG_MainActivity"
const val LICHESSDAILYPUZZLEURL: String = "https://lichess.org/api/puzzle/daily"
private const val DATEFILE = "latest_data_fetch_date.txt"
//LiveData and Intent data keys
const val PUZZLE = "puzzle"
const val SOLUTION = "solution"

class MainActivity : AppCompatActivity() {

    private var getGameButton: Button? = null
    private var continueButton: Button? = null

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val thisViewModel: MainActivityViewModel by viewModels()
    // alternative:
    //private val thisViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        log("created")
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //mandatory before referencing view's using findViewById
        //setContentView(R.layout.activity_main); //alternative to what's above

        supportActionBar?.title = getString(R.string.welcome) //alternative: resources.getText(R.string.welcome)

        //SET VIEWS (text, buttons, etc)
        //getGameButton = binding.getGameButton //alternative
        getGameButton = findViewById(R.id.getGameButton)
        continueButton = findViewById(R.id.continueButton)

        getGameButton?.setOnClickListener {
            if(isDataStateNotValid()){
                thisViewModel.getTodaysGame()
            }
            else {
                toast(R.string.alreadyUpdated)
            }
        }

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
                    binding.root.postDelayed ({toast(R.string.iSurvived)}, 1000)
                } else {
                    toast(R.string.puzzleUpdated)
                    log(thisViewModel.lichessGameOfTheDayPuzzle)
                    log(thisViewModel.lichessGameOfTheDaySolution)
                    thisViewModel.updateDisplayed.value=true
                }
            } else snackBar(R.string.connectionError)
        }

        continueButton?.setOnClickListener { launchGame() }
        log("continue button disabled") //on screen rotation, given this we can conclude that the isGameReady is observed AFTER this
        continueButton?.isEnabled = false //previously was if(isDataStateNotValid()) continueButton?.isEnabled = false, which checked for the values and date everytime, which isn't necessary
    }

    // "on" overwritten methods / Save state methods

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
        Snackbar.make(findViewById(R.id.getGameButton),getString(stringID), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                thisViewModel.getTodaysGame()
            }
            .show()
    }

    //Launch next activity
    private fun launchGame() {
        if(thisViewModel.isGameReady.value == false) {
            toast(R.string.WTFerror)
            return
        }
        val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
            putExtra(PUZZLE, thisViewModel.lichessGameOfTheDayPuzzle)
            putExtra(SOLUTION, thisViewModel.lichessGameOfTheDaySolution)
        }
        startActivity(intent)
    }

    //UTILITY METHODS

    private fun isDataStateNotValid() : Boolean = thisViewModel.wasTodaysPuzzleNotPulled() || thisViewModel.isDataNullOrEmpty()

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

    private fun log(arrayOfStrings: Array<String>?){
        val sb = StringBuilder()
        arrayOfStrings?.forEach { sb.append("$it ") }
        log(sb.toString())
    }
}

private fun log(s: String) = Log.i(TAG, s) //since both of the classes on this file use log, I put it here
private const val IS_GAME_READY_LIVEDATA_KEY = "ready"

class MainActivityViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) { //using AndroidViewModel and not ViewModel since AndroidViewModel can receive application: Application which extends from Context. And we need it for the file I/O, including the volley request
    init {
        log("MainActivityViewModel.init()")
    }
    //since we didnt absolutely need to notify the Activity when the data changed, using LiveData isnt necessarily necessary
    var lichessGameOfTheDayPuzzle: Array<String>? = null
    var lichessGameOfTheDaySolution: Array<String>? = null
    val isGameReady: LiveData<Boolean> = state.getLiveData(IS_GAME_READY_LIVEDATA_KEY)
    val context = getApplication<Application>()
    var currentScreenOrientation: MutableLiveData<Int> = MutableLiveData(context.resources.configuration.orientation)
    var updateDisplayed: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getTodaysGame() { //request to tget the json from the lichess API
        log("Getting the json...")
        val queue = Volley.newRequestQueue(context) //https://developer.android.com/training/volley/simple
        val responseListener = Response.Listener<String> { response -> //https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md
            //log(response.toString())

            val lichessGameOfTheDay: LichessJSON = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(),response)

            lichessGameOfTheDayPuzzle = lichessGameOfTheDay?.game?.pgn?.split(" ")?.toTypedArray()
            lichessGameOfTheDaySolution = lichessGameOfTheDay?.puzzle?.solution
            if(isDataNullOrEmpty()){
                state.set(IS_GAME_READY_LIVEDATA_KEY, false)
            } else state.set(IS_GAME_READY_LIVEDATA_KEY, true) //when this code executes, the code in "thisActivityViewModel.isGameReady.observe(this)" is also executed
            writeDateOfTheLatestPuzzlePulled(getTodaysDate())
        }
        val errorListener = Response.ErrorListener {
            log("Connection error, cant perform getTodaysGame()")
            state.set(IS_GAME_READY_LIVEDATA_KEY, false)
        }

        val stringRequest = StringRequest(Request.Method.GET, LICHESSDAILYPUZZLEURL, responseListener, errorListener)
        queue.add(stringRequest)
        log("Request finished")
    }

    fun isDataNullOrEmpty() = lichessGameOfTheDayPuzzle==null || lichessGameOfTheDaySolution==null || lichessGameOfTheDayPuzzle?.size==0 || lichessGameOfTheDaySolution?.size==0

    //Current date methods

    fun wasTodaysPuzzleNotPulled() : Boolean = !getTodaysDate().equals(readDateOfTheLatestPuzzlePull())

    private fun getTodaysDate() =  SimpleDateFormat("dd/M/yyyy").format(Date()) //The 'M' must be uppercase or it will read the minutes

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