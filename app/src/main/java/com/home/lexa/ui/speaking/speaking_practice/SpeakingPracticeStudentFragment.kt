package com.home.lexa.ui.speaking.speaking_practice

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSpeakingPracticeStudentBinding
import com.home.lexa.domain.models.ShortParagraphDto
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

class SpeakingPracticeStudentFragment : BaseFragment<FragmentSpeakingPracticeStudentBinding>(FragmentSpeakingPracticeStudentBinding::inflate) {
    private var speakingDayId = -1L
    private val viewModel: SpeakingPracticeStudentViewModel by viewModel()
    private var courseId = -1L
    
    private var paragraphs: List<ShortParagraphDto> = emptyList()
    private var currentIndex = 0
    private var isRecording = false

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentAudioPath: String? = null
    private val recordedAudios = mutableMapOf<Int, String>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecording()
        } else {
            Toast.makeText(requireContext(), "Cần quyền ghi âm để thực hiện bài tập", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setupViews(){
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
        
        setupControls()

        if (speakingDayId != -1L) {
            viewModel.loadParagraphList(speakingDayId)
        }
        
        binding.btnNext.setOnClickAction {
            // Kiểm tra xem đã ghi âm đoạn hiện tại chưa
            if (!recordedAudios.containsKey(currentIndex)) {
                Toast.makeText(requireContext(), "Vui lòng ghi âm trước khi tiếp tục", Toast.LENGTH_SHORT).show()
                return@setOnClickAction
            }

            if (currentIndex < paragraphs.size - 1) {
                currentIndex++
                updateContent()
            } else if (currentIndex == paragraphs.size - 1) {
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
        val fileName = "recording_${System.currentTimeMillis()}.mp3"
        val file = File(requireContext().cacheDir, fileName)
        currentAudioPath = file.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(currentAudioPath)
            try {
                prepare()
                start()
                isRecording = true
                binding.btnRecord.setBackground(Color.RED)
                binding.tvInstruction.text = "Đang ghi âm... Nhấn lại để dừng"
            } catch (e: IOException) {
                Log.e("AudioRecord", "prepare() failed")
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                Log.e("AudioRecord", "stop() failed")
            }
        }
        recorder = null
        isRecording = false
        binding.btnRecord.setBackground(Color.parseColor("#636AE8"))
        binding.tvInstruction.text = "Đã ghi nhận! Nhấn vào micro để nói lại"
        Toast.makeText(requireContext(), "Đã ghi âm thành công", Toast.LENGTH_SHORT).show()
        binding.tvParagraphContent.setTextColor(Color.parseColor("#4CAF50"))
        currentAudioPath?.let { path ->
            recordedAudios[currentIndex] = path
            val currentParagraph = paragraphs[currentIndex]
            Log.d("Gia tri path luu: ", path)
            viewModel.submitRecordingResult(currentParagraph.id, currentParagraph.paragraph ?: "", path)
        }
        
        // Cập nhật lại UI để kích hoạt nút Next
        updateContent()
    }

    private fun playRecordedAudio() {
        val path = recordedAudios[currentIndex]
        if (path == null) {
            Toast.makeText(requireContext(), "Bạn chưa ghi âm đoạn này", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Giá tri path record: ", path)
        Toast.makeText(requireContext(), "Đang phát ghi âm lại", Toast.LENGTH_SHORT).show()
        player?.stop()
        player?.release()
        
        try {
            player = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener { it.release() }
            }
        } catch (e: Exception) {
            Log.e("AudioPlay", "Lỗi phát âm thanh")
        }
    }

    private fun setupFinishButton() {
        binding.btnNext.apply {
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!)
            setBackground(Color.parseColor("#636AE8"))
            setIconTint(Color.WHITE)
            alpha = 1.0f
        }
    }

    private fun setupControls() {
        val grayColor = Color.parseColor("#757575")

        binding.btnRecord.apply {
            setSize(80)
            setIconSize(32)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mic)!!)
            setBackground(Color.parseColor("#636AE8"))
            setIconTint(Color.WHITE)
        }

        binding.btnPrev.apply {
            setSize(56)
            setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)!!)
            setBackground(Color.parseColor("#F5F5F5"))
            setIconTint(grayColor)
        }

        binding.btnNext.apply {
            setSize(56)
            setIconSize(24)
            setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
            setBackground(Color.parseColor("#F5F5F5"))
            setIconTint(grayColor)
        }
        
        binding.progressBar.setTitle("TIẾN ĐỘ BÀI HỌC")
    }

    private fun updateContent() {
        if (paragraphs.isEmpty()) return
        
        val hasRecording = recordedAudios.containsKey(currentIndex)
        
        // Nếu đã có bản ghi âm cho đoạn này thì hiện màu xanh, ngược lại hiện màu đen
        if (hasRecording) {
            binding.tvParagraphContent.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            binding.tvParagraphContent.setTextColor(Color.parseColor("#202124"))
        }
        
        val currentParagraph = paragraphs[currentIndex]
        binding.tvParagraphContent.text = currentParagraph.paragraph
        binding.tvProgressTitle.text = "Đoạn văn ${currentIndex + 1}/${paragraphs.size}"
        
        val progress = ((currentIndex + 1).toFloat() / paragraphs.size * 100).toInt()
        binding.progressBar.setProgress(progress)
        binding.tvCompletedPercent.text = "Đã hoàn thành $progress%"
        
        binding.btnPrev.alpha = if (currentIndex == 0) 0.3f else 1.0f
        
        if (currentIndex < paragraphs.size - 1) {
            binding.btnNext.apply {
                setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
                setBackground(Color.parseColor("#F5F5F5"))
                setIconTint(Color.parseColor("#757575"))
                // Chỉ hiện rõ nếu đã ghi âm, nếu chưa thì mờ đi
                alpha = if (hasRecording) 1.0f else 0.3f
            }
        } else {
            // Nếu là đoạn cuối
            if (hasRecording) {
                setupFinishButton()
            } else {
                binding.btnNext.apply {
                    setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)!!)
                    setBackground(Color.parseColor("#F5F5F5"))
                    setIconTint(Color.parseColor("#757575"))
                    alpha = 0.3f
                }
            }
        }
    }

    override fun observeData() {
        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                paragraphs = data.list_paragraphs.sortedBy { it.paragraph_order }
                currentIndex = 0
                updateContent()
            }
        }
        
        viewModel.updateParagraphResultStatus.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                viewModel.resetUpdateParagraphResultStatus()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder?.release()
        player?.release()
    }
}