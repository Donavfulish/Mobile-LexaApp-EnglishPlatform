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

class ParagraphCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = CardParagraphBinding.inflate(LayoutInflater.from(context), this, true)

    init{
        binding.AIButton.setText("AI evaluated", null)
        binding.AIButton.setTextSize(10f)

        binding.leftBtn.setText("Nghe phát âm chuẩn", context.getColor(R.color.purple_500))
        binding.leftBtn.setStroke(1, context.getColor(R.color.purple_500))
        binding.leftBtn.setTextSize(12f)
        binding.leftBtn.setBackground(context.getColor(R.color.white))
        binding.leftBtn.setIcon(context.getDrawable(R.drawable.ic_play_circle))
        binding.leftBtn.setIconPadding(2)


        binding.rightBtn.setText("Nghe lại âm của tôi", context.getColor(R.color.purple_200))
        binding.rightBtn.setStroke(1, context.getColor(R.color.purple_200))
        binding.rightBtn.setTextSize(12f)
        binding.rightBtn.setBackground(context.getColor(R.color.white))
        binding.rightBtn.setIcon(context.getDrawable(R.drawable.ic_replay))
        binding.rightBtn.setIconPadding(2)
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
                "green" -> android.R.color.holo_green_dark
                "yellow" -> android.R.color.holo_orange_dark
                "red" -> android.R.color.holo_red_dark
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
        binding.leftBtn.setOnClickListener { action.invoke() }
    }

    fun setOnClickUserSound(action: () -> Unit){
        binding.rightBtn.setOnClickListener { action.invoke() }
    }

}