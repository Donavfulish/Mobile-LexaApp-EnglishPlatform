package com.home.lexa.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.home.lexa.R
import com.home.lexa.databinding.InputDropdownBinding
import com.home.lexa.ui.utils.ColorTokenUtils

class DropDownInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
    private val binding = InputDropdownBinding.inflate(LayoutInflater.from(context), this)

    var onItemSelected: ((String) -> Unit) ? = null

    init {
        orientation = VERTICAL

        val typedArray = context.obtainStyledAttributes(attrs, com.home.lexa.R.styleable.DropdownInput)
        val title = typedArray.getString(com.home.lexa.R.styleable.DropdownInput_dropdownTitle)
        binding.tvTitle.text = title
        typedArray.recycle()
    }

    fun setTile(title: String) {
        binding.tvTitle.text = title
    }

    fun setSelection(value: String) {
        binding.tvSelectedValue.text = value
    }

    fun setUpOptions(options: List<String>) {
        binding.containerDropdown.setOnClickListener{
            val popup = PopupMenu(context, binding.containerDropdown)
            options.forEach{popup.menu.add(it)}

            popup.setOnMenuItemClickListener { item->
                val selected = item.title.toString()
                binding.tvSelectedValue.text = selected
                onItemSelected?.invoke(selected)
                true
            }
            popup.show()
        }
    }
    fun getSelection(): String {
        return binding.tvSelectedValue.text.toString()
    }

    fun setFrameColor(colorHex: String, hasBorder: Boolean = false) {
        setFrameColorInt(ColorTokenUtils.resolve(context, colorHex), hasBorder)
    }

    fun setFrameColor(@ColorRes colorRes: Int, hasBorder: Boolean = false) {
        setFrameColorInt(ContextCompat.getColor(context, colorRes), hasBorder)
    }

    private fun setFrameColorInt(@ColorInt baseColor: Int, hasBorder: Boolean) {
        val backgroundColor = ColorUtils.setAlphaComponent(baseColor, 38)

        binding.tvSelectedValue.setTextColor(baseColor)
        binding.ivArrow.setColorFilter(baseColor)

        val drawable = ContextCompat.getDrawable(context, R.drawable.bg_tag)?.mutate() as GradientDrawable
        drawable.setColor(backgroundColor)
        if (hasBorder) {
            drawable.setStroke(3, baseColor)
        } else {
            drawable.setStroke(0, Color.TRANSPARENT)
        }

        binding.containerDropdown.background = drawable
    }
}