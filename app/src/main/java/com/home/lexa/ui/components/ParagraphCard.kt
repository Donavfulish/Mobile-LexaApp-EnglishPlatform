package com.home.lexa.ui.components

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.databinding.CardParagraphBinding
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.ui.utils.TTSManager

class ParagraphCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = CardParagraphBinding.inflate(LayoutInflater.from(context), this, true)

    init{
        binding.AIButton.setText("AI evaluated", null)
        binding.AIButton.setTextSize(10f)

        binding.leftBtn.setText("Nghe phát âm chuẩn", context.getColor(R.color.purple_paragraph))
        binding.leftBtn.setStroke(1, context.getColor(R.color.purple_paragraph))
        binding.leftBtn.setTextSize(12f)
        binding.leftBtn.setBackground(context.getColor(R.color.white))
        binding.leftBtn.setIcon(context.getDrawable(R.drawable.ic_play_circle))
        binding.leftBtn.setIconPadding(2)
        binding.leftBtn.setOnClickAction {
            TTSManager.speak(binding.content.text.toString())
        }


        binding.rightBtn.setText("Nghe lại âm của tôi", context.getColor(R.color.pink))
        binding.rightBtn.setStroke(1, context.getColor(R.color.pink))
        binding.rightBtn.setTextSize(12f)
        binding.rightBtn.setBackground(context.getColor(R.color.white))
        binding.rightBtn.setIcon(context.getDrawable(R.drawable.ic_replay))
        binding.rightBtn.setIconPadding(2)

        setOnClickAISound {  }
    }

    fun setOnClickAIButton(action: () -> Unit){
        binding.AIButton.setOnClickListener {
            action.invoke()
        }
    }

    fun displayParagraph(paragraphResult: ParagraphResult) {
        val textView = binding.content
        binding.title.setText("PARAGRAPH ${paragraphResult.order}")
//        val builder = SpannableStringBuilder()
//
//        paragraphResult.paragraph.forEach { item ->
//            val start = builder.length
//            builder.append(item.w).append(" ")
//            val end = builder.length - 1
//
//            // Ánh xạ status sang màu sắc
//            val color = when (item.s) {
//                "green" -> R.color.green
//                "yellow" -> R.color.yellow_paragraph
//                "red" -> R.color.red_paragraph
//                else -> android.R.color.black
//            }
//            val actualColor = ContextCompat.getColor(context, color)
//
//            builder.setSpan(
//                ForegroundColorSpan(actualColor),
//                start,
//                end,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//
//        textView.text = builder

        val tvTitle = binding.title
        val tvContent = binding.content

        tvTitle.text = "PARAGRAPH ${paragraphResult.order}"

        val originalText = paragraphResult.original
        if (originalText.isEmpty()) return

        val spannable = SpannableString(originalText)

        // 1. Định nghĩa màu sắc
        val colorSuccess = ContextCompat.getColor(binding.root.context, R.color.status_success)
        val colorWarning = ContextCompat.getColor(binding.root.context, R.color.status_warning)
        val colorError = ContextCompat.getColor(binding.root.context, R.color.status_error_alt)

        // 2. Tô màu mặc định (xanh) cho toàn bộ văn bản gốc trước khi xử lý từ
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(binding.root.context, R.color.status_success)),
            0, originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 3. Regex lấy các từ trong văn bản gốc (để biết vị trí chính xác của chúng trong chuỗi original)
        val wordRegex = Regex("[a-zA-Z0-9-'’]+")
        val originalWords = wordRegex.findAll(originalText).toList()

        // 4. Thuật toán con trỏ: Dò danh sách kết quả chấm điểm (paragraph) với các từ gốc
        var pointerOriginal = 0

        paragraphResult.paragraph.forEach { evalItem ->
            // Làm sạch từ từ API/Result (xóa ký tự đặc biệt để so sánh)
            val evalClean = evalItem.w.lowercase()
                .replace('’', '\'')
                .replace(Regex("[^a-zA-Z0-9-']"), "")

            if (evalClean.isEmpty()) return@forEach

            // Tìm kiếm tuần tự trong các từ gốc chưa được duyệt
            for (i in pointerOriginal until originalWords.size) {
                val originalMatch = originalWords[i]
                val originalClean = originalMatch.value.lowercase()
                    .replace('’', '\'')
                    .replace(Regex("[^a-zA-Z0-9-']"), "")

                if (evalClean == originalClean) {
                    // ĐÃ KHỚP! Xác định màu dựa trên thuộc tính 's' của ParagraphWord
                    val correctColor = when (evalItem.s) {
                        "green" -> colorSuccess
                        "yellow" -> colorWarning
                        "red" -> colorError
                        else -> colorSuccess
                    }

                    // Tô màu lên đúng vị trí của từ đó trong văn bản gốc (giữ nguyên dấu câu xung quanh)
                    spannable.setSpan(
                        ForegroundColorSpan(correctColor),
                        originalMatch.range.first,
                        originalMatch.range.last + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Cập nhật con trỏ để không xét lại các từ đã qua
                    pointerOriginal = i + 1
                    break
                }
            }
        }

        // 5. Cập nhật lên giao diện
        tvContent.text = spannable
    }



    fun setTitle(title: String){
        binding.title.setText(title)
    }

    fun setOnClickAISound(action: () -> Unit){
        binding.leftBtn.setOnClickAction {
            TTSManager.speak(binding.content.text.toString())
        }
    }

    fun setOnClickUserSound(action: () -> Unit){
        binding.rightBtn.setOnClickAction { action.invoke() }
    }

}