package com.home.lexa.ui.components

import android.content.Context
import android.graphics.PorterDuff
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.core.widget.doOnTextChanged
import com.home.lexa.databinding.InputNormalBinding
import com.home.lexa.databinding.ButtonLexaBinding

// Kế thừa FrameLayout để bọc component XML lại
class NormalInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = InputNormalBinding.inflate(LayoutInflater.from(context), this, true)

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        // Chặn không cho các view con (EditText) tự lưu theo ID chung của tụi nó
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        // Chặn không cho các view con tự khôi phục theo ID chung
        dispatchThawSelfOnly(container)
    }

    // Set giá trị label và hiển`    thị Label trên ô Input
    fun setLabel(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvLabel.visibility = View.GONE
        } else {
            binding.tvLabel.text = text
            binding.tvLabel.visibility = View.VISIBLE
        }
    }

    // Cài đặt nội dung hint
    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return

        binding.etInput.hint = text
    }

    // Cài đặt icon đầu ô Input (resId: id của drawable resource, colorHex: "#123456")
    fun setIcon(resId: Int?, colorHex: String? = "#575E6B") {
        if (resId == null || resId == -1) {
            // Nếu không có icon hoặc id không hợp lệ, ẩn ImageView đi để EditText tràn ra
            binding.ivIcon.visibility = View.GONE
        } else {
            binding.ivIcon.setImageResource(resId)

            binding.ivIcon.setColorFilter(colorHex!!.toColorInt(), PorterDuff.Mode.SRC_IN)

            binding.ivIcon.visibility = View.VISIBLE
        }
    }

    fun getText(): String = binding.etInput.text.toString()

    fun setText(text: String?) {
        binding.etInput.setText(text)
    }

    // Giúp ViewModel lắng nghe được sự thay đổi của text
    fun onTextChanged(action: (String) -> Unit) {
        binding.etInput.doOnTextChanged { text, start, before, count ->
            action(text.toString())
        }
    }

    fun setInputHeight(heightInDp: Int) {
        val params = binding.containerInput.layoutParams
        // Chuyển từ Dp sang Pixel
        params.height = (heightInDp * context.resources.displayMetrics.density).toInt()
        binding.containerInput.layoutParams = params

        // Nếu chiều cao lớn, cho chữ lên phía trên cho đẹp
        if (heightInDp > 60) {
            binding.etInput.gravity = Gravity.TOP
        }
    }

    fun setEnable(isEnable: Boolean) {
        binding.etInput.isEnabled = isEnable
    }

    // Hàm nhận function (callback) xử lý click
    /*fun setOnLexaClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener {
            onClick.invoke()
        }
    }*/
}