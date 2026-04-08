package com.home.lexa.ui.course.student_course_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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

//    fun updateData(newList: List<ShortCourseDto>) {
//        if (this.courses == newList) return
//
//        this.courses = newList
//        notifyDataSetChanged()
//    }

    fun updateData(newList: List<ShortCourseDto>) {
        // 1. Tạo một bộ so sánh nội bộ
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = courses.size
            override fun getNewListSize(): Int = newList.size

            // So sánh xem 2 item có phải là 1 khóa học không (dựa vào ID)
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return courses[oldItemPosition].id == newList[newItemPosition].id
            }

            // So sánh xem nội dung của khóa học đó có bị thay đổi không
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return courses[oldItemPosition] == newList[newItemPosition]
            }
        }

        // 2. Tính toán sự khác biệt giữa list cũ và list mới
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // 3. Gán data mới
        this.courses = newList

        // 4. Áp dụng sự thay đổi (Dòng này THAY THẾ HOÀN TOÀN notifyDataSetChanged)
        diffResult.dispatchUpdatesTo(this)
    }
}