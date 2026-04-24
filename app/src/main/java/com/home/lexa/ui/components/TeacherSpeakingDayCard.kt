package com.home.lexa.ui.components
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.R
import com.home.lexa.databinding.CardTeacherSpeakingDayBinding

class TeacherSpeakingDayCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardTeacherSpeakingDayBinding.inflate(LayoutInflater.from(context), this, true)
    private var day = 1
    private var title = ""
    private var paragraphNum = 0

    fun setData(_day: Int, _title: String, _paragraphNum: Int) {
        setDay(_day)
        setTitle(_title)
        setParagraph(_paragraphNum)
    }

    fun setDay(_day: Int) {
        this.day = _day
        binding.tvDay.setTagData(context.getString(R.string.day, day.toString()), "#636AE8", true)
    }

    fun setTitle(_title: String) {
        this.title = _title
        binding.tvTitle.text = title
    }

    fun setParagraph(_number: Int) {
        this.paragraphNum = _number
        binding.tvParagraphNum.text = context.getString(R.string.paragraph_count, paragraphNum)
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.root.setOnClickListener {
            action()
        }
    }
}
