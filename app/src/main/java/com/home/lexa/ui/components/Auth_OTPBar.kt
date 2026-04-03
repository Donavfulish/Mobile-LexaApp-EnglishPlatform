package com.home.lexa.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.home.lexa.databinding.InputOtpBarBinding
import com.patrykandpatrick.vico.core.DefaultColors.Dark.lineColor

class Auth_OTPBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: InputOtpBarBinding =
        InputOtpBarBinding.inflate(LayoutInflater.from(context), this)

    // Callback để báo cho Fragment/Activity khi nhập 6 số
    var onOtpCompletionListener: ((String) -> Unit)? = null

    private var lastCompletedOtp: String = ""

    init {
        setupOtpListener()

        // Khi click vào vùng đệm xung quanh PinView
        setOnClickListener {
            focusAndShowKeyboard()
        }
    }

    private fun setupOtpListener() {
        binding.pinview.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                val otp = s?.toString() ?: ""

                if (otp.length == 6 && otp != lastCompletedOtp) {
                    lastCompletedOtp = otp
                    onOtpCompletionListener?.invoke(otp)

                    // Optional: Tự động ẩn bàn phím khi xong
                    // hideKeyboard()
                } else if (otp.length < 6) {
                    lastCompletedOtp = ""
                }
            }
        })
    }

    fun focusAndShowKeyboard() {
        binding.pinview.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Dùng toggle nếu show không được, nhưng showSoftInput là chuẩn nhất
        imm.showSoftInput(binding.pinview, InputMethodManager.SHOW_IMPLICIT)
    }

    fun setInputEnabled(enabled: Boolean) {
        binding.pinview.apply {
            isFocusable = enabled
            isFocusableInTouchMode = enabled
            isCursorVisible = enabled
            alpha = 1.0f

            if (enabled) {
                focusAndShowKeyboard()
            } else {
                clearFocus()
            }
        }
    }

    fun getOtp(): String = binding.pinview.text?.toString() ?: ""

    fun clearOtp() {
        binding.pinview.setText("")
    }

    fun showError() {
        val errorColor = Color.parseColor("#FF0000")
        binding.pinview.apply {
            setLineColor(errorColor)
            // Lắc nhẹ máy để báo lỗi (User sẽ hiểu là sai mà không cần nhìn màu xám)
            animate().translationX(10f).setDuration(50).setInterpolator(android.view.animation.CycleInterpolator(3f)).start()
            setItemBackgroundColor(Color.WHITE)
        }
    }

    fun clearError() {
        val defaultColor = Color.parseColor("#BDBDBD")
        binding.pinview.apply {
            // Trả lại màu gốc trong XML của bạn
            setLineColor(defaultColor)

            // Nếu bạn đang disable thì nhớ bật lại
            alpha = 1.0f
        }
    }
}