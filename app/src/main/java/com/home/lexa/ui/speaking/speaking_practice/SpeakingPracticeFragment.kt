package com.home.lexa.ui.speaking.speaking_practice

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSpeakingPracticeBinding
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import androidx.navigation.fragment.findNavController
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.ui.components.NormalInput
import com.home.lexa.ui.components.ParagraphEditCard
import com.home.lexa.ui.components.PopUpInput
import com.home.lexa.ui.components.Popup
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakingPracticeFragment : BaseFragment<FragmentSpeakingPracticeBinding>(FragmentSpeakingPracticeBinding::inflate) {
    private val viewModel: SpeakingPracticeViewModel by viewModel()
    private var speakingDayId = -1L
    private var courseId = -1L
    private var order = 0
    override fun setupViews() {
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
        order = arguments?.getInt("order") ?: 0

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setOnClickBack()
            setText("Ngày ${order + 1}")
            setBackButtonVisible(true)
        }
        activityBinding.appBarLayout.apply {
            setIconRightButton(ContextCompat.getDrawable(requireContext(), R.drawable.trash)!!)
            setOnClickToggleRightButton { _ ->
                val confirmPopup = Popup(requireContext())
                confirmPopup.showDialog(
                    title = "Xóa ngày học",
                    subTitle = "Bạn có chắc chắn muốn xóa toàn bộ ngày học này? Tất cả các đoạn văn bên trong cũng sẽ bị mất.",
                    isWarning = true,
                    confirmText = "Xóa toàn bộ",
                    onConfirm = {

                        viewModel.deleteSpeakingDay(speakingDayId, courseId)
                    }
                )
            }
        }


        if (speakingDayId != -1L) {
            viewModel.loadParagraphList(speakingDayId)
        }
        
        binding.saveBtn.apply{
            setBackground(ContextCompat.getColor(requireContext(), R.color.purple_paragraph))
            setText("Lưu thông tin", ContextCompat.getColor(requireContext(), R.color.white))
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
                binding.addBtn.visibility = View.VISIBLE
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Cập nhật tiêu đề thành công!", Toast.LENGTH_SHORT).show()
                binding.saveBtn.setText("Lưu thông tin", ContextCompat.getColor(requireContext(), R.color.white))
                viewModel.resetUpdateStatus()
            }
        }

        viewModel.updateParagraphStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Cập nhật đoạn văn thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateParagraphStatus()
                viewModel.loadParagraphList(speakingDayId)
            }?.onFailure {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateParagraphStatus()
            }
        }

        viewModel.createStatus.observe(viewLifecycleOwner){ result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Thêm đoạn văn mới thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateStatus()
                viewModel.loadParagraphList(speakingDayId)
            }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Xóa đoạn văn thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteStatus()
                viewModel.loadParagraphList(speakingDayId)
            }
        }

        viewModel.deleteSpeakingDayStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), "Đã xóa ngày học thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteSpeakingDayStatus()

                findNavController().popBackStack()
            }?.onFailure {
                Toast.makeText(requireContext(), "Lỗi khi xóa ngày học: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteSpeakingDayStatus()
            }
        }

        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            data?.let { detail ->
                binding.paragraphInput.setText(detail.title ?: "")

                binding.saveBtn.setOnClickAction {
                    val newTitle = binding.paragraphInput.text.toString().trim()
                    if (newTitle.isNotEmpty()){
                        binding.saveBtn.setText("Đang lưu...", ContextCompat.getColor(requireContext(), R.color.white))
                        viewModel.editSpeakingDay(speakingDayId, EditSpeakingDayRequest(title = newTitle))
                    }
                }

                binding.paragraphLayout.removeAllViews()
                
                val paragraphs = detail.list_paragraphs ?: emptyList()
                binding.paragraphNum.text = paragraphs.size.toString()

                paragraphs.forEach { paragraph ->
                    val paragraphCard = ParagraphEditCard(requireContext())
                    paragraphCard.setData(
                        _order = paragraph.paragraph_order?.toInt() ?: 0,
                        _paragraph = paragraph.paragraph ?: ""
                    )

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 32)
                    }
                    paragraphCard.layoutParams = params

                    paragraphCard.initSwipe()
                    
                    paragraphCard.setOnEditClickListener {
                        val popUpEdit = PopUpInput(requireContext())
                        val editInput = NormalInput(requireContext()).apply {
                            setLabel("Nội dung đoạn văn")
                            setText(paragraph.paragraph)
                        }
                        popUpEdit.insertNormalInput(editInput)
                        popUpEdit.showDialog(
                            dialogTitle = "Chỉnh sửa đoạn văn",
                            confirmText = "Cập nhật",
                            onConfirm = { dataList ->
                                viewModel.updateParagraph(paragraph.id, dataList[0])
                            }
                        )
                    }

                    paragraphCard.setOnDeleteClickListener {
                        // Thêm Popup xác nhận xoá
                        val confirmPopup = Popup(requireContext())
                        confirmPopup.showDialog(
                            title = "Xác nhận xoá",
                            subTitle = "Bạn có chắc chắn muốn xoá đoạn văn này không? Hành động này không thể hoàn tác.",
                            isWarning = true,
                            confirmText = "Xoá ngay",
                            onConfirm = {
                                viewModel.deleteParagraph(speakingDayId, paragraph.id)
                            }
                        )
                    }

                    binding.paragraphLayout.addView(paragraphCard)
                }

                binding.addBtn.setOnClickAction {
                    val popUpAdd = PopUpInput(requireContext())
                    val addInput = NormalInput(requireContext()).apply {
                        setLabel("Nội dung đoạn văn")
                        setPlaceHolderText("Nhập đoạn văn mới...")
                    }
                    popUpAdd.insertNormalInput(addInput)
                    popUpAdd.showDialog(
                        dialogTitle = "Tạo đoạn văn mới",
                        confirmText = "Tạo ngay",
                        onConfirm = { dataList ->
                            viewModel.createParagraph(speakingDayId, dataList[0], paragraphs.size + 1)
                        }
                    )
                }

            }
        }
    }
}
