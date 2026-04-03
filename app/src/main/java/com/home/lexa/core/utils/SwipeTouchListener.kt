package com.home.lexa.core.utils // Nhớ sửa lại package name cho đúng với project của bạn

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeTouchListener(
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit
) : View.OnTouchListener {

    private var dX = 0f
    private var startX = 0f
    private var startY = 0f

    // Ngưỡng di chuyển tối đa để được coi là một cú Click (10 pixels)
    private val CLICK_DRAG_TOLERANCE = 10f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                startX = event.rawX
                startY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val newX = event.rawX + dX
                view.animate()
                    .x(newX)
                    .rotation((newX - view.context.resources.displayMetrics.widthPixels / 2) / 20f)
                    .setDuration(0)
                    .start()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.rawX
                val endY = event.rawY
                val distanceX = endX - startX
                val distanceY = endY - startY
                val screenWidth = view.context.resources.displayMetrics.widthPixels

                // 1. NẾU LÀ CLICK: Tay nhấc lên mà di chuyển chưa tới 10px
                if (abs(distanceX) < CLICK_DRAG_TOLERANCE && abs(distanceY) < CLICK_DRAG_TOLERANCE) {
                    view.performClick() // Ép view kích hoạt sự kiện OnClick (chính là flipCard)

                    // Reset lại vị trí thẻ (đề phòng tay hơi run làm thẻ bị lệch 1-2px)
                    view.animate().x(0f).rotation(0f).setDuration(100).start()
                }
                // 2. NẾU LÀ VUỐT: Kiểm tra xem đã qua ngưỡng 1/3 màn hình chưa
                else if (distanceX > screenWidth / 3) {
                    swipeOut(view, screenWidth.toFloat(), onSwipeRight) // Vuốt phải
                } else if (distanceX < -(screenWidth / 3)) {
                    swipeOut(view, -screenWidth.toFloat(), onSwipeLeft) // Vuốt trái
                } else {
                    // 3. VUỐT CHƯA ĐỦ LỰC: Bật ngược trở lại giữa
                    view.animate().x(0f).rotation(0f).setDuration(300).start()
                }
                return true
            }
        }
        return false
    }

    private fun swipeOut(view: View, targetX: Float, onComplete: () -> Unit) {
        view.animate()
            .x(targetX)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                onComplete()
                // Reset view cho thẻ tiếp theo
                view.x = 0f
                view.rotation = 0f
                view.alpha = 1f
            }
            .start()
    }
}