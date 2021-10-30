package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.* //https://kotlinlang.org/docs/serialization.html
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import pt.isel.pdm.chess4android.model.LichessJSON

private const val TAG = "MY_LOG_MyActivity"


class MainActivity : AppCompatActivity() {

    private var lichessDailyPuzzleURL: String = "https://lichess.org/api/puzzle/daily"
    private var lichessGameOfTheDay: LichessJSON? = null
    private var lichessGameOfTheDayPuzzle: List<String>? = null
    private var lichessGameOfTheDaySolution: List<String>? = null
    private var grandTopTitle: TextView? = null
    private var getGameButton: Button? = null

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

        getGameButton?.setOnClickListener { getTodaysGame() }

    }

    private fun getTodaysGame(){
        val queue = Volley.newRequestQueue(this) //https://developer.android.com/training/volley/simple
        val responseListener = Response.Listener<String> { response -> //https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md
            log(response.toString())

            lichessGameOfTheDay = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(),response)

            lichessGameOfTheDayPuzzle = lichessGameOfTheDay?.game?.pgn?.split(" ")
            lichessGameOfTheDaySolution = lichessGameOfTheDay?.puzzle?.solution

            log(lichessGameOfTheDayPuzzle.toString())
            log(lichessGameOfTheDaySolution.toString())

            toast("Game updated")
        }
        val errorListener = Response.ErrorListener {
            snackBar("Error, check wifi connection?", getTodaysGame())
        }

        val stringRequest = StringRequest(Request.Method.GET, lichessDailyPuzzleURL, responseListener, errorListener)
        queue.add(stringRequest)
    }

    //UTILITY METHODS
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun snackBar(text: String, func: Unit){ //https://material.io/components/snackbars/android#using-snackbars
        Snackbar.make(findViewById(R.id.getGameButton), text, Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                func
            }
            .show()
    }

    private fun log(string: String){
        Log.i(TAG, string)
    }
}

