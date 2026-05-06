package com.home.lexa.ui.speaking.speaking_practice

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSpeakingPracticeStudentBinding
import com.home.lexa.domain.models.ShortParagraphDto
import com.home.lexa.ui.utils.AudioManager
import com.home.lexa.ui.utils.SpeechEvaluator
import com.home.lexa.ui.utils.SpeechToTextManager
import android.app.AlertDialog
import android.text.SpannableString
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import com.home.lexa.ui.utils.StringUtils
import com.home.lexa.ui.utils.TTSManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakingPracticeStudentFragment : BaseFragment<FragmentSpeakingPracticeStudentBinding>(FragmentSpeakingPracticeStudentBinding::inflate) {

    private val viewModel: SpeakingPracticeStudentViewModel by viewModel()
    private val sharedViewModel: PracticeSharedViewModel by sharedViewModel()

    private var speakingDayId = -1L
    private var courseId = -1L
    private var paragraphs: List<ShortParagraphDto> = emptyList()
    private var currentIndex = 0
    private var isRecording = false

    private lateinit var audioManager: AudioManager
    private lateinit var sttManager: SpeechToTextManager

    private var currentAudioPath: String? = null
    private var currentRecognizedText: String = ""
    private val recordedAudios = mutableMapOf<Int, String>()
    private var userTriggeredStop = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecording()
        } else {
            Toast.makeText(requireContext(), getString(R.string.recording_permission_msg), Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreRecordedAudios() {
        if (paragraphs.isEmpty() || speakingDayId == -1L) return

        paragraphs.forEachIndexed { index, _ ->
            val fileName = "record_day${speakingDayId}_idx$index.mp3"
            val file = java.io.File(requireContext().filesDir, fileName)

            if (file.exists()) {
                recordedAudios[index] = file.absolutePath
                Log.d("RESTORE", "Tìm thấy file cũ cho đoạn $index: ${file.absolutePath}")
            }
        }
    }

    override fun setupViews() {
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L

        audioManager = AudioManager(requireContext())
        sttManager = SpeechToTextManager(requireContext())

        setupControls()

        if (speakingDayId != -1L) {
            viewModel.loadParagraphList(speakingDayId)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        binding.btnNext.setOnClickAction {
            if (!recordedAudios.containsKey(currentIndex)) {
                Toast.makeText(requireContext(), getString(R.string.please_record_first), Toast.LENGTH_SHORT).show()
                return@setOnClickAction
            }

            if (currentIndex < paragraphs.size - 1) {
                currentIndex++
                updateContent()
            } else {
                val bundle = Bundle().apply {
                    putLong("speakingDayId", speakingDayId)
                }
                findNavController().navigate(R.id.action_speakingPracticeStudentFragment_to_dailyResultFragment, bundle)
            }
        }

        binding.btnPrev.setOnClickAction {
            if (currentIndex > 0) {
                currentIndex--
                updateContent()
            }
        }

        binding.btnRecord.setOnClickAction {
            handleRecordingAction()
        }

        binding.btnNgheLai.setOnClickListener {
            playRecordedAudio()
        }

        binding.btnNgheMau.setOnClickAction {
            val text = paragraphs.getOrNull(currentIndex)?.paragraph ?: ""
            TTSManager.speak(text)
        }

        binding.btnRecord.isEnabled = false
        audioManager.voskSTTManager.initModel {
            // Callback này chạy khi model đã sẵn sàng
            requireActivity().runOnUiThread {
                binding.btnRecord.isEnabled = true
                Log.d("LexaApp", "Vosk Model loaded successfully!")
            }
        }
    }

    private fun handleRecordingAction() {
        if (!isRecording) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {
        audioManager.resetMediaPlayer()
        val fileName = "record_day${speakingDayId}_idx$currentIndex"
        userTriggeredStop = false
        currentRecognizedText = ""

        // Khởi động song song: AudioRecord (layer thấp) + STT (layer cao)
        // VOICE_RECOGNITION source được thiết kế cho trường hợp này
        currentAudioPath = audioManager.startRecording(fileName)

//        sttManager.startListening(
//            onResult = { text ->
//                currentRecognizedText = text
//                if (!userTriggeredStop) {
//                    stopRecording()
//                }
//            },
//            onError = { errorMsg ->
//                Log.e("STT_ERROR", errorMsg)
//                if (!userTriggeredStop) {
//                    stopRecording()
//                }
//            }
//        )

        isRecording = true
        binding.btnRecord.setBackground(ContextCompat.getColor(requireContext(), R.color.recording_active_bg))
        binding.tvInstruction.text = getString(R.string.recording_msg)
    }

    private fun stopRecording() {
        if (!isRecording) return
        userTriggeredStop = true
        isRecording = false

//        sttManager.stopListening()
        binding.btnRecord.setBackground(ContextCompat.getColor(requireContext(), R.color.btn_primary_bg))
        binding.tvInstruction.text = getString(R.string.analyzing_voice)

        // Dừng AudioRecord và chờ file WAV flush xong rồi mới xử lý
        audioManager.stopRecording({
            // Callback này chạy trên Main thread sau khi file đã lưu xong
            processAndSaveResult()
        })
    }

    private fun processAndSaveResult() {
        audioManager.voskSTTManager.apply {
            currentRecognizedText = getFinalResult()
        }

        println("currentRecognizedText = $currentRecognizedText")

        if (currentRecognizedText.isBlank()) {
            activity?.runOnUiThread {
                binding.tvInstruction.text = getString(R.string.mic_not_clear)
            }
            return
        }

        val currentParagraph = paragraphs[currentIndex]
        val originalText = currentParagraph.paragraph ?: ""
        val evaluationResults = SpeechEvaluator.evaluate(originalText, currentRecognizedText)

        currentAudioPath?.let { path ->
            recordedAudios[currentIndex] = path
            sharedViewModel.saveParagraphToCache(
                index = currentIndex,
                paragraphId = currentParagraph.id,
                originalText = originalText,
                evaluationResults = evaluationResults,
                audioPath = path
            )
        }

        // Chạy trên MainThread để cập nhật UI
        activity?.runOnUiThread {
            updateContent()
            binding.tvInstruction.text = getString(R.string.recorded_msg)
        }
    }

    private fun playRecordedAudio() {
        val path = recordedAudios[currentIndex]
        if (path == null) {
            Toast.makeText(requireContext(), getString(R.string.not_recorded_yet), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), getString(R.string.playing_recording), Toast.LENGTH_SHORT).show()
        audioManager.playAudio(path)
    }

    private fun updateNavigationButtons(hasRecording: Boolean) {
        binding.btnPrev.alpha = if (currentIndex == 0) 0.3f else 1.0f
        if (currentIndex < paragraphs.size - 1) {
            binding.btnNext.apply {
                setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
                alpha = if (hasRecording) 1.0f else 0.3f
            }
        } else {
            binding.btnNext.apply {
                setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!)
                alpha = if (hasRecording) 1.0f else 0.3f
            }
        }
    }

    private fun updateContent() {
        if (paragraphs.isEmpty()) return

        val currentParagraph = paragraphs[currentIndex]
        val originalText = currentParagraph.paragraph ?: ""
        val cacheItem = sharedViewModel.sessionCache[currentIndex]

        // 1. SET CỠ CHỮ
        val charCount = originalText.length
        val newTextSizeSp = when {
            charCount <= 50 -> 22f
            charCount <= 100 -> 20f
            charCount <= 150 -> 18f
            charCount <= 200 -> 16f
            else -> 14f
        }
        binding.tvParagraphContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSizeSp)

        // 2. LOGIC TÔ MÀU SPANNABLE THÔNG MINH
        if (cacheItem != null && cacheItem.paragraphId == currentParagraph.id) {
            val spannable = SpannableString(originalText)

            val colorSuccess = ContextCompat.getColor(requireContext(), R.color.status_success)
            val colorWarning = ContextCompat.getColor(requireContext(), R.color.status_warning)
            val colorError = ContextCompat.getColor(requireContext(), R.color.status_error_alt)

            // BƯỚC 1: Tô màu Xanh cho toàn bộ text (Dấu câu và khoảng trắng sẽ nhận màu này)
            spannable.setSpan(
                ForegroundColorSpan(colorSuccess),
                0, originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Regex lấy các từ (Bao gồm chữ, số và dấu nháy đơn cho các từ như "don't")
            val wordRegex = Regex("[a-zA-Z0-9-'’]+")
            val originalWords = wordRegex.findAll(originalText).toList()

            // BƯỚC 2: Phủ màu ĐỎ lên tất cả các TỪ (Mặc định coi là đọc sai hoặc bỏ sót)
            originalWords.forEach { match ->
                spannable.setSpan(
                    ForegroundColorSpan(colorError),
                    match.range.first, match.range.last + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // BƯỚC 3: Dò kết quả API với văn bản gốc bằng 1 con trỏ
            var pointerOriginal = 0

            cacheItem.evaluationResults.forEach { evalItem ->
                // Làm sạch từ của API (xóa các ký tự đặc biệt thừa nếu có)
                val evalClean = evalItem.word.lowercase()
                    .replace('’', '\'')
                    .replace(Regex("[^a-zA-Z0-9-']"), "")
                if (evalClean.isEmpty()) return@forEach

                // Tìm kiếm tuần tự trong các từ gốc chưa được duyệt
                for (i in pointerOriginal until originalWords.size) {
                    val originalMatch = originalWords[i]
                    // Làm sạch từ gốc để so sánh chính xác (Bỏ qua hoa thường, ngoặc kép...)
                    val originalClean = originalMatch.value.lowercase()
                        .replace('’', '\'')
                        .replace(Regex("[^a-zA-Z0-9-']"), "")

                    if (evalClean == originalClean) {
                        // ĐÃ MATCH! Quyết định màu dựa trên API
                        val correctColor = when (evalItem.status) {
                            "GOOD", "Correct" -> colorSuccess
                            "MEDIUM" -> colorWarning
                            else -> colorError
                        }

                        // Ghi đè màu mới lên khoảng vị trí của từ này
                        spannable.setSpan(
                            ForegroundColorSpan(correctColor),
                            originalMatch.range.first, originalMatch.range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Đẩy con trỏ lên vị trí TỪ TIẾP THEO để không xét lại từ cũ và các từ đã bị nhảy cóc
                        pointerOriginal = i + 1
                        break
                    }
                }
            }

            binding.tvParagraphContent.text = spannable
        } else {
            // Trạng thái chưa có bản ghi
            binding.tvParagraphContent.text = originalText
            binding.tvParagraphContent.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }

        // 3. Cập nhật tiến độ & nút bấm
        val progress = (currentIndex.toFloat() / paragraphs.size * 100).toInt()
        binding.apply {
            tvProgressTitle.text = getString(R.string.paragraph_progress_count, currentIndex + 1, paragraphs.size)
            progressBar.setProgress(progress)
            tvCompletedPercent.text = getString(R.string.completed_percent, progress)
        }

        val hasRecording = recordedAudios.containsKey(currentIndex) || cacheItem != null
        updateNavigationButtons(hasRecording)
    }

    override fun observeData() {
        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                paragraphs = data.list_paragraphs.sortedBy { it.paragraph_order }
                restoreRecordedAudios()
                checkAndShowContinueDialog()
            }
        }

        // Lắng nghe kết quả nếu user chọn "Lưu tiến độ và thoát"
        viewModel.bulkSaveStatus.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                result.onSuccess { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(requireContext(), getString(R.string.progress_saved), Toast.LENGTH_SHORT).show()
                        viewModel.resetBulkSaveStatus() // Reset state để tránh trigger lại
                        findNavController().popBackStack()
                    }
                }.onFailure { error ->
                    Toast.makeText(requireContext(), getString(R.string.save_error_msg, error.message), Toast.LENGTH_SHORT).show()
                    viewModel.resetBulkSaveStatus()
                }
            }
        }
    }

    private fun checkAndShowContinueDialog() {
        // Tùy vào cách backend trả data, giả sử ta biết user đã làm đến câu index thứ N:
        // (Ở đây giả lập tìm index đầu tiên chưa có thu âm)
        val lastCompletedIndex = recordedAudios.keys.maxOrNull() ?: -1

        if (lastCompletedIndex >= 0 && lastCompletedIndex < paragraphs.size - 1) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.continue_lesson_title))
                .setMessage(getString(R.string.continue_lesson_msg))
                .setPositiveButton(getString(R.string.continue_action)) { _, _ ->
                    currentIndex = lastCompletedIndex + 1
                    updateContent()
                }
                .setNegativeButton(getString(R.string.start_over)) { _, _ ->
                    currentIndex = 0
                    recordedAudios.clear()
                    sharedViewModel.clearCache()
                    // Gửi request xoá tiến độ cũ lên server nếu backend yêu cầu
                    updateContent()
                }
                .setCancelable(false)
                .show()
        } else {
            updateContent()
        }
    }

    private fun setupControls() {
        binding.btnRecord.apply {
            setSize(80); setIconSize(32)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mic)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.btn_primary_bg));
            setIconTint(ContextCompat.getColor(requireContext(), R.color.icon_tint_inverse))
        }
        binding.btnPrev.apply {
            setSize(56); setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.control_bg));
            setIconTint(ContextCompat.getColor(requireContext(), R.color.text_tertiary))
        }
        binding.btnNext.apply {
            setSize(56); setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
            setBackground(ContextCompat.getColor(requireContext(), R.color.control_bg));
            setIconTint(ContextCompat.getColor(requireContext(), R.color.text_tertiary))
        }
        binding.btnNgheMau.apply {
            setIconSize(40); setIconColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)!!)
            setText(getString(R.string.listen_sample), ContextCompat.getColor(requireContext(), R.color.brand_primary))
            setBackground(ContextCompat.getColor(requireContext(), R.color.control_bg))
        }
        binding.progressBar.setTitle(getString(R.string.lesson_progress_upper))
    }

    private fun showExitDialog() {
        val options = arrayOf(
            getString(R.string.save_progress_action),
            getString(R.string.exit_without_saving),
            getString(R.string.cancel)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.pause_lesson_title))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Lưu tiến độ: Gọi ViewModel để đẩy cache lên server, sau đó thoát
                        saveProgressAndExit()
                    }
                    1 -> {
                        // Thoát không lưu: Xoá cache hiện tại và thoát
                        sharedViewModel.clearCache()
                        findNavController().popBackStack()
                    }
                    2 -> {
                        // Hủy: Tắt dialog, tiếp tục học
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun saveProgressAndExit() {
        val cacheData = sharedViewModel.sessionCache.values.toList()
        if (cacheData.isEmpty()) {
            findNavController().popBackStack()
            return
        }

        viewModel.submitBulkProgress(speakingDayId, cacheData)

    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
        sttManager.destroy()
    }
}