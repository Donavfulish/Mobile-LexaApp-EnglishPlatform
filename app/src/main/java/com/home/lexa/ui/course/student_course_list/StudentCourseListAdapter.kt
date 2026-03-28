package com.home.lexa.ui.course.student_course_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.components.CourseData
import com.home.lexa.ui.components.DeckCard
import com.home.lexa.ui.components.FeaturedCourseCard

class StudentCourseListAdapter(
    private var courses: List<ShortCourseDto>,
    private val onCardClick: (ShortCourseDto) -> Unit
) : RecyclerView.Adapter<StudentCourseListAdapter.ViewHolder>() {

    class ViewHolder(val courseItem: DeckCard) :
        RecyclerView.ViewHolder(courseItem)

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
        val course = courses[position]

        holder.courseItem.setDeckCardData(
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
        this.courses = newList
        notifyDataSetChanged()
    }
}