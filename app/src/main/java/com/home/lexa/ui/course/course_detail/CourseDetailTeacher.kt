package com.home.lexa.ui.course.course_detail

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.databinding.ActivityMainBinding
import com.home.lexa.databinding.FragmentCourseDetailBinding
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.ui.components.FlashcardMini
import com.home.lexa.ui.components.NormalInput
import com.home.lexa.ui.components.PopUpInput
import com.home.lexa.ui.components.Popup
import com.home.lexa.ui.components.TeacherSpeakingDayCard
import com.home.lexa.ui.components.ToggleSwitch

class CourseDetailTeacher(
    private val fragment: CourseDetailFragment,
    private val binding: FragmentCourseDetailBinding,
    private val viewModel: CourseDetailViewModel,
    private val activityBinding: ActivityMainBinding
): CourseDetailHandler {
    override fun setupViews() {
        activityBinding.appBarLayout.apply {
            val linearLayout = LinearLayout(fragment.requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            val publicTitleView = TextView(fragment.requireContext()).apply {
                setText("Public")
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 14f)

                val typeface = androidx.core.content.res.ResourcesCompat.getFont(fragment.requireContext(), R.font.archivo_bold)
                setTypeface(typeface)
                setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.black))
                setPadding(0, 0, 20, 0)
            }
            val publicToggleView = ToggleSwitch(fragment.requireContext())
            publicToggleView.isChecked = fragment.isPublic
            publicToggleView.onCheckedChangeListener = { isChecked ->
                fragment.isPublic = isChecked
            }
            linearLayout.addView(publicTitleView)
            linearLayout.addView(publicToggleView)
            insertCustomeViewRight(linearLayout)
        }

        binding.editToggle.onCheckedChangeListener = { isChecked ->
            for (i in 0 until binding.vocabularyGrid2.childCount) {
                val child = binding.vocabularyGrid2.getChildAt(i)
                if (child is FlashcardMini) {
                    child.setIsEditable(isChecked)
                }
            }
        }

        binding.topicInput.setOnItemClickListener{_, _, position, _->
            fragment.selectedTopicId = fragment.list_topic[position].id
            updateTopicColor(fragment.list_topic[position].colorHex)
            binding.topicInput.setText(fragment.list_topic[position].name, false)
            binding.topicInput.clearFocus()
        }

        binding.saveBtn.setOnClickAction {
            val newTitle = binding.courseTitleInput.text.toString()
            val newDesc = binding.introductionInput.text.toString()
            val newTopicId = fragment.selectedTopicId

            val request = EditCourseRequest(
                topicId = newTopicId,
                title = newTitle,
                description = newDesc,
                privacy = if (fragment.isPublic) "PUBLIC" else "PRIVATE",
                thumbnailUrl = viewModel.courseDetailData.value?.thumbnail_url ?: null
            )

            binding.saveBtn.setText("Đang lưu thông tin...", ContextCompat.getColor(fragment.requireContext(), R.color.white))

            if (newTitle.isNotBlank()) {
                viewModel.editCourse(fragment.courseId, request)
            }
        }

        binding.cameraBtn.apply{
            setIcon(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_camera)!!)
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.white_opacity))
        }
        binding.addBtn.apply {
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.purple_paragraph))
        }
        binding.saveBtn.apply{
            setBackground(ContextCompat.getColor(fragment.requireContext(), R.color.purple_paragraph))
            setText("Lưu thông tin", ContextCompat.getColor(fragment.requireContext(), R.color.white))
        }

        val popUpInput = PopUpInput(fragment.requireContext())
        val speakingDayTitle = NormalInput(fragment.requireContext()).apply {
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
                        courseId = fragment.courseId,
                        title = dataList[0]
                    ))
                },
                onCancel = {
                    Log.d("DEBUG_POPUP", "Đã hủy bỏ")
                }
            )
        }
    }

    override fun bindCourseData(course: SpeakingCourseDetailDto) {
        fragment.list_topic = viewModel.courseDetailData.value?.list_topic!!
        val list_topic_name = fragment.list_topic?.map { it.name }
        val adapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_1, list_topic_name!!)
        binding.topicInput.setAdapter(adapter)

        fragment.selectedTopicId = course.list_topic.find { it.name == course.type }?.id
        binding.topicInput.setTextSize(14f)
        binding.topicInput.setTextColor(ContextCompat.getColor(fragment.requireContext(), android.R.color.white))
        updateTopicColor(course.typeColor ?: fragment.list_topic[0].colorHex)
        binding.topicInput.post {
            binding.topicInput.setText(course.type, false)
            binding.topicInput.clearFocus()
        }
        binding.courseTitleInput.setText(course.title)
        binding.introductionInput.setText(course.description)
    }

    override fun bindSpeakingData(course: SpeakingCourseDetailDto) {
        course.list_speaking_day.forEachIndexed {index, day ->
            val dayCard = TeacherSpeakingDayCard(fragment.requireContext()).apply {
                    setData(
                        _day = index + 1,
                        _title = day.title,
                        _paragraphNum = day.paragraphNum
                    )
                    setOnClickAction {
                        val bundle = bundleOf(
                            "speakingDayId" to day.speakingDayId,
                            "order" to index
                        )
                        fragment.findNavController().navigate(
                            R.id.action_courseDetailFragment_to_speakingPracticeFragment,
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

    override fun bindFlashcardData(card: FlashcardMini) {
        binding.vocabularyGrid2.addView(card)
    }

    override fun observerViewModel() {
        // THEO DOI TINH TRANG CAP NHAT TT KHOA HOCC
        viewModel.updateStatus.observe(fragment.viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(fragment.requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
                binding.saveBtn.setText("Lưu thông tin", ContextCompat.getColor(fragment.requireContext(), R.color.white))
            }?.onFailure {
                Toast.makeText(fragment.requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
                binding.saveBtn.setText("Lưu thông tin", ContextCompat.getColor(fragment.requireContext(), R.color.white))
            }
        }

        // THEO DOI TINH TRANG THEM MOI NGAY HOC
        viewModel.createStatus.observe(fragment.viewLifecycleOwner){ result ->
            result?.onSuccess {
                Toast.makeText(fragment.requireContext(), "Thêm ngày học mới thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateStatus()
                viewModel.loadCourseDetail(fragment.courseId)
            }?.onFailure {
                Toast.makeText(fragment.requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("CREATE_STATUS", "Lỗi: ${it.message}", it)
                viewModel.resetCreateStatus()
            }
        }
    }

    fun updateTopicColor(colorHex: String?) {
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
}