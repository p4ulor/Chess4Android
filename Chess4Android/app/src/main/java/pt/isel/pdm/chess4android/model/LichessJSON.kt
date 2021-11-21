package pt.isel.pdm.chess4android.model
import kotlinx.serialization.*
import kotlin.arrayOfNulls

@Serializable
data class LichessJSON(
    val game: Game,
    val puzzle: Puzzle
)

@Serializable
data class Puzzle (
    val id: String,
    val rating: Int,
    val plays: Int,
    val initialPly: Int,
    val solution: Array<String>,
    val themes: Array<String>
)

@Serializable
class Game(
    val id: String,
    val perf: Perf,
    val rated: Boolean,
    val players: Array<Player>, //preferably there should only be 2, but we cant explicitly set size to 2
    val pgn: String
)
@Serializable
data class Player (
    val userId: String,
    val name: String,
    val color: String
)

@Serializable
data class Perf(
    val icon: String,
    val name: String
)