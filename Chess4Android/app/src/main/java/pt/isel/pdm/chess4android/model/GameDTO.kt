package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDTO(
    val lichessGameOfTheDayPuzzle: Array<String>?,
    val lichessGameOfTheDaySolution: Array<String>?,
    val puzzleID: String?,
    val date: String?
) : Parcelable


