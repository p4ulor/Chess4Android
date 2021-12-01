package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDTO(
    var id: String?,
    var puzzle: String?,
    var solution: String?,
    var date: String?,
    var isDone: Boolean
) : Parcelable


