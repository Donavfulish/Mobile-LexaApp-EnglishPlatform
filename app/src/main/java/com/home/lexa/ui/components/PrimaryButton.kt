package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.databinding.ButtonPrimaryBinding

class PrimaryButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ButtonPrimaryBinding.inflate(LayoutInflater.from(context), this, true)
    private var originalText: String = ""

    private val defaultBackgroudColor: Int = ContextCompat.getColor(context, R.color.btn_primary_bg)
    private val disabledBackgroundColor: Int = ContextCompat.getColor(context, R.color.btn_primary_disabled_bg)
    private var textColor: Int? = null
    private var backgroundColor: Int = defaultBackgroudColor

    fun setText(text: String, @ColorInt color: Int? = null) {
        originalText = text
        binding.primaryBtn.text = text
        color?.let {
            binding.primaryBtn.setTextColor(color)
            textColor = color
        }
    }

    fun setTextSize(size: Float) {
        binding.primaryBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    fun setBackground(@ColorInt color: Int) {
        binding.primaryBtn.backgroundTintList = ColorStateList.valueOf(color)
        backgroundColor = color
    }

    fun setTextColor(@ColorInt color: Int) {
        binding.primaryBtn.setTextColor(color)
    }

    fun setWidth(widthDp: Int) {
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.width = widthPx
    }

    fun setHeight(heightDp: Int) {
        val heightPx = (heightDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.height = heightPx
    }

    fun setStroke(widthDp: Int, @ColorInt color: Int?) {
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.strokeWidth = widthPx
        color?.let {
            binding.primaryBtn.strokeColor = ColorStateList.valueOf(color)
        }
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.primaryBtn.setOnClickListener {
            action.invoke()
        }
    }

    fun setLoading(isLoading: Boolean, loadingText: String = "Đang xử lý...") {
        binding.primaryBtn.isEnabled = !isLoading
        if (isLoading) {
            if (originalText.isEmpty()) originalText = binding.primaryBtn.text.toString()
            binding.primaryBtn.text = loadingText
        } else {
            binding.primaryBtn.text = originalText
        }
    }

    fun setEnabledState(isEnabled: Boolean) {
        binding.primaryBtn.isEnabled = isEnabled
        if (isEnabled) {
            // Khôi phục về màu chính (activeColor)
            binding.primaryBtn.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        } else {
            // Chuyển sang màu xám
            binding.primaryBtn.backgroundTintList = ColorStateList.valueOf(disabledBackgroundColor)
        }
    }
}