package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.home.lexa.R
import androidx.core.content.ContextCompat
import com.home.lexa.databinding.ViewIconButtonBinding
import com.home.lexa.databinding.ViewPrimaryButtonBinding

class IconButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ViewIconButtonBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(text: String,@ColorInt color: Int?) {
        binding.iconBtn.text = text
        color?.let {
            binding.iconBtn.setTextColor(color)
        }
    }

    fun setIcon(icon: Drawable?){
        icon?.let{
            binding.iconBtn.icon = icon
            binding.iconBtn.iconTint = null
        }
    }

    fun setIconSize(sizeDp: Int){
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        binding.iconBtn.iconSize = sizeDp
    }

    fun setIconPadding(paddingDp: Int){
        val paddingPx = (paddingDp * context.resources.displayMetrics.density).toInt()
        binding.iconBtn.iconPadding = paddingPx
    }

    fun setTextSize(size: Float){
        binding.iconBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    fun setBackground(@ColorInt color: Int){
        binding.iconBtn.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun setWidth(widthDp: Int){
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.iconBtn.width = widthPx
    }

    fun setHeight(heightDp: Int){
        val heightPx = (heightDp * context.resources.displayMetrics.density).toInt()
        binding.iconBtn.height = heightPx
    }

    fun setStroke(widthDp: Int, @ColorInt color: Int?){
        val widthPx = (widthDp * context.resources.displayMetrics.density).toInt()
        binding.iconBtn.strokeWidth = widthPx
        color?.let{
            binding.iconBtn.strokeColor = ColorStateList.valueOf(color)
        }
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.iconBtn.setOnClickListener {
            action.invoke()
        }
    }
}