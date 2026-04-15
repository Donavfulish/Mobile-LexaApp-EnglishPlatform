package com.home.lexa.ui.components

import android.content.Context
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.home.lexa.R
import com.home.lexa.databinding.InputPasswordBinding

// Kế thừa FrameLayout để bọc component XML lại
class PasswordInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = InputPasswordBinding.inflate(LayoutInflater.from(context), this, true)
    private var _isPasswordHidden = false

    init {
        binding.etInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        setupPasswordToggle()
        setMultipleLines(false)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        // Chặn không cho các view con (EditText) tự lưu theo ID chung của tụi nó
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        // Chặn không cho các view con tự khôi phục theo ID chung
        dispatchThawSelfOnly(container)
    }

    private fun setupPasswordToggle() {
        binding.ivShowPassword.setOnClickListener {
            _isPasswordHidden = !_isPasswordHidden

            if (_isPasswordHidden)
                hidePassword()
            else
                showPassword()
        }
    }

    private fun hidePassword() {
        binding.ivShowPassword.setImageResource(R.drawable.ic_close_eye)
        binding.etInput.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
        binding.etInput.setSelection(binding.etInput.text.length)
    }

    private fun showPassword() {
        binding.ivShowPassword.setImageResource(R.drawable.ic_open_eye)
        binding.etInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        binding.etInput.setSelection(binding.etInput.text.length)
    }

    fun setLabel(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvLabel.visibility = GONE
        } else {
            binding.tvLabel.text = text
            binding.tvLabel.visibility = VISIBLE
        }
    }

    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return

        binding.etInput.hint = text
    }

    fun getText(): String = binding.etInput.text.toString()

    fun setText(text: String?) {
        binding.etInput.setText(text)
    }
    fun setHorizontalScroll() {
        binding.etInput.isSingleLine = true
        binding.etInput.setHorizontallyScrolling(true)
        binding.etInput.maxLines = 1
        if (_isPasswordHidden) {
            // Trạng thái đang hiện mật khẩu
            binding.etInput.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
        } else {
            // Trạng thái đang ẩn mật khẩu (mặc định)
            binding.etInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        }
    }

    fun onTextChanged(action: (String) -> Unit) {
        binding.etInput.doOnTextChanged { text, start, before, count ->
            action(text.toString())
        }
    }

    fun setMultipleLines(isMultiLine: Boolean) {
        binding.etInput.apply {
            if (isMultiLine) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                isSingleLine = false

                maxLines = 5

                setHorizontallyScrolling(false)

                gravity = Gravity.TOP

                imeOptions = EditorInfo.IME_ACTION_NONE
            } else {
                inputType = InputType.TYPE_CLASS_TEXT
                isSingleLine = true

                maxLines = 1

                setHorizontallyScrolling(true)

                gravity = Gravity.CENTER_VERTICAL

                imeOptions = EditorInfo.IME_ACTION_DONE
            }

            if (_isPasswordHidden)
                hidePassword()
            else
                showPassword()

            requestLayout()
        }
    }

    // Hàm nhận function (callback) xử lý click
    /*fun setOnLexaClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener {
            onClick.invoke()
        }
    }*/
}