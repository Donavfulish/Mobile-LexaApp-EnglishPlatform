package com.home.lexa.ui.components

import android.content.Context

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.ViewDeckCardBinding
import coil.load
import com.home.lexa.R

data class CourseData(
    val title: String,
    val description: String,
    val authorName: String,
    val userCount: Int,
    val heartCount: Int,
    val wordCountText: String,
    val tagTitle: String,
    val tagColorHex: String,
    val thumbnail: String,
    val authorAvatar: String
)

class DeckCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewDeckCardBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Hàm nạp toàn bộ dữ liệu vào thẻ
     * @param data: Cục dữ liệu CourseData đã định nghĩa ở trên
     * @param onCardClick: Hàm xử lý khi bấm vào toàn bộ thẻ
     * @param onOptionsClick: Hàm xử lý khi bấm vào nút 3 chấm
     */
    fun setDeckCardData(
        data: CourseData,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {

        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvAuthorName.text = data.authorName
        binding.tvUserCount.text = data.userCount.toString()
        binding.tvHeartCount.text = data.heartCount.toString()
        binding.tvWordCount.text = data.wordCountText


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

        binding.ivAuthorAvatar.load(data.authorAvatar) {
            crossfade(true)
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