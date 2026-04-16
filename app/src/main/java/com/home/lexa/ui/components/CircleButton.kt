package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.home.lexa.databinding.ButtonCircleBinding

class CircleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ButtonCircleBinding.inflate(LayoutInflater.from(context), this, true)

    fun setSize(sizeDp: Int){
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        binding.circleBtn.layoutParams.width = sizePx
        binding.circleBtn.layoutParams.height = sizePx
        binding.circleBtn.requestLayout()
    }

    fun setIcon(icon: Drawable){
        binding.circleBtn.icon = icon
    }

    fun setIconSize(sizeDp: Int){
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        binding.circleBtn.iconSize = sizePx
    }

    fun setIconTint(@ColorInt color: Int) {
        binding.circleBtn.iconTint = ColorStateList.valueOf(color)
    }

    fun setBackground(@ColorInt color: Int){
        binding.circleBtn.backgroundTintList = ColorStateList.valueOf(color)
    }


    fun setOnClickAction(action: () -> Unit) {
        binding.circleBtn.setOnClickListener {
            action.invoke()
        }
    }
}