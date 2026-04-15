package com.home.lexa.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.domain.models.GetFeaturedCourseResponse


import com.home.lexa.ui.components.FeaturedCourseCard

class FeaturedCourseAdapter(

    private val onCardClick: (GetFeaturedCourseResponse) -> Unit,
    private val onFavoriteToggle: (GetFeaturedCourseResponse, Boolean) -> Unit,

) : ListAdapter<GetFeaturedCourseResponse, FeaturedCourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

        val customCard = FeaturedCourseCard(parent.context)

        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {

            val density = parent.context.resources.displayMetrics.density
            val marginHorizontal = (8 * density).toInt()
            val marginVertical = (2 * density).toInt()

            setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical)
        }

        customCard.layoutParams = layoutParams

        return CourseViewHolder(customCard)
    }
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

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

    class CourseDiffCallback : DiffUtil.ItemCallback<GetFeaturedCourseResponse>() {
        override fun areItemsTheSame(oldItem: GetFeaturedCourseResponse, newItem: GetFeaturedCourseResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GetFeaturedCourseResponse, newItem: GetFeaturedCourseResponse): Boolean {
            return oldItem == newItem
        }
    }
}