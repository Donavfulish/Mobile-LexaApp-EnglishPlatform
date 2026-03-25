package com.home.lexa.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.GetFeaturedCourseResponse


import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.components.FeaturedCourseCard

class FeaturedCourseAdapter(

    private val onCardClick: (GetFeaturedCourseResponse) -> Unit,
    private val onFavoriteToggle: (GetFeaturedCourseResponse, Boolean) -> Unit,

) : ListAdapter<GetFeaturedCourseResponse, FeaturedCourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        // 1. Khởi tạo trực tiếp Component của bạn bằng Context
        val customCard = FeaturedCourseCard(parent.context)

        // 2. Thiết lập chiều rộng, chiều cao và margin bằng code (thay vì dùng XML)
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // Công thức chuyển đổi từ dp sang pixel để set margin
            val density = parent.context.resources.displayMetrics.density
            val marginHorizontal = (8 * density).toInt()
            val marginVertical = (2 * density).toInt()

            setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical)
        }

        // Áp dụng thông số layout cho card
        customCard.layoutParams = layoutParams

        return CourseViewHolder(customCard)
    }
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    // Bỏ qua ViewBinding, truyền trực tiếp CustomCard vào ViewHolder
    inner class CourseViewHolder(private val customCard: FeaturedCourseCard) :
        RecyclerView.ViewHolder(customCard) {

        fun bind(course: GetFeaturedCourseResponse) {
            customCard.apply {
                setData(course)
                setOnClickCard { onCardClick(course) }
                setOnClickToggleFavoriteButton { isSelected -> onFavoriteToggle(course, isSelected) }
            }
        }
    }

    // Tự động so sánh sự thay đổi của danh sách để update UI tối ưu nhất
    class CourseDiffCallback : DiffUtil.ItemCallback<GetFeaturedCourseResponse>() {
        override fun areItemsTheSame(oldItem: GetFeaturedCourseResponse, newItem: GetFeaturedCourseResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GetFeaturedCourseResponse, newItem: GetFeaturedCourseResponse): Boolean {
            return oldItem == newItem
        }
    }
}