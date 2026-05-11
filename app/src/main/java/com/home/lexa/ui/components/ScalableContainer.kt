package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import coil.size.Scale

open class ScalableContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    // Thuộc tính scale để bạn chỉnh từ bên ngoài
    var scale: Float = 1.0f
        set(value) {
            field = value
            // Chúng ta không dùng this.scaleX/Y nữa để tránh lỗi layout trôi nổi
            invalidate() // Ép vẽ lại với tỉ lệ mới
        }

    init {
        // Rất quan trọng: Phải tắt clip để khi con xoay 3D không bị mất góc
        clipChildren = false
        clipToPadding = false
    }

    // Thủ thuật then chốt: Thay đổi cách vẽ toàn bộ con bên trong
    override fun dispatchDraw(canvas: android.graphics.Canvas) {
        canvas.save()
        // Thu nhỏ toàn bộ nội dung về tâm của View này
        canvas.scale(scale, scale, width / 2f, height / 2f)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    // Để bấm nút trúng, ta phải map ngược tọa độ chạm
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Tính toán tọa độ dựa trên tâm (Pivot Center)
        val pivotX = width / 2f
        val pivotY = height / 2f

        // Công thức dịch chuyển tọa độ chạm ngược lại với phép Scale
        ev.offsetLocation(-pivotX, -pivotY)
        ev.setLocation(ev.x / scale, ev.y / scale)
        ev.offsetLocation(pivotX, pivotY)

        return super.dispatchTouchEvent(ev)
    }
}