package com.home.lexa.ui.course.course_detail

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.databinding.ActivityMainBinding
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.CourseDetailDto
import com.home.lexa.domain.models.PartOfSpeech
import com.home.lexa.domain.models.ShortSpeakingDayDto
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.Popup
import com.home.lexa.ui.components.StudentSpeakingDayCard
import com.home.lexa.ui.components.ToggleSwitch

class CourseDetailStudent(
    private val fragment: CourseDetailFragment,
    private val binding: FragmentCourseDetailBinding,
    private val viewModel: CourseDetailViewModel,
    private val activityBinding: ActivityMainBinding
): CourseDetailHandler {
    override fun setupViews() {
        binding.learningBtn.apply {
            setTextSize(20f)
            setText("Tiếp tục học ngay", ContextCompat.getColor(fragment.requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.purple_paragraph))
        }
    }

    override fun bindCourseData(course: CourseDetailDto) {
        android.util.Log.e("DEBUG_FAVORITE", "Course ID: ${course.id}, is_favorite: ${course.is_favorite}")
        var isFavorite = course.is_favorite ?: false
        activityBinding.appBarLayout.apply {
            setIconRightButton(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_selector_favorite_btn)!!)
            setRightButtonSelected(!isFavorite)
            setOnClickToggleRightButton { isActivated ->
                if(isActivated){
                    viewModel.removeFavorite(course.id, course.deckId!!)
                } else {
                    viewModel.setFavorite(course.id, course.deckId!!)
                }
            }
        }
        binding.topic.apply {
            setTextSize(12f)
            setText(course.type!!, ContextCompat.getColor(fragment.requireContext(), android.R.color.white))
            setBackground( android.graphics.Color.parseColor(course.typeColor))
        }
        binding.titleCourse.text = course.title
        binding.introduction.text = course.description ?: ""
    }

    override fun bindSpeakingData(courseId: Long, list: List<ShortSpeakingDayDto>) {
        binding.speakingDayLayout.removeAllViews()

        binding.learningBtn.setOnClickAction {

            if (list.isNotEmpty()) {
                var targetDayIndex = list.indexOfFirst { it.completed < 100 }

                targetDayIndex = if (targetDayIndex != -1) targetDayIndex else 0

                val bundle = bundleOf(
                    "courseId" to courseId,
                    "speakingDayId" to list[targetDayIndex].speakingDayId,
                    "order" to targetDayIndex
                )

                fragment.findNavController().navigate(
                    R.id.action_courseDetailFragment_to_speakingPracticeStudentFragment,
                    bundle
                )
            } else {
                Toast.makeText(fragment.requireContext(), "Chưa có bài giảng nào!", Toast.LENGTH_SHORT).show()
            }
        }

        list.forEachIndexed {index, day ->
            val dayCard = StudentSpeakingDayCard(fragment.requireContext()).apply {
                    setData(
                        _day = index + 1,
                        _title = day.title,
                        _progressPercent = day.completed
                    )
                setOnClickAction {
                    val bundle = bundleOf(
                        "courseId" to courseId,
                        "speakingDayId" to day.speakingDayId,
                        "order" to index
                    )
                    fragment.findNavController().navigate(
                        R.id.action_courseDetailFragment_to_speakingPracticeStudentFragment,
                        bundle
                    )
                }
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

    override fun bindFlashcardData(flashcards:  List<DetailFlashcard>) {

        flashcards.forEach { item ->
            val card = FlashcardMini(fragment.requireContext())
            val posEnum = PartOfSpeech.fromId(item.partOfSpeechId)
            val vocab = Vocabulary(
                level = ColorLabel(item.type, "#E0E0E5"),
                imageUrl = item.imageUrl,
                word = item.word,
                pronunciation_url = item.audioUrl ?: "",
                transciption = item.transcription,
                part_of_speech = ColorLabel(fragment.getString(posEnum?.nameRes ?:R.string.pos_none ), "#636AE8"),
                definition = item.meaning,
                example = item.example ?: ""
            )
            card.setData(vocab)

            val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = androidx.gridlayout.widget.GridLayout.spec(
                    androidx.gridlayout.widget.GridLayout.UNDEFINED,
                    1f
                )
                setMargins(16, 16, 16, 16)
            }
            card.layoutParams = params
            binding.vocabularyGrid.addView(card)
        }
    }

    override fun observerViewModel() {
        viewModel.favoriteStatus.observe(fragment.viewLifecycleOwner){
                result ->
            result?.onSuccess {
                Toast.makeText(fragment.requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetFavoriteStatus()
            }?.onFailure {
                Toast.makeText(fragment.requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetFavoriteStatus()
            }
        }
    }
}
