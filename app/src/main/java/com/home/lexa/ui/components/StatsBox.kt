package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.home.lexa.databinding.ViewStatsBoxBinding

class StatsBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = ViewStatsBoxBinding.inflate(LayoutInflater.from(context), this)

    init {
        radius = 24f
        cardElevation = 0f // Trong hình ko có bóng đổ rõ
        setCardBackgroundColor(Color.WHITE)
        strokeWidth = 1
        strokeColor = Color.parseColor("#F0F0F0") // Viền siêu nhạt
    }

    fun setCardData(iconRes: Int, count: Int, title: String) {
        binding.ivIcon.setImageResource(iconRes)
        binding.tvCount.text = count.toString()
        binding.tvTitle.text = title
    }

    // Hàm quan trọng để tuỳ chỉnh màu như trong hình
    fun setIconStyle(tintColorHex: String, bgColorHex: String) {
        binding.ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(tintColorHex))
        binding.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColorHex))
    }
}