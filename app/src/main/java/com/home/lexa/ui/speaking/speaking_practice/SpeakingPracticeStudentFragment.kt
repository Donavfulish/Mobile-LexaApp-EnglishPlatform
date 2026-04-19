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
            Toast.makeText(requireContext(), "Cần quyền ghi âm để thực hiện bài tập", Toast.LENGTH_SHORT).show()
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

        binding.btnNext.setOnClickAction {
            if (!recordedAudios.containsKey(currentIndex)) {
                Toast.makeText(requireContext(), "Vui lòng ghi âm trước khi tiếp tục", Toast.LENGTH_SHORT).show()
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
        // Luôn giải phóng player nếu đang phát âm thanh mà bấm ghi âm
        audioManager.resetMediaPlayer()
        val fileName = "record_day${speakingDayId}_idx$currentIndex"

        userTriggeredStop = false // Reset cờ
        currentRecognizedText = ""

        // 1. Khởi động STT trước
        sttManager.startListening(
            onResult = { text ->
                currentRecognizedText = text
                // Nếu STT tự nhận diện xong (user ngừng nói lâu)
                if (!userTriggeredStop) {
                    stopRecording()
                }
                processAndSaveResult()
            },
            onError = { errorMsg ->
                Log.e("STT_ERROR", errorMsg)
                // Chỉ xử lý kết quả lỗi nếu nó không phải do ta chủ động cancel
                if (!userTriggeredStop) {
                    processAndSaveResult()
                }
            }
        )

        // 2. Delay một chút rồi mới chạy MediaRecorder để tránh xung đột Mic (Error 5/9)
        binding.root.postDelayed({
            if (isRecording) { // Chỉ chạy nếu user chưa bấm dừng ngay
                currentAudioPath = audioManager.startRecording(fileName)
                if (currentAudioPath == null) {
                    Log.e("AUDIO", "MediaRecorder failed to start")
                }
            }
        }, 400)

        isRecording = true
        binding.btnRecord.setBackground(Color.RED)
        binding.tvInstruction.text = "Đang ghi âm... Nhấn lại để dừng"
    }

    private fun stopRecording() {
        if (!isRecording) return

        userTriggeredStop = true // Đánh dấu user chủ động dừng
        isRecording = false

        audioManager.stopRecording()
        sttManager.stopListening()
        binding.btnRecord.setBackground(Color.parseColor("#636AE8"))
        binding.tvInstruction.text = "Đang phân tích giọng nói..."

    }

    private fun processAndSaveResult() {
        if (currentRecognizedText.isBlank()) {
            activity?.runOnUiThread {
                binding.tvInstruction.text = "Không nghe rõ, vui lòng thử lại!"
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
            binding.tvInstruction.text = "Đã ghi nhận! Nhấn vào micro để nói lại"
        }
    }

    private fun playRecordedAudio() {
        val path = recordedAudios[currentIndex]
        if (path == null) {
            Toast.makeText(requireContext(), "Bạn chưa ghi âm đoạn này", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Đang phát ghi âm lại", Toast.LENGTH_SHORT).show()
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
        val cacheItem = sharedViewModel.sessionCache[currentIndex]

        // LOGIC TÔ MÀU SPANNABLE
        if (cacheItem != null && cacheItem.paragraphId == currentParagraph.id) {
            val builder = SpannableStringBuilder()
            cacheItem.evaluationResults.forEach { item ->
                val start = builder.length
                builder.append(item.word).append(" ")
                val end = builder.length - 1

                val color = when (item.status) {
                    "GOOD" -> Color.parseColor("#4CAF50")
                    "MEDIUM" -> Color.parseColor("#FFB300")
                    else -> Color.parseColor("#F44336")
                }

                builder.setSpan(
                    ForegroundColorSpan(color),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.tvParagraphContent.text = builder
        } else {
            binding.tvParagraphContent.text = currentParagraph.paragraph
            binding.tvParagraphContent.setTextColor(Color.parseColor("#202124"))
        }

        // Cập nhật tiến độ & nút bấm
        val hasRecording = recordedAudios.containsKey(currentIndex) || cacheItem != null
        binding.tvProgressTitle.text = "Đoạn văn ${currentIndex + 1}/${paragraphs.size}"
        val progress = ((currentIndex + 1).toFloat() / paragraphs.size * 100).toInt()
        binding.progressBar.setProgress(progress)
        updateNavigationButtons(hasRecording)
    }

    override fun observeData() {
        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                paragraphs = data.list_paragraphs.sortedBy { it.paragraph_order }
                restoreRecordedAudios()
                updateContent()
            }
        }
    }

    private fun setupControls() {
        binding.btnRecord.apply {
            setSize(80); setIconSize(32)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mic)!!)
            setBackground(Color.parseColor("#636AE8")); setIconTint(Color.WHITE)
        }
        binding.btnPrev.apply {
            setSize(56); setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)!!)
            setBackground(Color.parseColor("#F5F5F5")); setIconTint(Color.parseColor("#757575"))
        }
        binding.btnNext.apply {
            setSize(56); setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
            setBackground(Color.parseColor("#F5F5F5")); setIconTint(Color.parseColor("#757575"))
        }
        binding.btnNgheMau.apply {
            setIconSize(40); setIconColor(Color.parseColor("#636AE8"))
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)!!)
            setText("Nghe mẫu", Color.parseColor("#636AE8"))
            setBackground(Color.parseColor("#F5F5F5"))
        }
        binding.progressBar.setTitle("TIẾN ĐỘ BÀI HỌC")
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
        sttManager.destroy()
    }
}