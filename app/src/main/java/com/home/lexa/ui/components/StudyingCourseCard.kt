package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import coil.load
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.databinding.CardStudyingCourseBinding
import com.home.lexa.domain.models.GetStudyingCourseResponse

class StudyingCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CardStudyingCourseBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        binding.topic.setTextSize(12f)
        val color = ContextCompat.getColor(context, R.color.white_opacity)
        binding.topic.setBackground(color)
    }

    fun setData(course: GetStudyingCourseResponse) {
        binding.title.text = course.title
        binding.topic.setText(course.topic.name, null)
        setThumbnail(course.thumbnail_url)
        binding.linearProgressBar.progress = course.progress
        binding.titleProgress.text = "Đã hoàn thành ${course.progress}%"
    }
    fun setThumbnail(url: String?){
        val finalUrl = if (url.isNullOrBlank()) Constants.DEFAULT_COURSE_IMAGE_URL else url
        binding.background.load(finalUrl) {
            crossfade(true)
        }
    }
    fun setOnClickTopic(action: () -> Unit){
        binding.topic.setOnClickAction {
            action()
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

}