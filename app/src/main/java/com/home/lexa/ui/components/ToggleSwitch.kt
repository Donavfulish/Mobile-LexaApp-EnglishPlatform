package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ButtonToggleSwitchBinding

class ToggleSwitch  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    private val binding =
        ButtonToggleSwitchBinding.inflate(LayoutInflater.from(context), this);

    var isChecked: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            updateUI(animate = isAttachedToWindow)
            onCheckedChangeListener?.invoke(value)
        }

    var onCheckedChangeListener: ((Boolean) -> Unit)? = null

    init {
        updateUI(animate = false)

        setOnClickListener{
            isChecked = !isChecked
        }
    }

    private fun updateUI(animate: Boolean) {
        binding.switchTrack.isSelected = isChecked

        binding.root.post {
            val trackWidth = binding.switchTrack.width
            val thumbWidth = binding.switchThumb.width

            if (trackWidth == 0) return@post

            val translationX = if (isChecked) {
                (trackWidth - thumbWidth - 8).toFloat()
            } else {
                0f
            }

            if (animate) {
                binding.switchThumb.animate()
                    .translationX(translationX)
                    .setDuration(200)
                    .start()
            } else {
                binding.switchThumb.translationX = translationX
            }
        }
    }
}
