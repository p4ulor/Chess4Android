package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pt.isel.pdm.chess4android.views.TileMatrix


private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    private var lichessGameOfTheDayPuzzle: Array<String>? = null
    private var lichessGameOfTheDaySolution: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)
        TileMatrix.forEach { tile ->
            tile?.setOnClickListener{
                val name: String = tile.piece?.toString() ?: "Empty"
                val color: String =if(tile.piece?.isWhite!!) "White" else "Black"
                Log.i(TAG, color+" "+name+ " in "+tile.piece?.position?.toString()) //very interesting, kotlin. tile.type.toString().lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }
        lichessGameOfTheDayPuzzle = intent.getStringArrayExtra(PUZZLE)
        lichessGameOfTheDaySolution = intent.getStringArrayExtra(SOLUTION)
    }

    override fun onBackPressed() {
        toast(R.string.progressLost)
        super.onBackPressed()
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

}