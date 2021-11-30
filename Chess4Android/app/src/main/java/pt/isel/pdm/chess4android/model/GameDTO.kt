package pt.isel.pdm.chess4android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDTO(
    val id: String?,
    val puzzle: String?,
    val solution: String?,
    val date: String?
) : Parcelable


