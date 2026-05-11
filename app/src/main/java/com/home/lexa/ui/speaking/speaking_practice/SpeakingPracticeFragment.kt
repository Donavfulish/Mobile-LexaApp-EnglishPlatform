package com.home.lexa.ui.speaking.speaking_practice

import android.content.ClipData
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
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
import com.home.lexa.domain.models.ParagraphOrderDto
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.ui.components.NormalInput
import com.home.lexa.ui.components.ParagraphEditCard
import com.home.lexa.ui.components.PopUpInput
import com.home.lexa.ui.components.Popup
import com.home.lexa.ui.utils.SpeechToTextManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakingPracticeFragment : BaseFragment<FragmentSpeakingPracticeBinding>(FragmentSpeakingPracticeBinding::inflate) {
    private val viewModel: SpeakingPracticeViewModel by viewModel()
    private var speakingDayId = -1L
    private var courseId = -1L
    private var order = 0
    private lateinit var sttManager: SpeechToTextManager
    private var currentRecognizedText: String = ""

    override fun setupViews() {
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
        order = arguments?.getInt("order") ?: 0
        sttManager = SpeechToTextManager(requireContext())

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setOnClickBack()
            setText(getString(R.string.speaking_day_order, order + 1))
            setBackButtonVisible(true)
        }
        activityBinding.appBarLayout.apply {
            setIconRightButton(ContextCompat.getDrawable(requireContext(), R.drawable.trash)!!)
            setOnClickToggleRightButton { _ ->
                val confirmPopup = Popup(requireContext())
                confirmPopup.showDialog(
                    title = getString(R.string.delete_speaking_day_title),
                    subTitle = getString(R.string.delete_speaking_day_msg),
                    isWarning = true,
                    confirmText = getString(R.string.delete_all),
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
            setText(getString(R.string.save_info), ContextCompat.getColor(requireContext(), R.color.white))
        }

        // Xử lý sự kiện click "Lưu thứ tự mới"
        binding.btnSaveOrder.setOnClickListener {
            val requestList = mutableListOf<ParagraphOrderDto>()

            // Quét lại vị trí của các view trong LinearLayout để tạo danh sách thứ tự mới
            for (i in 0 until binding.paragraphLayout.childCount) {
                val child = binding.paragraphLayout.getChildAt(i)
                val id = child.tag as? Long ?: continue
                requestList.add(ParagraphOrderDto(id = id, order = (i + 1).toLong()))
            }

            binding.btnSaveOrder.text = getString(R.string.saving)
            viewModel.reorderParagraphs(courseId, speakingDayId, ReorderParagraphsRequest(requestList))
        }
    }

    override fun onResume() {
        super.onResume()
        if (speakingDayId != -1L) {
            viewModel.loadParagraphList(speakingDayId)
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
                Toast.makeText(requireContext(), getString(R.string.update_title_success), Toast.LENGTH_SHORT).show()
                binding.saveBtn.setText(getString(R.string.save_info), ContextCompat.getColor(requireContext(), R.color.white))
                viewModel.resetUpdateStatus()
            }
        }

        viewModel.updateParagraphStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.update_paragraph_success), Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateParagraphStatus()
                viewModel.loadParagraphList(speakingDayId)
            }?.onFailure {
                Toast.makeText(requireContext(), getString(R.string.save_error_msg, it.message), Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateParagraphStatus()
            }
        }

        viewModel.createStatus.observe(viewLifecycleOwner){ result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.add_paragraph_success), Toast.LENGTH_SHORT).show()
                viewModel.resetCreateStatus()
                viewModel.loadParagraphList(speakingDayId)
            }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.delete_paragraph_success), Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteStatus()
                viewModel.loadParagraphList(speakingDayId)
            }
        }

        viewModel.deleteSpeakingDayStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.delete_day_success), Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteSpeakingDayStatus()
                findNavController().popBackStack()
            }?.onFailure {
                Toast.makeText(requireContext(), getString(R.string.save_error_msg, it.message), Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteSpeakingDayStatus()
            }
        }

        // Observer cho API Reorder
        viewModel.reorderStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.save_order_success), Toast.LENGTH_SHORT).show()
                binding.btnSaveOrder.text = getString(R.string.save_new_order)
                binding.btnSaveOrder.visibility = View.GONE
                viewModel.resetReorderStatus()
                viewModel.loadParagraphList(speakingDayId)
            }?.onFailure {
                Toast.makeText(requireContext(), getString(R.string.save_error_msg, it.message), Toast.LENGTH_SHORT).show()
                binding.btnSaveOrder.text = getString(R.string.save_new_order)
                viewModel.resetReorderStatus()
            }
        }

        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            data?.let { detail ->
                binding.paragraphInput.setText(detail.title ?: "")

                binding.saveBtn.setOnClickAction {
                    val newTitle = binding.paragraphInput.text.toString().trim()
                    if (newTitle.isNotEmpty()){
                        binding.saveBtn.setText(getString(R.string.saving), ContextCompat.getColor(requireContext(), R.color.white))
                        viewModel.editSpeakingDay(courseId, speakingDayId, EditSpeakingDayRequest(title = newTitle))
                    }
                }

                binding.paragraphLayout.removeAllViews()
                binding.btnSaveOrder.visibility = View.GONE // Reset nút lưu thứ tự

                val paragraphs = detail.list_paragraphs ?: emptyList()
                binding.paragraphNum.text = paragraphs.size.toString()

                // Khởi tạo DragListener để nhận sự kiện thả (Drop)
                val dragListener = View.OnDragListener { view, event ->
                    when (event.action) {
                        DragEvent.ACTION_DRAG_STARTED -> true
                        DragEvent.ACTION_DRAG_ENTERED -> {
                            view.alpha = 0.3f // Mờ đi khi bị view khác kéo ngang qua để báo hiệu
                            true
                        }
                        DragEvent.ACTION_DRAG_EXITED -> {
                            view.alpha = 1.0f
                            true
                        }
                        DragEvent.ACTION_DROP -> {
                            view.alpha = 1.0f
                            val draggedView = event.localState as View
                            val container = binding.paragraphLayout
                            val targetIndex = container.indexOfChild(view)

                            if (draggedView != view && targetIndex >= 0) {
                                // Đổi vị trí View trong UI
                                container.removeView(draggedView)
                                container.addView(draggedView, targetIndex)

                                // Hiện nút lưu khi có sự thay đổi
                                binding.btnSaveOrder.visibility = View.VISIBLE

                                // Cập nhật lại Text "PARAGRAPH XX" cho chuẩn visual sau khi đổi chỗ
                                for (i in 0 until container.childCount) {
                                    val child = container.getChildAt(i) as? ParagraphEditCard
                                    val pTitle = (child?.findViewById<TextView>(R.id.tvTitle))?.text.toString()
                                    child?.setData(i + 1, pTitle)
                                }
                            }
                            true
                        }
                        DragEvent.ACTION_DRAG_ENDED -> {
                            view.alpha = 1.0f
                            true
                        }
                        else -> false
                    }
                }

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

                    // Lưu ID vào tag để lát lấy ra khi gọi API
                    paragraphCard.tag = paragraph.id

                    paragraphCard.initSwipe()

                    // Gắn Drag Listener vào CHÍNH CARD ĐÓ để nó có thể làm điểm đến (nơi Drop)
                    paragraphCard.setOnDragListener(dragListener)

                    // Bắt sự kiện Touch vào ICON KÉO THẢ (6 chấm) để BẮT ĐẦU Drag
                    paragraphCard.setOnDragHandleTouchListener { v, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            val clipData = ClipData.newPlainText("id", paragraph.id.toString())
                            // Shadow hiển thị lúc kéo là toàn bộ thẻ paragraphCard
                            val shadow = View.DragShadowBuilder(paragraphCard)

                            // Truyền localState là paragraphCard để dễ đổi chỗ ở ACTION_DROP
                            v.startDragAndDrop(clipData, shadow, paragraphCard, 0)
                            true
                        } else {
                            false
                        }
                    }

                    paragraphCard.setOnEditClickListener {
                        val popUpEdit = PopUpInput(requireContext())
                        val editInput = NormalInput(requireContext()).apply {
                            setLabel(getString(R.string.paragraph_content_label))
                            post {
                                setText(paragraph.paragraph)
                            }
                        }
                        popUpEdit.insertNormalInput(editInput)
                        popUpEdit.showDialog(
                            dialogTitle = getString(R.string.edit_paragraph),
                            confirmText = getString(R.string.update),
                            onConfirm = { dataList ->
                                viewModel.updateParagraph(speakingDayId, paragraph.id, dataList[0])
                            }
                        )
                    }

                    paragraphCard.setOnDeleteClickListener {
                        val confirmPopup = Popup(requireContext())
                        confirmPopup.showDialog(
                            title = getString(R.string.confirm_delete),
                            subTitle = getString(R.string.delete_paragraph_msg),
                            isWarning = true,
                            confirmText = getString(R.string.delete_now),
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
                        setLabel(getString(R.string.paragraph_content_label))
                        setPlaceHolderText(getString(R.string.enter_new_paragraph))
                    }
                    popUpAdd.insertNormalInput(addInput)
                    popUpAdd.showDialog(
                        dialogTitle = getString(R.string.create_new_paragraph),
                        confirmText = getString(R.string.create_now),
                        onConfirm = { dataList ->
                            viewModel.createParagraph(speakingDayId, dataList[0], paragraphs.size + 1)
                        }
                    )
                }
            }
        }
    }


}