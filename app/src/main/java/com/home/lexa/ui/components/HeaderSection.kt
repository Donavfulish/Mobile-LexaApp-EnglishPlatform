package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ViewHeaderSectionBinding

class HeaderSection @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewHeaderSectionBinding.inflate(LayoutInflater.from(context), this)

    /**
     * @param title: Tiêu đề hiển thị (VD: "Khóa học nổi bật")
     * @param iconRes: ID của Icon (VD: R.drawable.ic_star). Truyền null nếu không muốn hiện Icon.
     * @param actionText: Chữ của nút bên phải (Mặc định là "Xem tất cả")
     * @param onActionClick: Hàm callback xử lý khi người dùng bấm vào nút
     */
    fun setHeaderData(
        title: String,
        iconRes: Int? = null,
        actionText: String = "Xem tất cả",
        onActionClick: (() -> Unit)? = null
    ) {

        binding.tvTitle.text = title


        if (iconRes != null) {
            binding.ivIcon.setImageResource(iconRes)
            binding.ivIcon.visibility = View.VISIBLE
        } else {
            binding.ivIcon.visibility = View.GONE
        }

        if (onActionClick != null) {
            binding.tvAction.text = actionText
            binding.tvAction.visibility = View.VISIBLE
            binding.tvAction.setOnClickListener {
                onActionClick.invoke()
            }
        } else {
            binding.tvAction.visibility = View.GONE
        }
    }
}