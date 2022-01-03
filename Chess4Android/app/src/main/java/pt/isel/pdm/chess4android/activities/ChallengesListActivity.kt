package pt.isel.pdm.chess4android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityChallengesListBinding

class ChallengesListActivity : AppCompatActivity() {

    private val layout by lazy { ActivityChallengesListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
    }
}