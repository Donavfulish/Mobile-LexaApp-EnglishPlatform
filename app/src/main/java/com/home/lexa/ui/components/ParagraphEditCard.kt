package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.home.lexa.databinding.CardParagraphEditBinding

class ParagraphEditCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = CardParagraphEditBinding.inflate(LayoutInflater.from(context), this, true)

    private var startX = 0f
    private val swipeThreshold = 100f
    private val actionButtonWidth = 200f // Độ rộng của nút (pixel)

    fun setData(_order: Int, _paragraph: String) {
        binding.tvDay.text = "PARAGRAPH " + String.format("%02d", _order)
        binding.tvTitle.text = _paragraph
    }

    /**
     * Khởi tạo chức năng vuốt sang trái (Xóa) và vuốt sang phải (Sửa)
     */
    fun initSwipe() {
        binding.foregroundContent.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val diffX = startX - endX

                    if (diffX > swipeThreshold) {
                        // VUỐT SANG TRÁI -> Hiện nút XÓA
                        binding.foregroundContent.animate()
                            .translationX(-actionButtonWidth)
                            .setDuration(200)
                            .start()
                    } else if (diffX < -swipeThreshold) {
                        // VUỐT SANG PHẢI -> Hiện nút SỬA
                        binding.foregroundContent.animate()
                            .translationX(actionButtonWidth)
                            .setDuration(200)
                            .start()
                    } else {
                        // Đóng lại nếu vuốt không đủ mạnh
                        binding.foregroundContent.animate()
                            .translationX(0f)
                            .setDuration(200)
                            .start()
                    }

                    if (Math.abs(diffX) < 10) v.performClick()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Bắt sự kiện Touch riêng cho icon kéo thả (6 chấm)
     * Lưu ý: Đảm bảo icon 6 chấm trong XML có ID là ic_drag
     */
    fun setOnDragHandleTouchListener(listener: OnTouchListener) {
        // Thay binding.icDrag bằng ID thật của icon 6 chấm trong file card_paragraph_edit.xml của bạn
        binding.ivDragHandle.setOnTouchListener(listener)
    }

    /**
     * Sự kiện khi ấn vào nút SỬA (bên trái)
     */
    fun setOnEditClickListener(onEdit: () -> Unit) {
        binding.layoutEdit.setOnClickListener {
            binding.foregroundContent.animate().translationX(0f).setDuration(100).withEndAction {
                onEdit()
            }.start()
        }
    }

    /**
     * Sự kiện khi ấn vào nút XÓA (bên phải)
     */
    fun setOnDeleteClickListener(onDelete: () -> Unit) {
        binding.layoutDelete.setOnClickListener {
            binding.foregroundContent.animate().translationX(0f).setDuration(100).withEndAction {
                onDelete()
            }.start()
        }
    }
}