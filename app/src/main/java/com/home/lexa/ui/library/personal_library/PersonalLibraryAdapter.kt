package com.home.lexa.ui.library.personal_library

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.ui.components.PersonalDeckCard

class PersonalLibraryAdapter(
    private var decks: List<DeckDto>,
    private val onItemClick: (DeckDto) -> Unit,
//    private val onOptionsClick: (DeckDto) -> Unit
) : RecyclerView.Adapter<PersonalLibraryAdapter.ViewHolder>() {

    class ViewHolder(val personalDeckCard: PersonalDeckCard) :
        RecyclerView.ViewHolder(personalDeckCard)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val card = PersonalDeckCard(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
            }
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deck = decks[position]

        holder.personalDeckCard.setDeckCardData(
            data = deck,
            onItemClick = {
                onItemClick(deck)
            },
            onOptionsClick = { }
        )
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    fun updateData(newList: List<DeckDto>) {
        this.decks = newList
        notifyDataSetChanged()
    }
}