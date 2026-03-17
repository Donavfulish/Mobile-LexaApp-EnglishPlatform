package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.home.lexa.databinding.FlashcardBinding
import com.home.lexa.databinding.StudentSpeakingDayCardBinding

class StudentSpeakingDayCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = StudentSpeakingDayCardBinding.inflate(LayoutInflater.from(context), this, true)
    private var day = 1
    private var title = "Chào hỏi cơ bản & Giới thiệu bản thân"
    private var progressPercent = 80

    fun setData(_day: Int, _title: String, _progressPercent: Int) {
        setDay(_day)
        setTitle(_title)
        setProgress(_progressPercent)
    }

    fun setDay(_day: Int) {
        this.day = _day
        binding.tvDayLabel.setTagData("Ngày " + day.toString(), "#636AE8", true)
    }

    fun setTitle(_title: String) {
        this.title = _title
        binding.tvTitle.text = title
    }

    fun setProgress(_progressPercent: Int) {
        if (_progressPercent > 100) return

        this.progressPercent = _progressPercent
        binding.progressBar.setProgress(progressPercent)

        if (progressPercent == 100) {
            binding.ivCheck.visibility = View.VISIBLE
        }
    }

}