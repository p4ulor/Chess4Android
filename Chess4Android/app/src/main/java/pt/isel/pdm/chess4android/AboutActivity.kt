package pt.isel.pdm.chess4android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

private const val GITHUBURL = "https://github.com/p4ulor/PDM-2122i-LI5X-G19"

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<ImageView>(R.id.gitHubButtonAndImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUBURL)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }
}