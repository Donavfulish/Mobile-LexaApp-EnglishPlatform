package com.home.lexa.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.home.lexa.R
import com.home.lexa.databinding.ViewTagBinding
import com.home.lexa.ui.utils.ColorTokenUtils


class Tag @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewTagBinding.inflate(LayoutInflater.from(context), this)

    fun setTagData(text: String, colorHex: String, hasBorder: Boolean = false) {

        val baseColor = ColorTokenUtils.resolve(context, colorHex)
        val backgroundColor = ColorUtils.setAlphaComponent(baseColor, 38)

        binding.tvTagName.text = text
        binding.tvTagName.setTextColor(baseColor)

        val drawable = ContextCompat.getDrawable(context, R.drawable.bg_tag)?.mutate() as GradientDrawable
        drawable.setColor(backgroundColor)
        if (hasBorder) {
            drawable.setStroke(3, baseColor)
        } else {
            drawable.setStroke(0, Color.TRANSPARENT)
        }

        binding.tvTagName.background = drawable
    }
}