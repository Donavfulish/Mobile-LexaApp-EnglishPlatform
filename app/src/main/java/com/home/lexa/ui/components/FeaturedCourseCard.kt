package com.home.lexa.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.databinding.CardFeaturedCourseBinding
import com.home.lexa.domain.models.CourseTest

class FeaturedCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = CardFeaturedCourseBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.topic.setTextSize(12f)
        val color = ContextCompat.getColor(context, R.color.white_opacity)
        binding.topic.setBackground(color)
        binding.favoriteBtn.isSelected = false
    }
    fun setData(course: CourseTest){
        binding.title.setText(course.title)
        binding.topic.setText(course.topic, null)
        binding.teacherName.setText(course.teacher)
        binding.background.setImageResource(course.imageRes)
        binding.groupNum.setText(course.member)
        binding.favoriteNum.setText(course.favorite)
        setFavoriteButtonSelected(course.isFavorite)
    }

    fun setTitle(title: String){
        binding.title.setText(title)
    }
    fun setTopic(topic: String){
        binding.topic.setText(topic, null)
    }
    fun setTeacherName(name: String){
        binding.teacherName.setText(name)
    }
    fun setGroupNum(num: String){
        binding.groupNum.setText(num)
    }
    fun setFavoriteNum(num: String){
        binding.favoriteNum.setText(num)
    }
    fun setImageBackground(img: Drawable){
        binding.background.setImageDrawable(img)
    }
    fun setFavoriteButtonSelected(isSelected: Boolean) {
        binding.favoriteBtn.isSelected = isSelected
    }

    fun setOnClickToggleFavoriteButton(onToggle: (Boolean) -> Unit){
        binding.favoriteBtn.setOnClickListener {
            val selected = !binding.favoriteBtn.isSelected
            binding.favoriteBtn.isSelected = selected
            onToggle(selected)
        }
    }

    fun setOnClickCard(action: () -> Unit){
        binding.title.setOnClickListener {
            action.invoke()
        }
        binding.background.setOnClickListener {
            action.invoke()
        }
    }

    fun setOnClickTopic(action: () -> Unit){
        binding.topic.setOnClickAction {
            action()
        }
    }

}