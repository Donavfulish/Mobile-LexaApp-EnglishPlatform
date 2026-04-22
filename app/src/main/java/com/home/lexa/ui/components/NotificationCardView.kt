package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.home.lexa.R
import com.home.lexa.databinding.CardBoxNotiBinding

class NotificationCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding: CardBoxNotiBinding

    private val ivIcon: ImageView
    private val tvTitle: TextView
    private val tvDescription: TextView

    private var onToggleChangeListener: ((Boolean) -> Unit)? = null

    init {
        binding = CardBoxNotiBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        ivIcon = binding.ivIcon
        tvTitle = binding.tvTitle
        tvDescription = binding.tvDescription

        // 🎨 Config card
        cardElevation = 0f
        strokeWidth = 1
        strokeColor = ContextCompat.getColor(context, R.color.c_e0e0e0)

        // 🎯 Toggle event
        binding.toggleMode.onCheckedChangeListener = { isChecked ->
            onToggleChangeListener?.invoke(isChecked)
        }
    }


    fun setOnToggleChangeListener(listener: (Boolean) -> Unit) {
        onToggleChangeListener = listener
    }

    fun setIcon(@DrawableRes resId: Int, tintColor: Int? = null) {
        ivIcon.setImageResource(resId)
        if (tintColor != null) {
            ivIcon.setColorFilter(tintColor)
        } else {
            ivIcon.clearColorFilter()
        }
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun setDescription(description: String?) {
        if (description.isNullOrEmpty()) {
            tvDescription.visibility = View.GONE
        } else {
            tvDescription.visibility = View.VISIBLE
            tvDescription.text = description
        }
    }

    fun setToggleState(isOn: Boolean) {
        binding.toggleMode.isChecked = isOn
    }
    fun setIconBackgroundTint(@ColorRes colorResId: Int) {
        ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
    }
}