package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import com.home.lexa.R
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.home.lexa.databinding.ViewPrimaryButtonBinding

class PrimaryButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ViewPrimaryButtonBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(text: String,@ColorInt color: Int?){
        binding.primaryBtn.text = text
        color?.let{
            binding.primaryBtn.setTextColor(color)
        }
    }

    fun setTextSize(size: Float){
        binding.primaryBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    fun setBackground(@ColorInt color: Int){
//        binding.primaryBtn.backgroundTintList = ColorStateList.valueOf(color)
        binding.primaryBtn.setBackgroundColor(color)
    }

    fun setWidth(widthDp: Int){
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.width = widthPx
    }

    fun setHeight(heightDp: Int){
        val heightPx = (heightDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.height = heightPx
    }

    fun setStroke(widthDp: Int, @ColorInt color: Int?){
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.primaryBtn.strokeWidth = widthPx
        color?.let{
            binding.primaryBtn.strokeColor = ColorStateList.valueOf(color)
        }
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.primaryBtn.setOnClickListener {
            action.invoke()
        }
    }
}