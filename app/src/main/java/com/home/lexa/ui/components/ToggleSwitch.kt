package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ComponentToggleSwitchBinding

class ToggleSwitch  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    private val binding =
        ComponentToggleSwitchBinding.inflate(LayoutInflater.from(context), this);

    var isChecked: Boolean = false
        set(value) {
            field = value
            updateUI(animate = true)
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

        var translationX = if (isChecked) {
            (binding.switchTrack.width - binding.switchThumb.width - 8).toFloat()
        } else {
            0f
        }

        if (animate) {
            binding.switchThumb.animate()
                .translationX(translationX)
                .setDuration(200)
                .start()
        } else {
            binding.switchThumb.post {
                binding.switchThumb.translationX = if (isChecked) 70f else 0f // Giá trị tạm tính
            }
        }
    }
}