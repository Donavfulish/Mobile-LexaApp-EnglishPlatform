package com.home.lexa.ui.components

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.home.lexa.R
import com.home.lexa.databinding.FlashcardBinding
import com.home.lexa.databinding.FlashcardMiniBinding
import com.home.lexa.databinding.LayoutFlashcardZoomBinding
import com.home.lexa.databinding.ViewLexaButtonBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.domain.models.mockVocabularyData

// Kế thừa FrameLayout để bọc component XML lại
class FlashcardMini @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScalableContainer(context, attrs) {
    private val binding = FlashcardMiniBinding.inflate(LayoutInflater.from(context), this)
    private var isFront = true
    private var isAnimating = false
    private var data: Vocabulary = mockVocabularyData
    private var isEditable = false

    init {
        setData(mockVocabularyData)

        //scale = 0.5f
        // Cho phép UI vượt ngoài phạm vi
        clipChildren = false
        clipToPadding = false

        // Thiết lập khoảng cách camera để hiệu ứng 3D không bị "văng" vào mặt người dùng
        val distance = 10000
        val scale = resources.displayMetrics.density * distance
        binding.layoutFront.root.cameraDistance = scale
        binding.layoutBack.root.cameraDistance = scale

        binding.layoutFront.btnZoom.setOnClickListener { zoom() }
        binding.layoutFront.btnEdit.setOnClickListener { onClickEdit() }
        binding.layoutFront.btnDelete.setOnClickListener { onClickDelete() }
        binding.layoutBack.btnSound.setOnClickListener { pronounce() }
        setOnClickListener { flipCard() }
    }

    fun setIsEditable(_isEditable: Boolean = true) {
        isEditable = _isEditable
        if (isEditable) {
            binding.layoutFront.btnZoom.visibility = View.GONE
            binding.layoutFront.btnEdit.visibility = View.VISIBLE
            binding.layoutFront.btnDelete.visibility = View.VISIBLE
        } else {
            binding.layoutFront.btnZoom.visibility = View.VISIBLE
            binding.layoutFront.btnEdit.visibility = View.GONE
            binding.layoutFront.btnDelete.visibility = View.GONE
        }

    }

    fun flipFront() { if (!isFront) flipCard() }
    fun flipBack() { if (isFront) flipCard() }

    // Bắt sự kiến bấm nút Zoom
    fun zoom() {
        val dialog = android.app.Dialog(context)
        val dialogBinding = LayoutFlashcardZoomBinding.inflate(LayoutInflater.from(context), null, false)

        // Tìm trực tiếp từ XML
        val bigCard = dialogBinding.bigCard
        val btnClose = dialogBinding.btnClose

        // Đổ dữ liệu vào
        bigCard.setData(this.data)

        // Xử lý nút đóng
        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        bigCard.setOnClickListener {
            bigCard.flipCard() // Phải bắt sự kiện này để ngăn thoát khi bấm flashcard
        }

        dialog.setContentView(dialogBinding.root)

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Bắt sự kiện bấm nút Edit
    fun onClickEdit() {
        // TODO chuyển tới trang Edit Flashcard
    }

    // Bắt sự kiện bấm nút Delete
    fun onClickDelete() {
        // TODO hiện confirmBox xóa flashcard
    }

    // Tác vụ phát âm khi bấm nút
    fun pronounce() {
        if (data.pronunciation_url.isEmpty()) return
        // TODO Phát âm từ data.pronunciation_url
    }

    // Đổ dữ liệu Vocabulary vào UI
    fun setData(_data: Vocabulary) {
        this.data = _data
        // Gán mặt trước (thường là từ vựng)
        binding.layoutFront.apply {
            tvLevel.apply {
                text = data.level.label
                setVocabularyStyle(data.level.colorString)
            }

            tvWord.text = data.word
            tvTranscription.text = data.transciption
        }

        // Gán mặt sau (định nghĩa, ví dụ)
        binding.layoutBack.apply {
            tvLevel.apply {
                text = data.level.label
                setVocabularyStyle(data.level.colorString)
            }

            tvWord.text = data.word
            tvTranscription.text = data.transciption

            tvPartOfSpeech.apply {
                text = data.part_of_speech.label
                setVocabularyStyle(data.part_of_speech.colorString)
            }

            tvDefinition.text = data.definition
        }
    }

    // Hiệu ứng lật thẻ 3D (tham khảo Gemini)
    fun flipCard() {
        if (isAnimating) return
        isAnimating = true

        val visibleView = if (isFront) binding.layoutFront.root else binding.layoutBack.root
        val invisibleView = if (isFront) binding.layoutBack.root else binding.layoutFront.root

        // 1. Thiết lập khoảng cách camera (cần thiết để không bị biến dạng 3D quá mức)
        val distance = 8000 * resources.displayMetrics.density
        visibleView.cameraDistance = distance
        invisibleView.cameraDistance = distance

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float

            // Công thức hình Sin để scale đạt 0.9f đúng ở giữa (0.5) và về 1.0f ở hai đầu
            val currentScale = 1f - (0.1f * Math.sin(progress * Math.PI.toDouble()).toFloat())

            if (progress <= 0.5f) {
                // --- Giai đoạn 1: Thu nhỏ và xoay mặt cũ đi ---
                val rotation = progress * 2 * 90f
                visibleView.rotationY = if (isFront) rotation else -rotation

                // Áp dụng scale trực tiếp lên view con như code tham khảo của bạn
                visibleView.scaleX = currentScale
                visibleView.scaleY = currentScale

                visibleView.alpha = 1f - (progress * 2)
                invisibleView.visibility = View.GONE
            } else {
                // --- Giai đoạn 2: Đổi mặt và phóng to mặt mới về 1.0 ---
                visibleView.visibility = View.GONE
                invisibleView.visibility = View.VISIBLE

                val rotation = (progress - 0.5f) * 2 * 90f - 90f
                invisibleView.rotationY = if (isFront) rotation else -rotation

                // Áp dụng scale cho mặt mới
                invisibleView.scaleX = currentScale
                invisibleView.scaleY = currentScale

                invisibleView.alpha = (progress - 0.5f) * 2
            }
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Reset trạng thái view cũ để lần lật sau không bị lỗi
                visibleView.scaleX = 1f
                visibleView.scaleY = 1f
                visibleView.rotationY = 0f

                isFront = !isFront
                isAnimating = false
            }
        })

        animator.start()
    }
}