package pt.isel.pdm.chess4android.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.GameDTO

class GameHistoryViewAdapter(private var gamesHistoryData: List<GameDTO>) : RecyclerView.Adapter<HistoryItemViewHolder>() { // a view that will create tuples that ammount to only filling up the whole screen, and that constant number of views will be reused when scrolling through all the tuples

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder { //creates View tuples/holders. It's called by the layout manager when it needs a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_tuple_view, parent, false)
        view.setOnClickListener{
            log("I was clicked on")
        }
        return HistoryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) { // re-use lists. Is called more than onCreateViewHolder
        holder.bindTo(gamesHistoryData[position])
    }

    override fun getItemCount(): Int = gamesHistoryData.size

    //-------------------- Above methods are obligated to be overrated

    fun loadNewHistoryData(newHistory: List<GameDTO>){
        gamesHistoryData = newHistory
        notifyDataSetChanged()
    }

    fun getGameDTO(position: Int) : GameDTO? {
        return if (position > -1 && position < gamesHistoryData.size) gamesHistoryData[position] else null
    }
}

/*
 * Implementation of the ViewHolder pattern. Its purpose is to eliminate the need for
 * executing findViewById each time a reference to a view's child is required.
 */
class HistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //receives a view and adds properties to the view
    val dateView = itemView.findViewById<TextView>(R.id.date)
    val idView = itemView.findViewById<TextView>(R.id.puzzleID)

    fun bindTo(gameDTO: GameDTO) { //Binds this view holder to the given quote item
        dateView.text = gameDTO.date
        idView.text = gameDTO.puzzleID
        dateView.setOnClickListener { log("the date was clicked on") }
        idView.setOnClickListener { log("the ID was clicked on") }
    }
}
