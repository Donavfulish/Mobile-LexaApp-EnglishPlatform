package com.home.lexa.ui.course.course_detail


import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.home.lexa.R
import androidx.core.content.ContextCompat
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import org.koin.android.ext.android.inject

import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailFragment : BaseFragment<FragmentCourseDetailBinding>(FragmentCourseDetailBinding::inflate) {
    private var handler: CourseDetailHandler? = null
        set(value) {
            if(value != null){
                // =====================================THONG TIN VIEW MODEL RIENG CUA TUNG ROLE=====================================
                value.observerViewModel()
            }
            field = value
        }
    private val viewModel: CourseDetailViewModel by viewModel()
    internal var isSpeakingMode = true
    internal var isOwner = true
    internal var isPublic = true
    internal var selectedTopicId: Int? = null
    internal var courseId = 17L
    internal lateinit var list_topic: List<Topic>
    private val activityBinding by lazy { (requireActivity() as MainActivity).binding }


    private val userManager: UserManager by inject()
    override fun onDestroyView() {
        super.onDestroyView()
        handler = null
    }
    override fun setupViews() {

        val courseId = arguments?.getLong("courseId") ?: -1L
        if (courseId == -1L) {
            viewModel.loadTopics()
        } else {
            viewModel.loadCourseDetail(courseId)
        }

        activityBinding.appBarLayout.apply {
            removeCustomView()
            setOnClickBack()
        }

        activityBinding.appBarLayout.apply {
            setText("Chi tiết khoá học");
            setBackButtonVisible(true);
        }


        // =============================================GENERAL SETUP==========================================
        binding.searchBarVocabulary.apply {
            setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setTextSearch("Tìm kiếm từ vựng...")
        }
        binding.vocabularyIconBtn.apply {
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_book)!!)
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
        syncTabUI()
    }

    override fun observeData() {

        // THEO DOI LOADING CHUNG
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.contentScroll.visibility = View.GONE
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.contentScroll.visibility = View.VISIBLE
                if(isOwner){
                    binding.addBtn.visibility = View.VISIBLE
                    binding.topLayoutTeacher.visibility = View.VISIBLE
                    binding.middleLayoutTeacher.visibility = View.VISIBLE
                    binding.bottomLayoutTeacher.visibility = View.VISIBLE

                    binding.learningBtn.visibility = View.GONE
                    binding.topLayoutStudent.visibility = View.GONE
                    binding.middleLayoutStudent.visibility = View.GONE
                    binding.bottomLayoutStudent.visibility = View.GONE
                } else {
                    binding.learningBtn.visibility = View.VISIBLE
                    binding.topLayoutStudent.visibility = View.VISIBLE
                    binding.middleLayoutStudent.visibility = View.VISIBLE
                    binding.bottomLayoutStudent.visibility = View.VISIBLE

                    binding.addBtn.visibility = View.GONE
                    binding.topLayoutTeacher.visibility = View.GONE
                    binding.middleLayoutTeacher.visibility = View.GONE
                    binding.bottomLayoutTeacher.visibility = View.GONE
                }
            }
        }

        // THEO DOI DS TOPIC (CHO VIỆC TẠO MỚI)
        viewModel.topicData.observe(viewLifecycleOwner) { topics ->
            list_topic = viewModel.topicData.value!!
            val list_topic_name = list_topic?.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_topic_name!!)
            binding.topicInput.setAdapter(adapter)
            handler = if(isOwner){
                CourseDetailTeacher(this, binding, viewModel, activityBinding)
            } else {
                CourseDetailStudent(this, binding, viewModel, activityBinding)
            }
            handler?.setupViews()
        }

        // THEO DOI TINH TRANG KHOA HOC TRA VE
        viewModel.courseDetailData.observe(viewLifecycleOwner) { course ->
            if (course == null){
                Toast.makeText(requireContext(), "Không tìm thấy dữ liệu khóa học", Toast.LENGTH_SHORT).show()
                return@observe
            }
            if(handler == null){
                isOwner = (course.creator.id == userManager.getUserId())
                handler = if(isOwner){
                    CourseDetailTeacher(this, binding, viewModel, activityBinding)
                } else {
                    CourseDetailStudent(this, binding, viewModel, activityBinding)
                }
                handler?.setupViews()
            }

            // =====================================THANH THONG TIN CHUNG=====================================
            if(course.creator.image != null) {
                binding.imgTeacher.load(course.creator.image) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_teacher)
                    error(R.drawable.placeholder_teacher)
                }
            }
            binding.teacherNameCourse.text = course.creator.name
            binding.studentNumCourse.text = course.studying_user_count.toString()
            binding.favoriteNumCourse.text = course.favorite_user_count.toString()
            binding.speakingNum.text = "${course.list_speaking_day.size} Bài học"
            binding.speakingDayLayout.removeAllViews()

            // =====================================THONG TIN HIEN THI KHOA HOC=====================================
            handler?.bindCourseData(course)

            // =====================================THONG TIN HIEN THI SPEAKING DAY=====================================
            handler?.bindSpeakingData(course)
        }

        // THEO DOI TINH TRANG FLASHCARD TRA VE
        viewModel.flashcardDetailData.observe(viewLifecycleOwner) { flashcards ->
            if (flashcards.isNullOrEmpty()) return@observe
            binding.flashcardNum.text = "${flashcards.size}"
            binding.vocabularyGrid.removeAllViews()
            binding.vocabularyGrid2.removeAllViews()
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
                handler?.bindFlashcardData(item, card)
            }
        }
    }

    internal fun updateToggleUI() {
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

    private fun syncTabUI() {
        updateToggleUI()
        if (isSpeakingMode) {
            binding.vocabularyLayout.visibility = View.GONE
            binding.speakingLayout.visibility = View.VISIBLE
        } else {
            binding.speakingLayout.visibility = View.GONE
            binding.vocabularyLayout.visibility = View.VISIBLE
        }
    }
}


