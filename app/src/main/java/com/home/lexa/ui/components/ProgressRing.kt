package com.home.lexa.ui.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ViewProgressRingBinding


class ProgressRing @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewProgressRingBinding.inflate(LayoutInflater.from(context), this, true)


    fun setProgress(percentage: Int) {
        val validPercentage = percentage.coerceIn(0, 100)
        binding.tvPercentage.text = validPercentage.toString()

        val animation = ObjectAnimator.ofInt(binding.customProgressBar, "progress", 0, validPercentage)
        animation.duration = 1000
        animation.start()
    }
}