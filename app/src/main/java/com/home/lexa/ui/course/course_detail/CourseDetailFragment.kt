package com.home.lexa.ui.course.course_detail


import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.home.lexa.R
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.EditCourseRequest
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
    internal var selectedTopicId =  0
    internal var courseId: Long = -1L
    internal lateinit var list_topic: List<Topic>
    private val activityBinding by lazy { (requireActivity() as MainActivity).binding }


    private val userManager: UserManager by inject()
    override fun onDestroyView() {
        super.onDestroyView()
        handler = null
    }
    override fun setupViews() {
        isOwner = true
        courseId = arguments?.getLong("courseId") ?: -1L
        if (courseId == -1L) {
            activityBinding.appBarLayout.apply {
                setText("Tạo mới khoá học");
                setBackButtonVisible(true)
            }
            viewModel.loadTopics()
        } else {
            activityBinding.appBarLayout.apply {
                setText("Chi tiết khoá học");
                setBackButtonVisible(true);
            }
            viewModel.loadCourseDetail(courseId)
        }

        activityBinding.appBarLayout.apply {
            removeCustomView()
            setOnClickBack()
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
                if(!isOwner){
                    binding.learningBtn.visibility = View.VISIBLE
                }
            }
        }

        binding.vocabularyBtn.setOnClickAction {
            if (isSpeakingMode) {
                isSpeakingMode = false
                updateToggleUI()
                binding.speakingLayout.visibility = View.GONE
                binding.vocabularyLayout.visibility = View.VISIBLE
                if(!isOwner){
                    binding.learningBtn.visibility = View.GONE
                }
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
            }
        }

        // THEO DOI DS TOPIC (CHO VIỆC TẠO MỚI)
        viewModel.topicData.observe(viewLifecycleOwner) { topics ->
            if (topics.isNullOrEmpty()) return@observe
            list_topic = topics
            selectedTopicId = list_topic[0].id
            val list_topic_name = list_topic?.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_topic_name!!)
            binding.topicInput.setAdapter(adapter)

            handler = CourseDetailTeacher(this, binding, viewModel, activityBinding)

            binding.topicInput.setTextSize(14f)
            binding.topicInput.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            updateTopicColor(list_topic[0].colorHex)
            binding.topicInput.post {
                binding.topicInput.setText(list_topic[0].name, false)
                binding.topicInput.clearFocus()
            }

            updateRoleUI(isOwner)
            binding.teacherNameCourse.text = userManager.getUserName()
            binding.creatingRememberCard.visibility = View.VISIBLE
            handler?.setupViews()
            binding.saveBtn.setOnClickAction {
                val newTitle = binding.courseTitleInput.text.toString()
                val newDesc = binding.introductionInput.text.toString()
                val newTopicId = selectedTopicId
                val newImageUrl = null
                val request = CreateCourseRequest(
                    topicId = newTopicId,
                    title = newTitle,
                    description = newDesc,
                    privacy = if (isPublic) "PUBLIC" else "PRIVATE",
                    thumbnailUrl = newImageUrl
                )
                binding.saveBtn.setText("Đang lưu thông tin...", ContextCompat.getColor(requireContext(), R.color.white))
                if (newTitle.isNotBlank() || newDesc.isNotBlank()) {
                    viewModel.createCourse(request)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // THEO DOI TINH TRANG KHOA HOC TRA VE
        viewModel.courseDetailData.observe(viewLifecycleOwner) { course ->
            if (course == null){
                Toast.makeText(requireContext(), "Không tìm thấy dữ liệu khóa học", Toast.LENGTH_SHORT).show()
                return@observe
            }
            if (courseId != -1L) {
                binding.creatingRememberCard.visibility = View.GONE
            }

            isOwner = (course.creator.id == userManager.getUserId())
            updateRoleUI(isOwner)
            if (course.deckId == null && !isOwner){
                binding.vocabularyListLayout.visibility = View.GONE
            }
            if(handler == null){
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
            if (flashcards.isNullOrEmpty()){
                if(!isOwner){
                    binding.vocabularyListLayout.visibility = View.GONE
                }
                return@observe
            } else {
                if(!isOwner){
                    binding.vocabularyListLayout.visibility = View.VISIBLE
                }
            }
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

        viewModel.createCourseStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { newId ->
                Toast.makeText(requireContext(), "Tạo khoá học thành công!", Toast.LENGTH_SHORT).show()
                this.courseId = newId

                viewModel.resetCreateCourseStatus()
                viewModel.resetTopicData()
                binding.saveBtn.setText(
                    "Lưu thông tin",
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                val bundle = bundleOf("courseId" to newId)
                findNavController().navigate(R.id.courseDetailFragment, bundle,
                    NavOptions.Builder()
                    .setPopUpTo(R.id.courseDetailFragment, true) // Xoá màn hình "Tạo mới" khỏi BackStack
                    .build())

            }?.onFailure {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateCourseStatus()
                binding.saveBtn.setText(
                    "Lưu thông tin",
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
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

    internal fun updateTopicColor(colorHex: String?) {
        if (colorHex.isNullOrEmpty()) return
        try {
            val colorInt = android.graphics.Color.parseColor(colorHex)
            binding.topicInputLayout.apply {
                boxBackgroundColor = colorInt
                setBoxStrokeColorStateList(android.content.res.ColorStateList.valueOf(colorInt))
            }
            binding.topicInput.apply {
                setTextColor(android.graphics.Color.WHITE)
            }
        } catch (e: Exception) {
            Log.e("COLOR_ERROR", "Mã màu $colorHex không hợp lệ")
        }
    }

    private fun updateRoleUI(isTeacherMode: Boolean) {
        if (isTeacherMode) {
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


