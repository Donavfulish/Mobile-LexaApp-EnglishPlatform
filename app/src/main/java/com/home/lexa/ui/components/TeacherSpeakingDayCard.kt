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
import com.home.lexa.databinding.StudentSpeakingDayCardBinding
import com.home.lexa.databinding.TeacherSpeakingDayCardBinding
import com.home.lexa.databinding.ViewTagBinding


class TeacherSpeakingDayCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = TeacherSpeakingDayCardBinding.inflate(LayoutInflater.from(context), this, true)
    private var day = 1
    private var title = "Chào hỏi cơ bản & Giới thiệu bản thân"
    private var paragraphNum = 15

    fun setData(_day: Int, _title: String, _paragraphNum: Int) {
        setDay(_day)
        setTitle(_title)
        setParagraph(_paragraphNum)
    }

    fun setDay(_day: Int) {
        this.day = _day
        binding.tvDay.setTagData("Ngày " + day.toString(), "#636AE8", true)
    }

    fun setTitle(_title: String) {
        this.title = _title
        binding.tvTitle.text = title
    }

    fun setParagraph(_number: Int) {
        this.paragraphNum = _number
        binding.tvParagraphNum.text = "$paragraphNum đoạn văn"
    }
}