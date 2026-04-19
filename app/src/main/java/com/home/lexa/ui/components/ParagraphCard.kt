package com.home.lexa.ui.components

import android.content.Context
import android.text.Spannable
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

    fun displayParagraph(paragraph: ParagraphResult) {
        val textView = binding.content
        binding.title.setText("PARAGRAPH ${paragraph.order}")
        val builder = SpannableStringBuilder()

        paragraph.paragraph.forEach { item ->
            val start = builder.length
            builder.append(item.w).append(" ")
            val end = builder.length - 1

            // Ánh xạ status sang màu sắc
            val color = when (item.s) {
                "green" -> R.color.green
                "yellow" -> R.color.yellow_paragraph
                "red" -> R.color.red_paragraph
                else -> android.R.color.black
            }
            val actualColor = ContextCompat.getColor(context, color)

            builder.setSpan(
                ForegroundColorSpan(actualColor),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = builder
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
        binding.rightBtn.setOnClickListener { action.invoke() }
    }

}