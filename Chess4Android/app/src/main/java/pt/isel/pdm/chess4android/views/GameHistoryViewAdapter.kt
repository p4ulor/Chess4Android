package pt.isel.pdm.chess4android.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.OnItemClickListener
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.GameDTO

class GameHistoryViewAdapter(
    private var gamesHistoryData: List<GameDTO>,
    private val itemClickedListener: OnItemClickListener
                                                    ) : RecyclerView.Adapter<HistoryItemViewHolder>() { // a view that will create tuples that ammount to only filling up the whole screen, and that constant number of views will be reused when scrolling through all the tuples

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder { //creates View tuples/holders. It's called by the layout manager when it needs a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_tuple_view, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) { // re-use lists. Is called more than onCreateViewHolder
        holder.bindTo(gamesHistoryData[position], itemClickedListener)
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
    private val dateView = itemView.findViewById<TextView>(R.id.date)
    private val idView = itemView.findViewById<TextView>(R.id.puzzleID)
    private val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)

    fun bindTo(gameDTO: GameDTO, itemClickedListener: OnItemClickListener) { //Binds this view holder to the given quote item
        dateView.text = gameDTO.date
        idView.text = gameDTO.puzzleID
        checkBox.setOnClickListener {
            itemClickedListener.onCheckBoxClicked()
            checkBox.isChecked=false //setting a setOnClickListener actually turns on isClickable, so we do this
        }
        itemView.setOnClickListener {
            itemClickedListener.onItemClicked(gameDTO)
        }
    }
}
