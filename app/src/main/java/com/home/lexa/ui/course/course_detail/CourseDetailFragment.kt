package com.home.lexa.ui.course.course_detail


import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.home.lexa.R
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.Topic
import com.home.lexa.core.Constants
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
    internal var deckId: Long = -1L
    internal lateinit var list_topic: List<Topic>
    internal var courseImageUri: Uri? = null
    private val activityBinding by lazy { (requireActivity() as MainActivity).binding }

    private val userManager: UserManager by inject()

    override fun onDestroyView() {
        super.onDestroyView()
        // RESET DU LIEU KHI THOAT DE TRANH UI NHAY KHI QUAY LAI
        viewModel.clearData()
        handler = null
    }

    internal val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            courseImageUri = uri
            binding.backgroundCourse.load(uri) {
                crossfade(true)
            }
        } else {
            // Người dùng đóng thư viện mà không chọn ảnh
        }
    }

    override fun setupViews() {
        isOwner = true
        courseId = arguments?.getLong("courseId") ?: -1L
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>("refreshCourseDetail")
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                if (shouldRefresh == true && courseId != -1L) {
                    viewModel.loadCourseDetail(courseId)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set("refreshCourseDetail", false)
                }
            }
        if (courseId == -1L) {
            activityBinding.appBarLayout.apply {
                setText(getString(R.string.create_new_course))
                setBackButtonVisible(true)
            }
            viewModel.loadTopics()
        } else {
            activityBinding.appBarLayout.apply {
                setText(getString(R.string.course_detail))
                setBackButtonVisible(true)
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
            setText(getString(R.string.speaking), ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setBackground(ContextCompat.getColor(requireContext(), R.color.white))
        }

        binding.vocabularyBtn.apply{
            setTextSize(16f)
            setIconPadding(5)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_vocabulary))
            setText(getString(R.string.vocabulary_tab), null)
            setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
        }

        binding.cameraBtn.setOnClickAction {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                binding.learningBtn.visibility = View.GONE
            }
        }

        binding.searchBarVocabulary.apply {
            onSearchAction { q ->
                val currentDeckId = viewModel.courseDetailData.value?.deckId
                if (currentDeckId != null) {
                    viewModel.searchInfor = viewModel.searchInfor.copy(query = q)
                    viewModel.loadMoreFlashcards(false, currentDeckId, viewModel.searchInfor, null)
                } else {
                    Log.e("SEARCH_DEBUG", "DeckId is null, cannot search")
                }
            }
            setOnSortOptionChanged { options ->
                val currentDeckId = viewModel.courseDetailData.value?.deckId
                if (currentDeckId != null) {
                    viewModel.searchInfor = viewModel.searchInfor.copy(sortBy = options.sortBy, order = options.order)
                    viewModel.loadMoreFlashcards(false, currentDeckId, viewModel.searchInfor, null)
                }
            }
            onTextChanged { q ->
                if(q.length >= 2){
                    viewModel.getSuggestions(q)
                }
            }
        }

        // Xử lý nút yêu thích
        binding.circleFavorite.setOnClickListener {
            val course = viewModel.courseDetailData.value
            if (course != null && course.deckId != null) {
                val isCurrentlyFavorite = course.is_favorite == true
                if (isCurrentlyFavorite) {
                    viewModel.removeFavorite(course.id, course.deckId)
                } else {
                    viewModel.setFavorite(course.id, course.deckId)
                }
            }
        }

        // Xử lý nút đóng Mẹo học nhanh
        binding.btnCloseTips.setOnClickListener {
            binding.rememberCard.visibility = View.GONE
            userManager.setHideQuickTips()
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

        viewModel.paginationLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading){
                binding.paginationProgressBar.visibility = View.VISIBLE
            } else {
                binding.paginationProgressBar.visibility = View.GONE
            }
        }

        // THEO DOI DS TOPIC (CHO VIỆC TẠO MỚI)
        viewModel.topicData.observe(viewLifecycleOwner) { topics ->
            if (topics.isNullOrEmpty()) return@observe
            list_topic = topics
            selectedTopicId = list_topic[0].id
            val list_topic_name = list_topic.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_topic_name)
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
                val request = CreateCourseRequest(
                    topicId = newTopicId,
                    title = newTitle,
                    description = newDesc,
                    privacy = if (isPublic) "PUBLIC" else "PRIVATE",
                    thumbnailUrl = null
                )
                binding.saveBtn.setText(getString(R.string.saving_information), ContextCompat.getColor(requireContext(), R.color.white))
                if (newTitle.isNotBlank() || newDesc.isNotBlank()) {
                    viewModel.createCourse(request, courseImageUri)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_information), Toast.LENGTH_LONG).show()
                }
            }
        }

        // THEO DOI TINH TRANG KHOA HOC TRA VE
        viewModel.courseDetailData.observe(viewLifecycleOwner) { course ->
            if (course == null){
                // SỬA: Không Toast báo lỗi ở đây vì có thể nó null do clearData
                return@observe
            }
            deckId = course.deckId!!
            if (courseId != -1L) {
                binding.creatingRememberCard.visibility = View.GONE
            }

            if(course.creator.id != userManager.getUserId()) {
                isOwner = false
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
            binding.imgTeacher.load(course.creator.image ?: Constants.DEFAULT_AVATAR_URL) {
                crossfade(true)
                placeholder(R.drawable.placeholder_teacher)
                error(R.drawable.placeholder_teacher)
            }
            binding.teacherNameCourse.text = course.creator.name
            binding.studentNumCourse.text = course.studying_user_count.toString()
            binding.favoriteNumCourse.text = course.favorite_user_count.toString()
            binding.speakingNum.text = getString(R.string.lesson_count, course.list_speaking_day.totalItems)
            binding.speakingDayLayout.removeAllViews()

            // Cập nhật icon yêu thích
            val isFavorite = course.is_favorite == true
            binding.circleFavorite.setIconResource(
                if (isFavorite) R.drawable.ic_favorite_btn else R.drawable.ic_favorite_border_btn
            )
            binding.circleFavorite.setIconTint(
                android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), if (isFavorite) R.color.red else R.color.purple_paragraph)
                )
            )

            // =====================================THONG TIN HIEN THI KHOA HOC=====================================
            handler?.bindCourseData(course)
            binding.contentScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _->
                val content = v.getChildAt(0)
                val totalContentHeight = content.measuredHeight
                val screenHeight = v.measuredHeight
                val threshold = 300
                if (scrollY + screenHeight >= totalContentHeight - threshold) {
                    if (viewModel.paginationLoading.value == false && !viewModel.isLastPage) {
                        if (isSpeakingMode) {
                            viewModel.loadMoreSpeakingDay(true, courseId, viewModel.nextItem)
                        }
                        else {
                            if (isOwner) {
                                viewModel.loadMoreFlashcards(
                                    true, deckId, viewModel.searchInfor, viewModel.nextItem
                                )
                            }
                        }
                    }
                }
            })
        }

        // Quan sát trạng thái thay đổi yêu thích
        viewModel.favoriteStatus.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            
            result.onSuccess {
                // Kiểm tra trạng thái hiện tại (trước khi refresh) để hiện thông báo đúng
                val isCurrentlyFavorite = viewModel.courseDetailData.value?.is_favorite == true
                
                // Nếu trạng thái cũ là false -> vừa thực hiện yêu thích -> hiện "yêu thích thành công"
                // Nếu trạng thái cũ là true -> vừa thực hiện bỏ yêu thích -> hiện "bỏ yêu thích thành công"
                val messageRes = if (isCurrentlyFavorite) R.string.unfavorite_success else R.string.favorite_success
                
                Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show()
                
                // Tải lại dữ liệu mới nhất
                viewModel.loadCourseDetail(courseId)
                viewModel.resetFavoriteStatus()
            }.onFailure {
                Toast.makeText(requireContext(), getString(R.string.error_message, it.message), Toast.LENGTH_SHORT).show()
                viewModel.resetFavoriteStatus()
            }
        }

        // THEO DOI TINH TRANG SPEAKINGDAY TRA VE
        viewModel.speakingDayDetailData.observe(viewLifecycleOwner){ speakingDays ->
            if (speakingDays.isNullOrEmpty()){
                return@observe
            }
            handler?.bindSpeakingData(courseId, speakingDays)
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

            if (viewModel.totalPages > 0) {
                binding.flashcardNum.text = "${viewModel.totalPages}"
            } else if (flashcards.isNotEmpty()) {
                binding.flashcardNum.text = flashcards.size.toString()
            }

            binding.vocabularyGrid.removeAllViews()
            binding.vocabularyGrid2.removeAllViews()
            handler?.bindFlashcardData(flashcards)

            binding.vocabularyListLayout.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _->
                val content = v.getChildAt(0)
                val totalContentHeight = content.measuredHeight
                val screenHeight = v.measuredHeight
                val threshold = 300
                if (scrollY + screenHeight >= totalContentHeight - threshold) {
                    if (viewModel.paginationLoading.value == false && !viewModel.isLastPage) {
                        if (!isSpeakingMode && !isOwner) {
                            viewModel.loadMoreFlashcards(true, deckId, viewModel.searchInfor, viewModel.nextItem)                        }
                    }
                }
            })
        }

        viewModel.createCourseStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { newId ->
                Toast.makeText(requireContext(), getString(R.string.create_course_success), Toast.LENGTH_SHORT).show()
                this.courseId = newId

                viewModel.resetCreateCourseStatus()
                viewModel.resetTopicData()
                binding.saveBtn.setText(
                    getString(R.string.save_information),
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
                AppMemoryCache.removePrefix("getAllCourses_")
                AppMemoryCache.removePrefix("getFavoriteCourses_")
                AppMemoryCache.removePrefix("getMyCourses_")
                val bundle = bundleOf("courseId" to newId)
                findNavController().navigate(R.id.courseDetailFragment, bundle,
                    NavOptions.Builder()
                    .setPopUpTo(R.id.courseDetailFragment, true) // Xoá màn hình "Tạo mới" khỏi BackStack
                    .build())

            }?.onFailure {
                Toast.makeText(requireContext(), getString(R.string.error_message, it.message), Toast.LENGTH_SHORT).show()
                viewModel.resetCreateCourseStatus()
                binding.saveBtn.setText(
                    getString(R.string.save_information),
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
            }
        }
        
        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            binding.searchBarVocabulary.setSuggestions(suggestions)
        }
    }

    internal fun updateToggleUI() {
        viewModel.nextItem = null
        viewModel.currentPages = 0
        viewModel.totalPages = 0
        viewModel.isLastPage = false
        if (isSpeakingMode) {
            binding.speakingBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setText(getString(R.string.speaking), ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setBackground(ContextCompat.getColor(requireContext(), R.color.white))
            }
            binding.vocabularyBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.black))
                setText(getString(R.string.vocabulary_tab), ContextCompat.getColor(requireContext(), R.color.black))
                setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
            }
        } else {
            binding.speakingBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.black))
                setBackground(ContextCompat.getColor(requireContext(), R.color.gray_E0E0E5))
                setText(getString(R.string.speaking), ContextCompat.getColor(requireContext(), R.color.black))
            }
            binding.vocabularyBtn.apply {
                setIconColor(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
                setBackground(ContextCompat.getColor(requireContext(), R.color.white))
                setText(getString(R.string.vocabulary_tab), ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
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

            // Kiểm tra trạng thái hiển thị Mẹo học nhanh (chỉ dành cho học sinh)
            binding.rememberCard.visibility = if (userManager.shouldShowQuickTips()) View.VISIBLE else View.GONE
        }
    }

}
