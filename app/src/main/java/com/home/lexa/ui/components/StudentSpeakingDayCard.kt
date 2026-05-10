package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.home.lexa.R
import com.home.lexa.databinding.CardStudentSpeakingDayBinding

class StudentSpeakingDayCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = CardStudentSpeakingDayBinding.inflate(LayoutInflater.from(context), this, true)
    private var day = 1
    private var title = ""
    private var progressPercent = 0

    fun setData(_day: Int, _title: String, _progressPercent: Int) {
        setDay(_day)
        setTitle(_title)
        setProgress(_progressPercent)
    }

    fun setDay(_day: Int) {
        this.day = _day
        binding.tvDayLabel.setTagData(context.getString(R.string.day, day.toString()), "@color/brand_primary", true)
    }

    fun setTitle(_title: String) {
        this.title = _title
        binding.tvTitle.text = title
    }

    fun setProgress(_progressPercent: Int) {
        progressPercent = _progressPercent.coerceIn(0, 100)
        binding.progressBar.setProgress(progressPercent)

        if (progressPercent == 100) {
            binding.ivCheck.visibility = View.VISIBLE
        } else {
            binding.ivCheck.visibility = View.GONE
        }
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.root.setOnClickListener {
            action()
        }
    }

}
