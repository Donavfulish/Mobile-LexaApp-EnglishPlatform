package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.home.lexa.databinding.ComponentOtpBarBinding

class Auth_OTPBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentOtpBarBinding =
        ComponentOtpBarBinding.inflate(LayoutInflater.from(context), this)

    init {
        // Khi click vào vùng đệm xung quanh PinView
        setOnClickListener {
            focusAndShowKeyboard()
        }
    }

    fun focusAndShowKeyboard() {
        binding.pinview.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Dùng toggle nếu show không được, nhưng showSoftInput là chuẩn nhất
        imm.showSoftInput(binding.pinview, InputMethodManager.SHOW_IMPLICIT)
    }

    fun getOtp(): String = binding.pinview.text?.toString() ?: ""

    fun clearOtp() {
        binding.pinview.setText("")
    }
}