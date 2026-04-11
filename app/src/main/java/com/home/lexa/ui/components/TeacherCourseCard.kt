package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.CardTeacherCourseBinding
import coil.load
import com.home.lexa.R
import com.home.lexa.domain.models.ShortCourseDto

class TeacherCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Nạp file XML thu gọn vào đây
    private val binding = CardTeacherCourseBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * @param data: Dùng lại model CourseData đã tạo ở thẻ trước
     * @param onCardClick: Hàm callback khi user bấm vào toàn bộ thẻ
     * @param onOptionsClick: Hàm callback khi user bấm vào icon 3 chấm
     */
    fun setCourseData(
        data: ShortCourseDto,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {

        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvAuthorName.text = data.creator_name
        binding.tvHeartCount.text = data.studying_user_count.toString()
        binding.tvUserCount.text = data.studying_user_count.toString()


        binding.tagCategory.setTagData(
            text = data.topic.name,
            colorHex = data.topic.colorHex,
            hasBorder = false
        )

        binding.ivThumbnail.load(data.thumbnail_url) {
            crossfade(true) // Hiệu ứng mờ dần khi ảnh tải xong cho đẹp
            placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị tạm trong lúc chờ tải
            error(R.drawable.ic_launcher_background) // Ảnh hiển thị nếu link bị lỗi/mất mạng
        }
        binding.ivAuthorAvatar.load(data.creator_avatar_url) {
            crossfade(true) // Hiệu ứng mờ dần khi ảnh tải xong cho đẹp
            placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị tạm trong lúc chờ tải
            error(R.drawable.ic_launcher_background) // Ảnh hiển thị nếu link bị lỗi/mất mạng
        }

        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }

        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
}