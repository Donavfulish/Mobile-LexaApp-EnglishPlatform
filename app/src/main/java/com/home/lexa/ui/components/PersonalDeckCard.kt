package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.CardPersonalDeckBinding
import com.home.lexa.domain.models.DeckDto


//data class StudySetData(
//    val title: String,
//    val wordCountText: String,
//    val timeText: String,
//    val iconRes: Int,
//    val tagTitle: String,
//    val tagColorHex: String
//)

class PersonalDeckCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val binding = CardPersonalDeckBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Hàm nạp dữ liệu vào component
     * @param data: Dữ liệu của bài học
     * @param onItemClick: Xử lý khi bấm vào toàn bộ thẻ (để vào học)
     * @param onOptionsClick: Xử lý khi bấm vào nút 3 chấm (để sửa/xóa)
     */
    fun setDeckCardData(
        data: DeckDto,
        onItemClick: () -> Unit,
        onOptionsClick: () -> Unit = {}
    ) {

        binding.tvTitle.text = data.title
        binding.tvWordCount.text = "${data.vocabNumber ?: 0} từ"
        binding.tvTime.text = data.createdAt
        //binding.ivMainIcon.setImageResource(data.iconRes)
        binding.tagCategory.setTagData(
            text = data.topic?.name ?: "non-topic",
            colorHex = data.topic?.colorHex ?: "#636AE8",
            hasBorder = false
        )
        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }

        binding.root.setOnClickListener {
            onItemClick.invoke()
        }
    }
}