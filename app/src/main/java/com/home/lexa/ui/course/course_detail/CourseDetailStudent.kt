package com.home.lexa.ui.course.course_detail

import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.databinding.ActivityMainBinding
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.Popup
import com.home.lexa.ui.components.StudentSpeakingDayCard

class CourseDetailStudent(
    private val fragment: CourseDetailFragment,
    private val binding: FragmentCourseDetailBinding,
    private val viewModel: CourseDetailViewModel,
    private val activity: ActivityMainBinding
): CourseDetailHandler {
    override fun setupViews() {
        binding.learningBtn.apply {
            setTextSize(20f)
            setText("Tiếp tục học ngay", ContextCompat.getColor(fragment.requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.purple_paragraph))
        }
    }

    override fun bindCourseData(course: SpeakingCourseDetailDto) {
        binding.titleCourse.text = course.title
        binding.topic.apply {
            setTextSize(12f)
            setText(course.type!!, ContextCompat.getColor(fragment.requireContext(), android.R.color.white))
            setBackground( android.graphics.Color.parseColor(course.typeColor))
        }
        binding.introduction.text = course.description ?: ""
    }

    override fun bindSpeakingData(course: SpeakingCourseDetailDto) {
        course.list_speaking_day.forEachIndexed {index, day ->
            val dayCard = StudentSpeakingDayCard(fragment.requireContext()).apply {
                    setData(
                        _day = index + 1,
                        _title = day.title,
                        _progressPercent = day.completed
                    )
            }

            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 32) // Khoảng cách dưới 32px (hoặc dùng dp)
            }
            dayCard.layoutParams = params
            binding.speakingDayLayout.addView(dayCard)
        }
    }

    override fun bindFlashcardData(item: DetailFlashcard, card: FlashcardMini) {
        val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
            width = 0
            height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
            setMargins(16, 16, 16, 16)
        }
        card.layoutParams = params
        binding.vocabularyGrid.addView(card)
    }

    override fun observerViewModel() {
    }
}