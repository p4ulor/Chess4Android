package pt.isel.pdm.chess4android.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.databinding.ActivityChessGameBinding
import pt.isel.pdm.chess4android.model.ChallengeInfo
import pt.isel.pdm.chess4android.model.User

private const val GAME_EXTRA = "GameActivity.GameInfoExtra"
private const val LOCAL_PLAYER_EXTRA = "GameActivity.LocalPlayerExtra"

class ChessGameActivity : AppCompatActivity() {

    private val layout by lazy { ActivityChessGameBinding.inflate(layoutInflater) }

    companion object {
        fun buildIntent(origin: Context, local: User, turn: User, challengeInfo: ChallengeInfo) =
            Intent(origin, ChessGameActivity::class.java)
                //.putExtra(GAME_EXTRA, Board(turn = turn).toGameState(challengeInfo.id))
                .putExtra(LOCAL_PLAYER_EXTRA, local.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)
    }
}