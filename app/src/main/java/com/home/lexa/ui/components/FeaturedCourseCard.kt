package com.home.lexa.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.databinding.CardFeaturedCourseBinding
import coil.load
import com.home.lexa.domain.models.GetFeaturedCourseResponse

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
    fun setData(course: GetFeaturedCourseResponse){
        binding.title.setText(course.title)

        binding.topic.setText(course.topic.name, null)
        binding.teacherName.setText(course.creator_name)
        binding.groupNum.setText(course.studying_user_count.toString())
        binding.favoriteNum.setText(course.favorite_user_count.toString())

        setFavoriteButtonSelected(course.is_favorite == true )
        setThumbnail(course.thumbnail_url)
        setTeacherImage(course.creator_avatar_url)
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
    fun setThumbnail(url: String?){
        val finalUrl = if (url.isNullOrBlank()) Constants.DEFAULT_COURSE_IMAGE_URL else url
        binding.background.load(finalUrl) {
            crossfade(true)
            placeholder(null) // Xóa bỏ ảnh xanh khi đang load
            error(R.drawable.default_course) // Dùng ảnh mặc định nếu link lỗi
        }
    }
    fun setTeacherImage(url: String?){
        val finalUrl = if (url.isNullOrBlank()) Constants.DEFAULT_AVATAR_URL else url
        binding.teacherIcon.load(finalUrl) {
            crossfade(true)
            placeholder(null)
            error(R.drawable.default_course)
        }
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