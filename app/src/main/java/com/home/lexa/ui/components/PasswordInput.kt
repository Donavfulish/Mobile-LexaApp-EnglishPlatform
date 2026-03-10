package com.home.lexa.ui.components

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.core.widget.doOnTextChanged
import com.home.lexa.R
import com.home.lexa.databinding.NormalInputBinding
import com.home.lexa.databinding.PasswordInputBinding
import com.home.lexa.databinding.ViewLexaButtonBinding

// Kế thừa FrameLayout để bọc component XML lại
class PasswordInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = PasswordInputBinding.inflate(LayoutInflater.from(context), this, true)
    private var _isPasswordHidden = false

    init {
        binding.etInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        setupPasswordToggle()
    }

    private fun setupPasswordToggle() {
        binding.ivShowPassword.setOnClickListener {
            _isPasswordHidden = !_isPasswordHidden

            if (_isPasswordHidden) {
                // 1. Đổi icon sang "Mắt mở"
                binding.ivShowPassword.setImageResource(R.drawable.close_eye)
                // 2. Hiện mật khẩu
                binding.etInput.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
            } else {
                // 1. Đổi icon sang "Mắt đóng"
                binding.ivShowPassword.setImageResource(R.drawable.open_eye)
                // 2. Ẩn mật khẩu (dùng PasswordTransformationMethod)
                binding.etInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }

            // Di chuyển con trỏ về cuối text để không bị nhảy khi đổi mode
            binding.etInput.setSelection(binding.etInput.text.length)
        }
    }

    // Set giá trị label và hiển thị Label trên ô Input
    fun setLabel(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvLabel.visibility = GONE
        } else {
            binding.tvLabel.text = text
            binding.tvLabel.visibility = VISIBLE
        }
    }

    // Cài đặt nội dung hint
    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return

        binding.etInput.hint = text
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

    // Hàm nhận function (callback) xử lý click
    /*fun setOnLexaClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener {
            onClick.invoke()
        }
    }*/
}