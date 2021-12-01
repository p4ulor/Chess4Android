package pt.isel.pdm.chess4android.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.sql.Date
import java.util.concurrent.Executors

@Entity(tableName = "GAME") //represents tables in your app's database.
data class GameTable (
    @PrimaryKey val id: String,
    val puzzle: String,
    val solution: String,
    val date: String,
    val isDone: Boolean
)

@Dao // Data Access Object, provides methods that your app can use to query, update, insert, and delete data in the database
interface GameTableDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //https://stackoverflow.com/a/54260385/9375488
    fun insert(gameTable: GameTable)

    @Delete
    fun delete(gameTable: GameTable)

    @Update
    fun update(gameTable: GameTable)

    // the following 2 queries order by date year, month and then day, I found this MUCH easier than changing the type of my DTO and Table property to Date
    @Query("SELECT * FROM GAME ORDER BY SUBSTR(date, 7, 10) DESC, SUBSTR(date, 5, 5) DESC, SUBSTR(date, 1, 2) DESC")//to change color: Color scheme -> General -> Injected language fragment
    fun getAll() : List<GameTable>

    @Query("SELECT * FROM GAME ORDER BY SUBSTR(date, 1, 2) DESC, SUBSTR(date, 5, 5) DESC, SUBSTR(date, 7, 10) DESC LIMIT :count") //https://stackoverflow.com/questions/31016070/how-to-use-substring-in-rawquery-android
    fun getLast(count: Int) : List<GameTable>

    @Query("SELECT * FROM GAME WHERE id=:id")
    fun getGameWithID(id: String) : GameTable

    @Query("UPDATE GAME SET isDone=:isDone WHERE id=:id")
    fun setIsDone(id: String, isDone: Boolean)

}

@Database(entities = [GameTable::class], version = 1) //creates DB schema
abstract class GamesDataBase : RoomDatabase(){
    abstract fun getHistory() : GameTableDAO
}

private val dataAccessExecutor = Executors.newSingleThreadExecutor() // allocates a task to execute on a new thread

fun <T> doAsyncWithResult(action: () -> T) : MutableLiveData<T> {
    val result = MutableLiveData<T>()
    dataAccessExecutor.submit { result.postValue(action())}
    return result
}

fun doAsync(action: () -> Unit) = dataAccessExecutor.submit(action)
