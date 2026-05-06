package com.home.lexa.ui.course.course_detail

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.R
import com.home.lexa.core.Constants
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
    private fun navigateToSpeakingPractice(
        courseId: Long,
        speakingDayId: Long,
        order: Int,
        forceStartOver: Boolean = false,
        skipResumeDialog: Boolean = false
    ) {
        val bundle = bundleOf(
            "courseId" to courseId,
            "speakingDayId" to speakingDayId,
            "order" to order,
            "forceStartOver" to forceStartOver,
            "skipResumeDialog" to skipResumeDialog
        )
        fragment.findNavController().navigate(
            R.id.action_courseDetailFragment_to_speakingPracticeStudentFragment,
            bundle
        )
    }

    private fun navigateToDailyResult(courseId: Long, speakingDayId: Long) {
        val bundle = bundleOf(
            "courseId" to courseId,
            "speakingDayId" to speakingDayId,
            "fromCompletedDay" to true
        )
        fragment.findNavController().navigate(
            R.id.action_courseDetailFragment_to_dailyResultFragment,
            bundle
        )
    }

    private fun showResumeDialog(
        day: ShortSpeakingDayDto,
        courseId: Long,
        order: Int
    ) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.continue_lesson_title))
            .setMessage(fragment.getString(R.string.continue_lesson_msg))
            .setPositiveButton(fragment.getString(R.string.continue_action)) { _, _ ->
                navigateToSpeakingPractice(courseId, day.speakingDayId, order, false, true)
            }
            .setNegativeButton(fragment.getString(R.string.start_over)) { _, _ ->
                navigateToSpeakingPractice(courseId, day.speakingDayId, order, true, true)
            }
            .show()
    }

    override fun setupViews() {
        binding.learningBtn.apply {
            setTextSize(20f)
            setText(fragment.getString(R.string.continue_learning_now), ContextCompat.getColor(fragment.requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.purple_paragraph))
        }
    }

    override fun bindCourseData(course: CourseDetailDto) {
        val isFavorite = course.is_favorite == true

        activityBinding.appBarLayout.apply {
            setIconRightButton(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_selector_favorite_btn)!!)
            setRightButtonSelected(isFavorite)
            setOnClickToggleRightButton { isActivated ->
                if(isActivated){
                    viewModel.setFavorite(course.id, course.deckId!!)
                } else {
                    viewModel.removeFavorite(course.id, course.deckId!!)
                }
            }
        }

        binding.circleFavorite.apply {
            setIconResource(if (isFavorite) R.drawable.ic_favorite_btn else R.drawable.ic_favorite_border_btn)
            setIconTint(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(fragment.requireContext(), if (isFavorite) R.color.red else R.color.purple_paragraph)
            ))
            setOnClickListener {
                if (isFavorite) {
                    viewModel.removeFavorite(course.id, course.deckId!!)
                } else {
                    viewModel.setFavorite(course.id, course.deckId!!)
                }
            }
        }
        binding.backgroundCourse.load(course.thumbnail_url) {
            crossfade(true)
            placeholder(R.drawable.background_course)
            error(R.drawable.background_course)
        }
        binding.topic.apply {
            setTextSize(12f)
            setText(course.type!!, ContextCompat.getColor(fragment.requireContext(), android.R.color.white))
            setBackground( android.graphics.Color.parseColor(course.typeColor))
        }
        binding.titleCourse.text = course.title
        binding.introduction.text = course.description ?: ""

        // Sửa: Chỉ dùng URL và error URL từ Constants
        binding.backgroundCourse.load(if (course.thumbnail_url.isNullOrBlank()) Constants.DEFAULT_COURSE_IMAGE_URL else course.thumbnail_url) {
            crossfade(true)
//            error(Constants.DEFAULT_COURSE_IMAGE_URL)
        }
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
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_lessons_available), Toast.LENGTH_SHORT).show()
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
                    when {
                        day.completed >= 100 -> {
                            navigateToDailyResult(courseId, day.speakingDayId)
                        }
                        day.completed > 0 -> {
                            showResumeDialog(day, courseId, index)
                        }
                        else -> {
                            navigateToSpeakingPractice(courseId, day.speakingDayId, index, false)
                        }
                    }
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
        viewModel.favoriteStatus.observe(fragment.viewLifecycleOwner) { result ->
            if (result == null) return@observe

            result.onSuccess {
                val isCurrentlyFavorite = viewModel.courseDetailData.value?.is_favorite == true
                val messageRes = if (isCurrentlyFavorite) R.string.unfavorite_success else R.string.favorite_success

                Toast.makeText(fragment.requireContext(), fragment.getString(messageRes), Toast.LENGTH_SHORT).show()

                viewModel.loadCourseDetail(viewModel.courseDetailData.value?.id ?: -1L)
                viewModel.resetFavoriteStatus()
            }.onFailure {
                Toast.makeText(fragment.requireContext(), fragment.getString(R.string.error_message, it.message), Toast.LENGTH_SHORT).show()
                viewModel.resetFavoriteStatus()
            }
        }
    }
}
