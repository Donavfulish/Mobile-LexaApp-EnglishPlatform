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
import com.home.lexa.databinding.ViewCircleButtonBinding
import com.home.lexa.databinding.ViewIconButtonBinding
import com.home.lexa.databinding.ViewPrimaryButtonBinding

class CircleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ViewCircleButtonBinding.inflate(LayoutInflater.from(context), this, true)

    fun setSize(sizeDp: Int){
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        binding.circleBtn.width = sizePx
        binding.circleBtn.height = sizePx
    }

    fun setIcon(icon: Drawable){
        binding.circleBtn.icon = icon
    }

    fun setIconSize(sizeDp: Int){
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        binding.circleBtn.iconSize = sizeDp
    }

    fun setBackground(@ColorInt color: Int){
        binding.circleBtn.backgroundTintList = ColorStateList.valueOf(color)
    }


    fun setOnPrimaryButtonClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener {
            onClick.invoke()
        }
    }
}