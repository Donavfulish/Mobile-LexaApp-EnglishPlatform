package com.home.lexa.ui.course.student_course_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.components.CourseData
import com.home.lexa.ui.components.DeckCard

class StudentCourseListAdapter(
    private var courses: List<ShortCourseDto>,
//    private val onItemClick: (DeckDto) -> Unit,
//    private val onOptionsClick: (DeckDto) -> Unit
) : RecyclerView.Adapter<StudentCourseListAdapter.ViewHolder>() {

    class ViewHolder(val favoriteDeckCard: DeckCard) :
        RecyclerView.ViewHolder(favoriteDeckCard)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val card = DeckCard(parent.context).apply {
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
        val deck = courses[position]

        holder.favoriteDeckCard.setDeckCardData(
            data = deck,
            onCardClick = { },
            onOptionsClick = { }
        )
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    fun updateData(newList: List<ShortCourseDto>) {
        this.courses = newList
        notifyDataSetChanged()
    }
}