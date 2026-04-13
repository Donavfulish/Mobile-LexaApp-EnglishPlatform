package com.home.lexa.ui.components

import android.content.Context
import android.graphics.PorterDuff
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.graphics.toColorInt
import androidx.core.widget.doOnTextChanged
import com.home.lexa.databinding.InputNormalBinding

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

    init {
        setMultipleLines(false)
    }
    fun setLabel(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvLabel.visibility = View.GONE
        } else {
            binding.tvLabel.text = text
            binding.tvLabel.visibility = View.VISIBLE
        }
    }

    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return
        binding.etInput.hint = text
    }

    fun setIcon(@DrawableRes resId: Int?, colorHex: String? = "#575E6B") {
        if (resId == null || resId == -1) {
            binding.ivIcon.visibility = View.GONE
        } else {
            binding.ivIcon.setImageResource(resId)

            binding.ivIcon.setColorFilter(colorHex!!.toColorInt(), PorterDuff.Mode.SRC_IN)

            binding.ivIcon.visibility = View.VISIBLE
        }
    }

    fun setEndIcon(@DrawableRes resId: Int?, colorHex: String? = "#575E6B") {
        if (resId == null || resId == -1) {
            binding.ivEndIcon.visibility = View.GONE
        } else {
            binding.ivEndIcon.setImageResource(resId)
            binding.ivEndIcon.setColorFilter(colorHex!!.toColorInt(), PorterDuff.Mode.SRC_IN)
            binding.ivEndIcon.visibility = View.VISIBLE
        }
    }

    fun setOnEndIconClickListener(onClick: () -> Unit) {
        binding.ivEndIcon.setOnClickListener {
            onClick.invoke()
        }
    }

    fun setOnInputClickListener(onClick: () -> Unit) {
        binding.etInput.isFocusable = false
        binding.etInput.isClickable = true
        binding.etInput.setOnClickListener { onClick() }
        binding.containerInput.setOnClickListener { onClick() }
    }

    fun getText(): String = binding.etInput.text.toString()

    fun setText(text: String?) {
        binding.etInput.setText(text)
    }

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
            binding.etInput.gravity = Gravity.TOP or Gravity.START
        }
    }

    fun setEnable(isEnable: Boolean) {
        binding.etInput.isEnabled = isEnable
    }
    fun setHorizontalScroll() {
            binding.etInput.isSingleLine = true
            binding.etInput.setHorizontallyScrolling(true)
            binding.etInput.maxLines = 1
    }
    fun setMaxLength(maxLength: Int) {

        val currentFilters = binding.etInput.filters
        val newFilters = currentFilters.toMutableList()

        newFilters.removeAll { it is InputFilter.LengthFilter }
        newFilters.add(InputFilter.LengthFilter(maxLength))

        binding.etInput.filters = newFilters.toTypedArray()
    }
    fun setMultipleLines(isMultiLine: Boolean) {
        binding.etInput.apply {
            if (isMultiLine) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                isSingleLine = false

                maxLines = 5

                setHorizontallyScrolling(false)

                setGravity(Gravity.TOP or Gravity.START)

                imeOptions = EditorInfo.IME_ACTION_NONE
            } else {
                inputType = InputType.TYPE_CLASS_TEXT
                isSingleLine = true

                maxLines = 1

                setHorizontallyScrolling(true)

                gravity = Gravity.CENTER_VERTICAL

                imeOptions = EditorInfo.IME_ACTION_DONE
            }

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