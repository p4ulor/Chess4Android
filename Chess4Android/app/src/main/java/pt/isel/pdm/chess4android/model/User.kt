package pt.isel.pdm.chess4android.model

// I named this User because Player is already in use by the LichessJSON
enum class User {

    CIRCLE, CROSS;

    companion object {
        val firstToMove: User = CIRCLE
    }

    /**
     * The other player
     */
    val other: User
        get() = if (this == CIRCLE) CROSS else CIRCLE
}
