package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.home.lexa.databinding.CardTeacherCourseBinding
import coil.load
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.domain.models.ShortCourseDto

class TeacherCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardTeacherCourseBinding.inflate(LayoutInflater.from(context), this, true)

    fun setCourseData(
        data: ShortCourseDto,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {
        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvAuthorName.text = data.creator_name
        binding.tvHeartCount.text = data.favorite_user_count.toString()
        binding.tvUserCount.text = data.studying_user_count.toString()

        binding.tagCategory.setTagData(
            text = data.topic.name,
            colorHex = data.topic.colorHex,
            hasBorder = false
        )

        // Thumbnail
        val thumbnailUrl = if (data.thumbnail_url.isNullOrBlank()) Constants.DEFAULT_COURSE_IMAGE_URL else data.thumbnail_url
        binding.ivThumbnail.load(thumbnailUrl) {
            crossfade(true)
            placeholder(null) 
            error(Constants.DEFAULT_COURSE_IMAGE_URL)
        }

        // Author Avatar
        val avatarUrl = if (data.creator_avatar_url.isNullOrBlank()) Constants.DEFAULT_AVATAR_URL else data.creator_avatar_url
        binding.ivAuthorAvatar.load(avatarUrl) {
            crossfade(true)
            placeholder(null)
            error(Constants.DEFAULT_AVATAR_URL)
        }

        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }

        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
    fun ToggleDeleteMode(status: Boolean, onDeleteClick: () -> Unit) {
        binding.ivOptions.visibility = if (status) View.GONE else View.VISIBLE
        binding.ivDelete.visibility = if (status) View.VISIBLE else View.GONE
        binding.ivDelete.setOnClickListener { 
            onDeleteClick.invoke()
        }
    }
}
