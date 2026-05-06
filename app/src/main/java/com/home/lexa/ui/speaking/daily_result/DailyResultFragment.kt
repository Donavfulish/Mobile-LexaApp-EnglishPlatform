package com.home.lexa.ui.speaking.daily_result

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentDailyResultBinding
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.domain.models.ParagraphWord
import com.home.lexa.ui.components.ParagraphCard
import com.home.lexa.ui.speaking.speaking_practice.PracticeSharedViewModel
import com.home.lexa.ui.utils.AudioManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DailyResultFragment : BaseFragment<FragmentDailyResultBinding>(FragmentDailyResultBinding::inflate) {

    // Lấy chung Cache với màn Practice
    private val sharedViewModel: PracticeSharedViewModel by sharedViewModel()

    // Dùng ViewModel riêng để gọi API khi cần thiết
    private val dailyResultViewModel: DailyResultViewModel by viewModel()

    private lateinit var audioManager: AudioManager
    private var fromCompletedDay = false
    private var courseId = -1L
    private var speakingDayId = -1L

    override fun setupViews() {
        audioManager = AudioManager(requireContext())
        fromCompletedDay = arguments?.getBoolean("fromCompletedDay", false) ?: false
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
        sharedViewModel.bindSpeakingDay(speakingDayId, resetOnChange = true)

        // 1. LẤY DỮ LIỆU TỪ CACHE VÀ HIỂN THỊ NGAY LẬP TỨC
        loadDataFromCache()

        // 2. NÚT HỌC LẠI: Hủy cache và quay về
        binding.btnRetry.setOnClickListener {
            sharedViewModel.clearCache()
            if (fromCompletedDay && courseId != -1L && speakingDayId != -1L) {
                val bundle = bundleOf(
                    "courseId" to courseId,
                    "speakingDayId" to speakingDayId,
                    "forceStartOver" to true,
                    "returnToCourseAfterSave" to true
                )
                findNavController().navigate(
                    R.id.action_dailyResultFragment_to_speakingPracticeStudentFragment,
                    bundle
                )
            } else {
                findNavController().popBackStack()
            }
        }

        // 3. NÚT TRỞ VỀ KHÓA HỌC: Đẩy cache lên Database -> Hủy cache -> Quay về
        binding.btnBackToCourse.setOnClickListener {
            saveCacheToDatabase()
        }
    }

    private fun loadDataFromCache() {
        val cacheData = sharedViewModel.sessionCache.values.toList()

        if (cacheData.isEmpty()) return

        // Tính tổng số từ
        val totalGood = cacheData.sumOf { it.goodCount }
        val totalAccepted = cacheData.sumOf { it.mediumCount }
        val totalBad = cacheData.sumOf { it.badCount }
        val totalWords = totalGood + totalAccepted + totalBad

        // Cập nhật UI Tổng quan
        val progress = if (totalWords > 0) ((totalGood + totalAccepted).toFloat() / totalWords * 100).toInt() else 0
        binding.progressRing.setProgress(progress)
        binding.tvGoodCount.text = totalGood.toString()
        binding.tvAcceptedCount.text = totalAccepted.toString()
        binding.tvBadCount.text = totalBad.toString()

        // Cập nhật UI danh sách Paragraph
        binding.paragraphList.removeAllViews()
        cacheData.forEachIndexed { index, cacheItem ->
            val card = ParagraphCard(requireContext())
            val layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 48) }
            card.layoutParams = layoutParams

            // Chuyển đổi WordEvaluationItem từ cache sang định dạng ParagraphWord của UI
            val uiWords = cacheItem.evaluationResults.map { eval ->
                val color = when (eval.status) {
                    "GOOD" -> "green"
                    "MEDIUM" -> "yellow"
                    "BAD" -> "red"
                    else -> "red"
                }
                ParagraphWord(eval.word, color)
            }

            // Tạo data giả lập để nhét vào Card
            val paragraphResult = ParagraphResult(
                id = cacheItem.paragraphId.toInt(),
                order = (index + 1).toString(),
                paragraph = uiWords,
                audioUrl = "", // Chỗ này nếu có audio mẫu thì truyền vào
                userUrl = cacheItem.localAudioPath // Truyền file path local
            )

            card.displayParagraph(paragraphResult)
            card.setOnClickUserSound {
                val path = cacheItem.localAudioPath
                if (!path.isNullOrEmpty() && java.io.File(path).exists()) {
                    audioManager.playAudio(path) {
                        // Callback khi nghe xong (nếu cần đổi icon nút bấm)
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.record_not_found), Toast.LENGTH_SHORT).show()
                }
            }
            binding.paragraphList.addView(card)
        }
    }

    private fun saveCacheToDatabase() {
        // Hiện loading ở đây...
        val cacheData = sharedViewModel.sessionCache.values.toList()

        // Lấy speakingDayId từ arguments (được truyền từ SpeakingPracticeStudentFragment sang)
        if (speakingDayId != -1L) {
            // Gọi ViewModel với đủ 2 tham số
            dailyResultViewModel.submitFinalResult(speakingDayId, cacheData)
        } else {
            Toast.makeText(requireContext(), getString(R.string.lesson_id_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
        // Lắng nghe trạng thái upload từ dailyResultViewModel
        dailyResultViewModel.uploadStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                // Xóa cache sau khi lưu DB thành công
                sharedViewModel.clearCache()
                Toast.makeText(requireContext(), getString(R.string.save_result_success), Toast.LENGTH_SHORT).show()
                // Quay về màn course
                findNavController().popBackStack(R.id.courseFragment, false)
            } else if (isSuccess == false) {
                Toast.makeText(requireContext(), getString(R.string.save_result_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioManager.release()
    }
}