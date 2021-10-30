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
            Log.i(TAG, response.toString())
            //val data0 = Json.decodeFromString<LichessJSON>(response) //alternative 1, not ignoring unknown keys can lead to problems
            //val data1 = Json { ignoreUnknownKeys=true }.decodeFromString(Data.serializer(),response) //alternative 2
            lichessGameOfTheDay = Json { ignoreUnknownKeys = true }.decodeFromString(LichessJSON.serializer(),response)

            Log.i(TAG, lichessGameOfTheDay?.game?.pgn.toString())
            /*//using import org.json.JSONObject, which makes it a bit harder for us
            val jsonData = JSONObject(response)
            Log.i(TAG, "Response is: $response")
            val iterator = jsonData.keys()
            while(iterator.hasNext()){
                Log.i(TAG, "Response is: ${iterator.next()}")
            }*/

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
}

