package com.home.lexa.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.home.lexa.R
import com.home.lexa.databinding.CardParagraphEditBinding
import com.home.lexa.databinding.CardStudentSpeakingDayBinding
import com.home.lexa.databinding.CardTeacherSpeakingDayBinding
import com.home.lexa.databinding.ViewTagBinding


class ParagraphEditCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardParagraphEditBinding.inflate(LayoutInflater.from(context), this, true)
    private var order = 1
    private var paragraph = "Chào hỏi cơ bản & Giới thiệu bản thân"
    fun setData(_order: Int, _paragraph: String) {
        setDay(_order)
        setTitle(_paragraph)
    }

    fun setDay(_order: Int) {
        this.order = _order
        binding.tvDay.setText("PARAGRAPH ${order}")
    }

    fun setTitle(_paragraph: String) {
        this.paragraph = _paragraph
        binding.tvTitle.text = paragraph
    }
}