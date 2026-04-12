package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import com.home.lexa.R
import com.home.lexa.databinding.CardStudentCourseBinding
import com.home.lexa.domain.models.ShortCourseDto

data class CourseProgressData(
    val title: String,
    val description: String,
    val authorName: String,
    val userCount: Int,
    val heartCount: Int,
    val progressPercent: Int,
    val actionText: String = "HỌC NGAY",
    val tagTitle: String,
    val tagColorHex: String,
    val thumbnail: String,
    val authorAvatar: String
)
class StudentCourseCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardStudentCourseBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * @param data: Dữ liệu truyền vào
     * @param onActionClick: Hàm callback khi user bấm vào nút "HỌC NGAY"
     * @param onCardClick: Hàm callback khi user bấm vào phần thân thẻ
     * @param onOptionsClick: Hàm callback khi user bấm vào icon 3 chấm
     */
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

        binding.ivThumbnail.load(data.creator_avatar_url) {
            crossfade(true) // Hiệu ứng mờ dần khi ảnh tải xong cho đẹp
            placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị tạm trong lúc chờ tải
            error(R.drawable.ic_launcher_background) // Ảnh hiển thị nếu link bị lỗi/mất mạng
        }
        binding.ivAuthorAvatar.load(data.thumbnail_url) {
            crossfade(true) // Hiệu ứng mờ dần khi ảnh tải xong cho đẹp
            placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị tạm trong lúc chờ tải
            error(R.drawable.ic_launcher_background) // Ảnh hiển thị nếu link bị lỗi/mất mạng
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