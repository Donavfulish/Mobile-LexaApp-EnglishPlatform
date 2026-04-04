package com.home.lexa.ui.course.course_detail


import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.home.lexa.R
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.NormalInput
import com.home.lexa.ui.components.PopUpInput
import com.home.lexa.ui.components.StudentSpeakingDayCard
import com.home.lexa.ui.components.TeacherSpeakingDayCard
import com.home.lexa.ui.components.ToggleSwitch

import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseDetailFragment : BaseFragment<FragmentCourseDetailBinding>(FragmentCourseDetailBinding::inflate) {
    private val viewModel: CourseDetailViewModel by viewModel()
    private var isSpeakingMode = true
    private var isTeacher = true
    private var isPublic = true
    private var selectedTopicId: Int? = null
    var courseId = 17L
    private lateinit var list_topic: List<Topic>
    override fun setupViews() {

//        val courseId = arguments?.getLong("courseId") ?: -1L

        if (courseId == -1L) {
            Toast.makeText(requireContext(), "ID khóa học không hợp lệ", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.loadCourseDetail(courseId)
        }

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setOnClickBack()
        }
        activityBinding.appBarLayout.apply {
            val linearLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            val publicTitleView = TextView(requireContext()).apply {
                setText("Public")
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 14f)

                val typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.archivo_bold)
                setTypeface(typeface)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setPadding(0, 0, 20, 0)
            }
            val publicToggleView = ToggleSwitch(requireContext())
            publicToggleView.isChecked = isPublic
            publicToggleView.onCheckedChangeListener = { isChecked ->
                isPublic = isChecked
            }
            linearLayout.addView(publicTitleView)
            linearLayout.addView(publicToggleView)
            insertCustomeViewRight(linearLayout)
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


        // =============================================ROLE SETUP==========================================
        if(isTeacher){
            setUpTeacher()
        } else {
            setUpStudent()
        }

        binding.topic.setOnClickAction {
            AppMemoryCache.remove("speakingCourseDetail_${courseId}")
            AppMemoryCache.remove("vocabularyList_${courseId}")
            val check:SpeakingCourseDetailDto?  = AppMemoryCache.get("speakingCourseDetail_17")
            Log.e("DEBUG_CACHE", "Cache after remove: $check")
        }
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
                if(isTeacher){
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

        // THEO DOI TINH TRANG KHOA HOC TRA VE
        viewModel.courseDetailData.observe(viewLifecycleOwner) { course ->
            if (course == null){
                Toast.makeText(requireContext(), "Không tìm thấy dữ liệu khóa học", Toast.LENGTH_SHORT).show()
                return@observe
            }
            list_topic = viewModel.courseDetailData.value?.list_topic!!
            val list_topic_name = list_topic?.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_topic_name!!)
            binding.topicInput.setAdapter(adapter)

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

            if(isTeacher){
                selectedTopicId = course.list_topic.find { it.name == course.type }?.id
                binding.topicInput.setTextSize(14f)
                binding.topicInput.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                updateTopicColor(course.typeColor ?: list_topic[0].colorHex)
                binding.topicInput.post {
                    binding.topicInput.setText(course.type, false)
                    binding.topicInput.clearFocus()
                }

                binding.courseTitleInput.setText(course.title)
                binding.introductionInput.setText(course.description)
            } else {
                // Khoa hoc
                binding.titleCourse.text = course.title
                binding.topic.apply {
                    setTextSize(12f)
                    setText(course.type!!, ContextCompat.getColor(requireContext(), android.R.color.white))
                    setBackground( android.graphics.Color.parseColor(course.typeColor))
                }
                binding.introduction.text = course.description ?: ""
            }

            course.list_speaking_day.forEachIndexed {index, day ->
                val dayCard = if (isTeacher) {
                    TeacherSpeakingDayCard(requireContext()).apply {
                        setData(
                            _day = index + 1,
                            _title = day.title,
                            _paragraphNum = day.paragraphNum
                        )
                        setOnClickAction {
                            val bundle = bundleOf("speakingDayId" to day.speakingDayId)
                            findNavController().navigate(
                                R.id.action_courseDetailFragment_to_speakingPracticeFragment,
                                bundle
                            )
                        }
                    }
                } else {
                    StudentSpeakingDayCard(requireContext()).apply {
                        setData(
                            _day = index + 1,
                            _title = day.title,
                            _progressPercent = day.completed
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

        // THEO DOI TINH TRANG FLASHCARD TRA VE
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
                if (isTeacher){
                    binding.vocabularyGrid2.addView(card)
                } else {
                    binding.vocabularyGrid.addView(card)
                }
            }
        }

        // THEO DOI TINH TRANG CAP NHAT TT KHOA HOCC
        viewModel.updateStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
                binding.saveBtn.setText("Lưu thông tin", ContextCompat.getColor(requireContext(), R.color.white))
            }?.onFailure {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
                binding.saveBtn.setText("Lưu thông tin", ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        // THEO DOI TINH TRANG THEM MOI NGAY HOC
        viewModel.createStatus.observe(viewLifecycleOwner){ result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Thêm ngày học mới thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateStatus()
                viewModel.loadCourseDetail(courseId)
            }?.onFailure {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("CREATE_STATUS", "Lỗi: ${it.message}", it)
                viewModel.resetCreateStatus()
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

    private fun updateTopicColor(colorHex: String?) {
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

    private fun setUpStudent(){
        binding.learningBtn.apply {
            setTextSize(20f)
            setText("Tiếp tục học ngay", ContextCompat.getColor(requireContext(), R.color.white))
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
        }
    }

    private fun setUpTeacher(){
        binding.editToggle.onCheckedChangeListener = { isChecked ->
            for (i in 0 until binding.vocabularyGrid2.childCount) {
                val child = binding.vocabularyGrid2.getChildAt(i)
                if (child is FlashcardMini) {
                    child.setIsEditable(isChecked)
                }
            }
        }

        binding.topicInput.setOnItemClickListener{_, _, position, _->
            selectedTopicId = list_topic[position].id
            updateTopicColor(list_topic[position].colorHex)
            binding.topicInput.setText(list_topic[position].name, false)
            binding.topicInput.clearFocus()
        }

        binding.saveBtn.setOnClickAction {
            val newTitle = binding.courseTitleInput.text.toString()
            val newDesc = binding.introductionInput.text.toString()
            val newTopicId = selectedTopicId

            val request = EditCourseRequest(
                topicId = newTopicId,
                title = newTitle,
                description = newDesc,
                privacy = if (isPublic) "PUBLIC" else "PRIVATE",
                thumbnailUrl = viewModel.courseDetailData.value?.thumbnail_url ?: null
            )
            
            binding.saveBtn.setText("Đang lưu thông tin...", ContextCompat.getColor(requireContext(), R.color.white))

            if (newTitle.isNotBlank()) {
                viewModel.editCourse(courseId, request)
            }
        }

        binding.cameraBtn.apply{
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_camera)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.white_opacity))
        }
        binding.addBtn.apply {
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
        }
        binding.saveBtn.apply{
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setText("Lưu thông tin", ContextCompat.getColor(requireContext(), R.color.white))
        }

        val popUpInput = PopUpInput(requireContext())
        val speakingDayTitle = NormalInput(requireContext()).apply {
            setLabel("Tiêu đề")
            setPlaceHolderText("Nhập tiêu đề ngày học...")
        }
        popUpInput.insertNormalInput(speakingDayTitle)
        binding.addBtn.setOnClickAction {
            popUpInput.showDialog(
                dialogTitle = "Tạo ngày học mới",
                confirmText = "Tạo ngay",
                onConfirm = { dataList ->
                    viewModel.createSpeakingDay(CreateSpeakingDayRequest(
                        courseId = courseId,
                        title = dataList[0]
                    ))
                },
                onCancel = {
                    Log.d("DEBUG_POPUP", "Đã hủy bỏ")
                }
            )
        }
    }
}


