package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.ViewTeacherCourseCardBinding
import coil.load
import com.home.lexa.R

class TeacherCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Nạp file XML thu gọn vào đây
    private val binding = ViewTeacherCourseCardBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * @param data: Dùng lại model CourseData đã tạo ở thẻ trước
     * @param onCardClick: Hàm callback khi user bấm vào toàn bộ thẻ
     * @param onOptionsClick: Hàm callback khi user bấm vào icon 3 chấm
     */
    fun setCourseData(
        data: CourseData,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {

        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvHeartCount.text = data.heartCount.toString()
        binding.tvUserCount.text = data.userCount.toString()


        binding.tagCategory.setTagData(
            text = data.tagTitle,
            colorHex = data.tagColorHex,
            hasBorder = false
        )

        binding.ivThumbnail.load(data.thumbnail) {
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