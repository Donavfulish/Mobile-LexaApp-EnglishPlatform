package com.home.lexa.ui.speaking.daily_result

import android.widget.Toast
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

    override fun setupViews() {
        audioManager = AudioManager(requireContext())

        // 1. LẤY DỮ LIỆU TỪ CACHE VÀ HIỂN THỊ NGAY LẬP TỨC
        loadDataFromCache()

        // 2. NÚT HỌC LẠI: Hủy cache và quay về
        binding.btnRetry.setOnClickListener {
            sharedViewModel.clearCache()
            findNavController().popBackStack()
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
                    Toast.makeText(requireContext(), "Bản ghi không tồn tại hoặc đã bị xóa", Toast.LENGTH_SHORT).show()
                }
            }
            binding.paragraphList.addView(card)
        }
    }

    private fun saveCacheToDatabase() {
        // Hiện loading ở đây...
        val cacheData = sharedViewModel.sessionCache.values.toList()

        // Gọi ViewModel để gọi API (Gửi List các ParagraphCacheItem lên backend)
        // Lưu ý: File audio path hiện tại là file local. Bạn cần đảm bảo Backend của bạn
        // hỗ trợ nhận file multipart, hoặc bạn phải gọi API upload file lấy URL trước khi đẩy text lên.
        dailyResultViewModel.submitFinalResult(cacheData)
    }

    override fun observeData() {
        // Lắng nghe trạng thái upload từ dailyResultViewModel
        dailyResultViewModel.uploadStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                // Xóa cache sau khi lưu DB thành công
                sharedViewModel.clearCache()
                Toast.makeText(requireContext(), "Đã lưu kết quả thành công!", Toast.LENGTH_SHORT).show()
                // Quay về màn course
                findNavController().popBackStack(R.id.courseFragment, false)
            } else if (isSuccess == false) {
                Toast.makeText(requireContext(), "Lỗi khi lưu kết quả lên server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioManager.release()
    }
}