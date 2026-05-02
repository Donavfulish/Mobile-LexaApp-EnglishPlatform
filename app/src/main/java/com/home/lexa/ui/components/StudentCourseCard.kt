package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.databinding.CardStudentCourseBinding
import com.home.lexa.domain.models.ShortCourseDto


class StudentCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardStudentCourseBinding.inflate(LayoutInflater.from(context), this, true)


    fun setCourseData(
        data: ShortCourseDto,
        onActionClick: () -> Unit,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {

        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvAuthorName.text = data.creator_name
        binding.tvUserCount.text = data.studying_user_count.toString()
        binding.tvHeartCount.text = data.favorite_user_count.toString()
        binding.btnAction.text = "HỌC NGAY"


        binding.progressBar.setProgress(data.completed ?: 0)

        binding.tagCategory.setTagData(
            text = data.topic.name,
            colorHex = data.topic.colorHex,
            hasBorder = false
        )

        binding.ivThumbnail.load(data.thumbnail_url ?: Constants.DEFAULT_COURSE_IMAGE_URL) {
            crossfade(true)
            // Đã gỡ bỏ ic_launcher_background (màu xanh)
        }
        binding.ivAuthorAvatar.load(data.creator_avatar_url ?: Constants.DEFAULT_AVATAR_URL) {
            crossfade(true)
            // Đã gỡ bỏ ic_launcher_background (màu xanh)
        }

        binding.btnAction.setOnClickListener {
            onActionClick.invoke()
        }
        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }
        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
}