package com.home.lexa.ui.components

import android.content.Context

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.ViewDeckCardBinding
data class CourseData(
    val title: String,
    val description: String,
    val authorName: String,
    val userCount: Int,
    val heartCount: Int,
    val wordCountText: String,
    val tagTitle: String,
    val tagColorHex: String,
    val thumbnailRes: Int,
    val authorAvatarRes: Int
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
            hasBorder = false // Vì thẻ mẫu không có viền
        )


        binding.ivThumbnail.setImageResource(data.thumbnailRes)
        binding.ivAuthorAvatar.setImageResource(data.authorAvatarRes)


        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }


        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
}