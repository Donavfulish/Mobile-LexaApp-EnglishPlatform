package com.home.lexa.ui.course.teacher_course_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.components.DeckCard
import com.home.lexa.ui.components.TeacherCourseCard
import com.home.lexa.ui.course.student_course_list.StudentCourseListAdapter
import com.home.lexa.ui.course.student_course_list.StudentCourseListAdapter.ViewHolder

class TeacherCourseListAdapter(
    private var courses: List<ShortCourseDto>,
    private val onCardClick: (ShortCourseDto) -> Unit
//    private val onItemClick: (DeckDto) -> Unit,
//    private val onOptionsClick: (DeckDto) -> Unit
) : RecyclerView.Adapter<TeacherCourseListAdapter.ViewHolder>() {

    class ViewHolder(val favoriteDeckCard: TeacherCourseCard) :
        RecyclerView.ViewHolder(favoriteDeckCard)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherCourseListAdapter.ViewHolder {
        val card = TeacherCourseCard(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
            }
        }
        return ViewHolder(card)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val deck = courses[position]
//
//        holder.favoriteDeckCard.setDeckCardData(
//            data = deck,
//            onCardClick = { },
//            onOptionsClick = { }
//        )
//    }

    override fun onBindViewHolder(holder: TeacherCourseListAdapter.ViewHolder, position: Int) {
        val course = courses[position]

        holder.favoriteDeckCard.setCourseData(
            data = course,
            onCardClick = {
                onCardClick(course)
            },
            onOptionsClick = { }
        )
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    fun updateData(newList: List<ShortCourseDto>) {
        if (this.courses == newList) return

        this.courses = newList
        notifyDataSetChanged()
    }
}