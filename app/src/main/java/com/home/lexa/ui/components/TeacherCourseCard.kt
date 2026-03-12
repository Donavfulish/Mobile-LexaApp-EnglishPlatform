package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.ViewTeacherCourseCardBinding


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


        binding.ivThumbnail.setImageResource(data.thumbnailRes)


        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }

        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
}