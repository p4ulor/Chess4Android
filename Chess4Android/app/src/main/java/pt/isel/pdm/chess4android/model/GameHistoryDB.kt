package pt.isel.pdm.chess4android.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.util.concurrent.Executors

@Entity(tableName = "GAME") //represents tables in your app's database.
data class GameTable (
    @PrimaryKey val id: String,
    val date: String
)

@Dao // Data Access Object, provides methods that your app can use to query, update, insert, and delete data in the database
interface GameTableDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gameEntity: GameTable)

    @Delete
    fun delete(gameEntity: GameTable)

    @Query("SELECT * FROM GAME ORDER BY id DESC LIMIT 100") //Color scheme -> General -> Injected language fragment
    fun getAll() : List<GameTable>

    @Query("SELECT * FROM GAME ORDER BY id DESC LIMIT :count")
    fun getLast(count: Int) : List<GameTable>
}

@Database(entities = [GameTable::class], version = 1) //creates DB schema
abstract class GamesDataBase : RoomDatabase(){
    abstract fun getHistory() : GameTableDAO
}

private val dataAcessExecutor = Executors.newSingleThreadExecutor() // allocates a task to execute on a new thread

fun <T> doAsyncWithResult(action: () -> T) : LiveData<T> {
    val result = MutableLiveData<T>()
    dataAcessExecutor.submit { result.postValue(action())}
    return result
}

fun doAsync(action: () -> Unit) = dataAcessExecutor.submit(action)
