package pt.isel.pdm.chess4android.views

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.model.ChallengeInfo

/**
 * Represents views (actually, the corresponding holder) that display the information pertaining to
 * a [ChallengeInfo] instance
 */
class ChallengeViewHolder(private val view: ViewGroup) : RecyclerView.ViewHolder(view) {

    private val challengerNameView: TextView = view.findViewById(R.id.challengerName)
    private val challengerMessageView: TextView = view.findViewById(R.id.message)

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(view.context, R.color.list_item_background),
            ContextCompat.getColor(view.context, R.color.list_item_background_selected),
            ContextCompat.getColor(view.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = radialGradientDrawable() //view.background as GradientDrawable doesnt work cuz my background is state type of background
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.start()

        animation.doOnEnd { onAnimationEnd() }
    }

    /**
     * Used to create an association between the current view holder instance and the given
     * data item
     *
     * @param   challenge               the challenge data item
     * @param   itemSelectedListener    the function to be called whenever the item is selected
     */
    fun bindTo(challenge: ChallengeInfo?, itemSelectedListener: (ChallengeInfo) -> Unit) {
        challengerNameView.text = challenge?.challengerName ?: ""
        challengerMessageView.text = challenge?.challengerMessage ?: ""

        if (challenge != null)
            view.setOnClickListener {
                itemView.isClickable = false
                startAnimation {
                    itemSelectedListener(challenge)
                    itemView.isClickable = true
                }
            }
    }
}

private fun radialGradientDrawable() : GradientDrawable { //https://android--code.blogspot.com/2020/06/android-kotlin-create-gradientdrawable.html
    return GradientDrawable().apply {
        colors = intArrayOf(
            Color.parseColor("#DA1884"),
            Color.parseColor("#FFF600"),
            Color.parseColor("#800020")
        )
        gradientType = GradientDrawable.RADIAL_GRADIENT
        shape = GradientDrawable.RECTANGLE

        gradientRadius = 350F

        // border around drawable
        setStroke(5,Color.parseColor("#CD5700"))
    }
}

/**
 * Adapts [ChallengeInfo] instances to be displayed in a [RecyclerView]
 */
class ChallengesListAdapter(
    private val contents: List<ChallengeInfo> = emptyList(),
    private val itemSelectedListener: (ChallengeInfo) -> Unit = { }) :
    RecyclerView.Adapter<ChallengeViewHolder>() {

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bindTo(contents[position], itemSelectedListener)
    }

    override fun getItemCount(): Int = contents.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.challenge_item_tuple_view, parent, false) as ViewGroup

        return ChallengeViewHolder(view)
    }
}
