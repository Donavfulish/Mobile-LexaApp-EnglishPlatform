package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.R
import com.home.lexa.databinding.CardSettingMenuItemBinding

class SettingMenuItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CardSettingMenuItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // Layout params để chiếm full chiều ngang khi được ném vào LinearLayout
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingMenuItem)

        val iconRes = typedArray.getResourceId(R.styleable.SettingMenuItem_menuIcon, 0)
        if (iconRes != 0) {
            binding.ivMenuIcon.setImageResource(iconRes)
        } else {
            binding.ivMenuIcon.visibility = View.GONE
        }

        binding.tvMenuTitle.text = typedArray.getString(R.styleable.SettingMenuItem_menuTitle)

        val valueText = typedArray.getString(R.styleable.SettingMenuItem_menuValue)
        binding.tvMenuValue.text = valueText

        val showValue = typedArray.getBoolean(R.styleable.SettingMenuItem_showValue, true)
        binding.tvMenuValue.visibility = if (showValue && !valueText.isNullOrEmpty()) View.VISIBLE else View.GONE

        typedArray.recycle()
    }

    fun setMenuValue(value: String) {
        binding.tvMenuValue.text = value
        binding.tvMenuValue.visibility = if (value.isNotEmpty()) View.VISIBLE else View.GONE
    }
}