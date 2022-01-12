package pt.isel.pdm.chess4android.model

// I named this User because Player is already in use by the LichessJSON
enum class User {
    WHITE,
    BLACK;

    companion object {
        val firstToMove: User = WHITE
    }

    val opponent: User
    get() = if (this == WHITE) BLACK else WHITE
}
