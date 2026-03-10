package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.home.lexa.databinding.ViewStatsBoxBinding

class StatsBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = ViewStatsBoxBinding.inflate(LayoutInflater.from(context), this)

    init {

        radius = 32f
        cardElevation = 12f
        setCardBackgroundColor(android.graphics.Color.WHITE)
        strokeWidth = 0

    }

    /**
     * Hàm truyền tham số vào Component
     * @param iconRes: ID của icon trong thư mục drawable (VD: R.drawable.ic_book)
     * @param count: Số lượng hiển thị
     * @param title: Tiêu đề bên dưới
     */
    fun setCardData(iconRes: Int, count: Int, title: String) {
        binding.ivIcon.setImageResource(iconRes)
        binding.tvCount.text = count.toString()
        binding.tvTitle.text = title
    }
}