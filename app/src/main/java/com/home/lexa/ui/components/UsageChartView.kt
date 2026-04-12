package com.home.lexa.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class UsageChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f) // 7 days (T2 -> CN)
    private val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6200EE") // Màu tím chủ đạo của app
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F0F0F0")
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val yAxisLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = 24f
        textAlign = Paint.Align.LEFT
    }

    private val barRect = RectF()
    private val paddingBottom = 60f
    private val paddingLeft = 80f // Thêm padding bên trái để hiện số phút
    private val barWidthPercent = 0.5f 

    fun setData(newData: List<Float>) {
        if (newData.size == 7) {
            this.data = newData
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val viewHeight = height.toFloat()
        val chartHeight = viewHeight - paddingBottom
        val chartWidth = width - paddingLeft
        
        val maxVal = data.maxOrNull() ?: 0f
        val displayMax = if (maxVal < 60f) 60f else (Math.ceil(maxVal / 60.0) * 60).toFloat() // Làm tròn lên bội số của 60

        val dayWidth = chartWidth / 7

        // 1. Vẽ các đường kẻ ngang (Grid lines) và số phút (Y-axis labels)
        for (i in 0..4) {
            val ratio = i / 4f
            val y = chartHeight * (1 - ratio)
            
            // Vẽ đường lưới
            canvas.drawLine(paddingLeft, y, width, y, gridPaint)
            
            // Vẽ số phút ở trục tung
            val minutes = (displayMax * ratio).toInt()
            canvas.drawText("${minutes}m", 10f, y + 10f, yAxisLabelPaint)
        }

        // 2. Vẽ cột và nhãn ngày
        data.forEachIndexed { index, value ->
            val centerX = paddingLeft + (dayWidth * index) + (dayWidth / 2)
            
            val barHeight = (value / displayMax) * chartHeight
            val left = centerX - (dayWidth * barWidthPercent / 2)
            val right = centerX + (dayWidth * barWidthPercent / 2)
            val top = chartHeight - barHeight
            val bottom = chartHeight

            barRect.set(left, top, right, bottom)
            
            canvas.drawRoundRect(barRect, 12f, 12f, barPaint)

            canvas.drawText(dayLabels[index], centerX, viewHeight - 10f, labelPaint)
        }
        
        canvas.drawLine(paddingLeft, chartHeight, width, chartHeight, gridPaint)
    }
}