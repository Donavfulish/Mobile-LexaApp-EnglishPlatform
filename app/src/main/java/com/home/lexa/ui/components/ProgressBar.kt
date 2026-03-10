package com.home.lexa.ui.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ViewProgressBarBinding

class ProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewProgressBarBinding.inflate(LayoutInflater.from(context), this)

    fun setProgress(percentage: Int) {
        val validPercentage = percentage.coerceIn(0, 100)


        binding.tvPercentage.text = "$validPercentage%"

        val animation = ObjectAnimator.ofInt(
            binding.linearProgressBar,
            "progress",
            binding.linearProgressBar.progress,
            validPercentage
        )
        animation.duration = 800 // 0.8 giây
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }


    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }
}