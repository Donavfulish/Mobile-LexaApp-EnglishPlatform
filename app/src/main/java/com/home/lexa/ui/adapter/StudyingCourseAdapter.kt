package com.home.lexa.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.ui.components.StudyingCourseCard


class StudyingCourseAdapter(
    private val onCardClick: (GetStudyingCourseResponse) -> Unit

) : ListAdapter<GetStudyingCourseResponse, StudyingCourseAdapter.ViewHolder>(CourseProgressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 1. Khởi tạo Component
        val customCard = StudyingCourseCard(parent.context)

        // 2. Thiết lập thông số Layout (Dành cho danh sách cuộn ngang)
        val density = parent.context.resources.displayMetrics.density
        val cardWidthPixels = (280 * density).toInt() // Chiều rộng cố định 280dp

        val layoutParams = RecyclerView.LayoutParams(
            cardWidthPixels,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // Margin 8dp cho các cạnh để tạo khoảng thở
            val margin = (8 * density).toInt()
            setMargins(margin, margin, margin, margin)
        }

        customCard.layoutParams = layoutParams

        return ViewHolder(customCard)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val customCard: StudyingCourseCard) :
        RecyclerView.ViewHolder(customCard) {

        fun bind(course: GetStudyingCourseResponse) {
            customCard.apply {
                setData(course)
                setOnClickCard { onCardClick(course) }

            }
        }
    }

    class CourseProgressDiffCallback : DiffUtil.ItemCallback<GetStudyingCourseResponse>() {
        override fun areItemsTheSame(oldItem: GetStudyingCourseResponse, newItem: GetStudyingCourseResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GetStudyingCourseResponse, newItem: GetStudyingCourseResponse): Boolean {
            return oldItem == newItem
        }
    }
}