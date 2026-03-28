package com.home.lexa.ui.course.course_detail



import android.util.Log
import android.view.View
import android.widget.Toast
import com.home.lexa.R
import androidx.core.content.ContextCompat
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.StudentSpeakingDayCard
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailFragment : BaseFragment<FragmentCourseDetailBinding>(FragmentCourseDetailBinding::inflate) {
    private val viewModel: CourseDetailViewModel by viewModel()
    private var isSpeakingMode = true
    override fun setupViews() {

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView();
        }
        activityBinding.appBarLayout.apply {
            setText("Chi tiết khoá học");
            setBackButtonVisible(true);
        }
        binding.searchBarVocabulary.apply {
            setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setTextSearch("Tìm kiếm từ vựng...")
        }
        binding.vocabularyIconBtn.apply {
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_book)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
        }

        binding.learningBtn.apply {
            setTextSize(20f)
            setText("Tiếp tục học ngay", ContextCompat.getColor(requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
        }

        binding.speakingBtn.apply{
            setTextSize(16f)
            setIconPadding(5)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_speaking))
            setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setText("Speaking", ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setBackground(ContextCompat.getColor(requireContext(), R.color.white))
        }

        binding.vocabularyBtn.apply{
            setTextSize(16f)
            setIconPadding(5)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_vocabulary))
            setText("Từ vựng", null)
            setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
        }

        binding.speakingBtn.setOnClickAction {
            if (!isSpeakingMode) {
                isSpeakingMode = true
                updateToggleUI()
                binding.vocabularyLayout.visibility = View.GONE
                binding.speakingLayout.visibility = View.VISIBLE
            }
        }

        binding.vocabularyBtn.setOnClickAction {
            if (isSpeakingMode) {
                isSpeakingMode = false
                updateToggleUI()
                binding.speakingLayout.visibility = View.GONE
                binding.vocabularyLayout.visibility = View.VISIBLE
            }
        }
        val courseId = 17L
        val cacheCourse: SpeakingCourseDetailDto? = AppMemoryCache.get("speakingCourseDetail_${courseId}")
        val cacheVocabulary: List<DetailFlashcard>? = AppMemoryCache.get("vocabularyList_${courseId}")
        if(cacheCourse != null){
            viewModel.setCourseAndFlashcard(cacheCourse, cacheVocabulary!!)
        } else {
            viewModel.loadCourseDetail(courseId)
        }

    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.contentScroll.visibility = View.GONE
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.contentScroll.visibility = View.VISIBLE
            }
        }

        viewModel.courseDetailData.observe(viewLifecycleOwner) { course ->
            if (course == null){
                Toast.makeText(requireContext(), "Không tìm thấy dữ liệu khóa học", Toast.LENGTH_SHORT).show()
                return@observe
            }
            // Khoa hoc
            binding.titleCourse.text = course.title
            binding.topic.apply {
                setTextSize(12f)
                setText(course.type!!, ContextCompat.getColor(requireContext(), android.R.color.white))
                setBackground( android.graphics.Color.parseColor(course.typeColor))
            }
            binding.introduction.text = course.description ?: ""

            // giao vien
            if(course.creator.image != null) {
                binding.imgTeacher.load(course.creator.image) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_teacher)
                    error(R.drawable.placeholder_teacher)       // Hiện ảnh này nếu link lỗi
                }
            }
            binding.teacherNameCourse.text = course.creator.name
            binding.studentNumCourse.text = course.studying_user_count.toString()
            binding.favoriteNumCourse.text = course.favorite_user_count.toString()

            binding.speakingNum.text = "${course.list_speaking_day.size} Bài học"
            binding.speakingDayLayout.removeAllViews()
            course.list_speaking_day.forEachIndexed {index, day ->
                val dayCard = StudentSpeakingDayCard(requireContext())
                dayCard.setData(
                    _day = index + 1,
                    _title = day.title,
                    _progressPercent = day.completed
                )
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

        viewModel.flashcardDetailData.observe(viewLifecycleOwner) { flashcards ->
            if (flashcards.isNullOrEmpty()) return@observe
            binding.flashcardNum.text = "${flashcards.size}"
            binding.vocabularyGrid.removeAllViews()
            flashcards.forEach { item ->
                val card = FlashcardMini(requireContext())
                val vocab = Vocabulary(
                    level = ColorLabel(item.type, "#E0E0E5"),
                    image = 0,
                    word = item.word,
                    pronunciation_url = item.audioUrl ?: "",
                    transciption = item.transcription,
                    part_of_speech = ColorLabel(item.partOfSpeech, "#636AE8"),
                    definition = item.meaning,
                    example = item.example ?: ""
                )
                card.setData(vocab)

                val params = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                    width = 0
                    height = androidx.gridlayout.widget.GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
                    setMargins(16, 16, 16, 16)
                }
                card.layoutParams = params
                binding.vocabularyGrid.addView(card)
            }
        }


    }

    private fun updateToggleUI() {
        if (isSpeakingMode) {
            binding.speakingBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setText("Speaking", ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setBackground(ContextCompat.getColor(requireContext(), R.color.white))
            }
            binding.vocabularyBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.black))
                setText("Từ vựng", ContextCompat.getColor(requireContext(), R.color.black))
                setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
            }
        } else {
            binding.speakingBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.black))
                setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
                setText("Speaking", ContextCompat.getColor(requireContext(), R.color.black))
            }
            binding.vocabularyBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setBackground(ContextCompat.getColor(requireContext(), R.color.white))
                setText("Từ vựng", ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            }
        }
    }
}