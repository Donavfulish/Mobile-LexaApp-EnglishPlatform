package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.home.lexa.R
import com.home.lexa.databinding.ViewStatsBoxBinding

class StatsBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = ViewStatsBoxBinding.inflate(LayoutInflater.from(context), this)

    init {
        radius = 24f
        cardElevation = 0f // Trong hình ko có bóng đổ rõ
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.c_ffffff))
        strokeWidth = 1
        strokeColor = ContextCompat.getColor(context, R.color.c_f0f0f0) // Viền siêu nhạt
    }

    fun setCardData(iconRes: Int, count: Int, title: String) {
        binding.ivIcon.setImageResource(iconRes)
        binding.tvCount.text = count.toString()
        binding.tvTitle.text = title
    }

    // Hàm quan trọng để tuỳ chỉnh màu như trong hình
    fun setIconStyle(@ColorRes tintColorRes: Int, @ColorRes bgColorRes: Int) {
        binding.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColorRes))
        binding.ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, bgColorRes))
    }
}