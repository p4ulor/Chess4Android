package pt.isel.pdm.chess4android.model

import androidx.room.*

@Entity(tableName = "GAME") //represents tables in your app's database.
data class GameTuple (
    @PrimaryKey val id: String,
    val date: String
)

@Dao // Data Access Object, provides methods that your app can use to query, update, insert, and delete data in the database
interface GameTupleDAO {
    @Insert
    fun insert(game: GameTuple)

    @Delete
    fun delete(game: GameTuple)

    @Query("SELECT * FROM GAME ORDER BY id DESC LIMIT 100") //Color scheme -> General -> Injected language fragment
    fun getAll() : List<GameTuple>

    @Query("SELECT * FROM GAME ORDER BY id DESC LIMIT :count")
    fun getLast(count: Int) : List<GameTuple>
}

@Database(entities = [GameTuple::class], version = 1) //creates DB schema
abstract class GamesDataBase : RoomDatabase(){
    abstract fun getHistory() : GameTupleDAO
}
