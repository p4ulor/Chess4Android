package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pt.isel.pdm.chess4android.model.ChessPieces
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.Tile
import pt.isel.pdm.chess4android.views.TileMatrix


private const val TAG = "MY_LOG_PuzzleSolvingActivity"

class PuzzleSolvingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_solving)
        TileMatrix.forEach { tile ->
            tile?.setOnClickListener{
                val name :String = tile.piece?.toString() ?: "Empty"
                Log.i(TAG, name) //very interesting, kotlin. tile.type.toString().lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }

    }

    override fun onBackPressed() {
        toast(R.string.cantBack)
    }
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun toast(id: Int) = toast(getString(id))

}