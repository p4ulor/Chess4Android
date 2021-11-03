package pt.isel.pdm.chess4android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json
import pt.isel.pdm.chess4android.model.LichessJSON
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction0
import kotlin.text.StringBuilder

private const val TAG = "MY_LOG_MainActivity"
private const val LICHESSDAILYPUZZLEURL: String = "https://lichess.org/api/puzzle/daily"
private const val DATEFILE = "latest_data_fetch_date.txt"

class MainActivity : AppCompatActivity() {

    private var lichessGameOfTheDay: LichessJSON? = null
    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null
    private var grandTopTitle: TextView? = null
    private var getGameButton: Button? = null
    private var continueButton: Button? = null

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //mandatory before referencing view's using findViewById
        //setContentView(R.layout.activity_main); //alternative to what's above
        //SET VIEWS (text, buttons, etc)
        grandTopTitle = findViewById(R.id.grandTopTitle)
        getGameButton = findViewById(R.id.getGameButton)
        continueButton = findViewById(R.id.continueButton)

        getGameButton?.setOnClickListener { getTodaysGame() }
        continueButton?.setOnClickListener { launchGame() }

        continueButton?.isEnabled = false
        getTodaysGame()
    }

    private fun getTodaysGame(){
        if(getTodaysDate().equals(readDateOfTheLatestPuzzlePull()) && lichessGameOfTheDay!=null){
            toast(R.string.alreadyUpdated)
            continueButton?.isEnabled = true
            return
        }
        val queue = Volley.newRequestQueue(this) //https://developer.android.com/training/volley/simple
        val responseListener = Response.Listener<String> { response -> //https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md
            log(response.toString())

            lichessGameOfTheDay = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(),response)

            lichessGameOfTheDayPuzzle =  lichessGameOfTheDay?.game?.pgn?.split(" ")?.toTypedArray()

            lichessGameOfTheDaySolution = lichessGameOfTheDay?.puzzle?.solution

            writeDateOfTheLatestPuzzlePulled(getTodaysDate())
            continueButton?.isEnabled = true

            toast(R.string.puzzleUpdated)

            log(lichessGameOfTheDayPuzzle)
            log(lichessGameOfTheDaySolution)
        }
        val errorListener = Response.ErrorListener {
            log("Connection error, cant perform getTodaysGame()")
            snackBar(R.string.jsonError, ::getTodaysGame)
        }

        val stringRequest = StringRequest(Request.Method.GET, LICHESSDAILYPUZZLEURL, responseListener, errorListener)
        queue.add(stringRequest)
    }

    private fun getTodaysDate() =  SimpleDateFormat("dd/M/yyyy").format(Date()) //M must be uppercase or it will read the minutes

    private fun readDateOfTheLatestPuzzlePull() : String { //https://developer.android.com/training/data-storage/app-specific
        var sb = StringBuilder()
        this.openFileInput(DATEFILE).bufferedReader().useLines { lines ->
            lines.forEach { line -> sb.append(line) }
        }
        log("$sb was read")
        return sb.toString()
    }

    private fun writeDateOfTheLatestPuzzlePulled(string: String) { //same link as above
        this.openFileOutput(DATEFILE, Context.MODE_PRIVATE).use {
            it.write(string.toByteArray())
            log("Saved $string to $filesDir/$DATEFILE") //data/user/0/pt.isel.pdm.chess4android/files/latest_data_fetch_data.txt
        }
    }

    //Launch other activity
    private fun launchGame() {
        if(lichessGameOfTheDayPuzzle==null || lichessGameOfTheDaySolution==null || lichessGameOfTheDayPuzzle?.size==0 || lichessGameOfTheDaySolution?.size==0) {
            toast(R.string.WTFerror)
            return
        }
        val intent = Intent(this, PuzzleSolvingActivity::class.java).apply {
            putExtra("PUZZLE", lichessGameOfTheDayPuzzle)
            putExtra("SOLUTION", lichessGameOfTheDaySolution)
        }
        startActivity(intent)
    }

    //UTILITY METHODS

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

    private fun snackBar(stringID: Int, function: KFunction0<Unit>){ //https://material.io/components/snackbars/android#using-snackbars //or function: () -> (Unit) https://stackoverflow.com/a/44132689
        Snackbar.make(findViewById(R.id.getGameButton), getString(stringID), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                function()
            }
            .show()
    }

    private fun log(string: String) = Log.i(TAG, string)

    private fun log(arrayOfStrings: Array<String>?){
        val sb = StringBuilder()
        arrayOfStrings?.forEach { sb.append("$it ") }
        log(sb.toString())
    }

    //Not used, maybe for now
    private fun readTXTFileFromAssets(string: String) : String { //asset files are read only (by the app). The user cannot access them through android's file explorer
        val i: InputStream = baseContext.assets.open(string) //or ctx.resources.assets.open()
        val s: Scanner = Scanner(i).useDelimiter("\\A") //https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
        val result = if (s.hasNext()) s.next() else ""
        log(result)
        return result
    }
}

