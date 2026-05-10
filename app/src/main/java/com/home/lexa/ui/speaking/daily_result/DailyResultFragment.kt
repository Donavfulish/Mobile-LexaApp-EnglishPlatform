package com.home.lexa.ui.speaking.daily_result

import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import coil.size.ViewSizeResolver
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentDailyResultBinding
import com.home.lexa.domain.models.DailyResultSummary
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.domain.models.ParagraphWord
import com.home.lexa.ui.components.ParagraphCard
import com.home.lexa.ui.speaking.speaking_practice.ParagraphCacheItem
import com.home.lexa.ui.speaking.speaking_practice.PracticeSharedViewModel
import com.home.lexa.ui.utils.AudioManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class DailyResultFragment : BaseFragment<FragmentDailyResultBinding>(FragmentDailyResultBinding::inflate) {

    private val sharedViewModel: PracticeSharedViewModel by sharedViewModel()

    private val dailyResultViewModel: DailyResultViewModel by viewModel()

    private lateinit var audioManager: AudioManager
    private var fromCompletedDay = false
    private var courseId = -1L
    private var speakingDayId = -1L
    private var expectingRemoteSummary = false

    override fun setupViews() {
        binding.layoutResultScreen.visibility = View.GONE

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.visibility = View.GONE

        audioManager = AudioManager(requireContext())
        fromCompletedDay = arguments?.getBoolean("fromCompletedDay", false) ?: false
        courseId = arguments?.getLong("courseId") ?: -1L
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L

        binding.progressSpinner.visibility = View.GONE

        sharedViewModel.bindSpeakingDay(speakingDayId, resetOnChange = true)

        resetSummaryPlaceholders()

        val cacheSnapshot = sharedViewModel.sessionCache.values.toList()
        expectingRemoteSummary = cacheSnapshot.isEmpty() && speakingDayId != -1L
        when {
            cacheSnapshot.isNotEmpty() -> summaryFromCache(cacheSnapshot)?.let(::displayDailySummary)
            speakingDayId != -1L -> dailyResultViewModel.loadDailyResult(speakingDayId)
            else -> Toast.makeText(requireContext(), getString(R.string.lesson_id_not_found), Toast.LENGTH_SHORT).show()
        }

        binding.btnRetry.setOnClickListener {
            sharedViewModel.clearCache()
            if ((fromCompletedDay || courseId != -1L) && speakingDayId != -1L && courseId != -1L) {
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

        binding.btnBackToCourse.setOnClickListener {
            if (sharedViewModel.sessionCache.isNotEmpty()) {
                saveCacheToDatabase()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun resetSummaryPlaceholders() {
        binding.progressRing.setProgress(0)
        binding.tvGoodCount.text = "0"
        binding.tvAcceptedCount.text = "0"
        binding.tvBadCount.text = "0"
        binding.paragraphList.removeAllViews()
    }

    private fun summaryFromCache(cacheData: List<ParagraphCacheItem>): DailyResultSummary? {
        if (cacheData.isEmpty()) return null
        val totalGood = cacheData.sumOf { it.goodCount }
        val totalAccepted = cacheData.sumOf { it.mediumCount }
        val totalBad = cacheData.sumOf { it.badCount }
        val paragraphs = cacheData.mapIndexed { index, cacheItem ->
            val uiWords = cacheItem.evaluationResults.map { eval ->
                val color = when (eval.status) {
                    "GOOD" -> "green"
                    "MEDIUM" -> "yellow"
                    "BAD" -> "red"
                    else -> "red"
                }
                ParagraphWord(eval.word, color)
            }
            ParagraphResult(
                id = cacheItem.paragraphId.toInt(),
                original = cacheItem.originalText,
                paragraph = uiWords,
                order = (index + 1).toString(),
                audioUrl = "",
                userUrl = cacheItem.localAudioPath
            )
        }
        return DailyResultSummary(totalGood, totalAccepted, totalBad, paragraphs)
    }

    private fun displayDailySummary(summary: DailyResultSummary) {
        val totalWords = summary.totalGood + summary.totalAccepted + summary.totalBad
        val progress = if (totalWords > 0) {
            ((summary.totalGood + summary.totalAccepted).toFloat() / totalWords * 100).toInt()
        } else 0

        binding.progressRing.setProgress(progress)
        binding.tvGoodCount.text = summary.totalGood.toString()
        binding.tvAcceptedCount.text = summary.totalAccepted.toString()
        binding.tvBadCount.text = summary.totalBad.toString()

        binding.paragraphList.removeAllViews()
        summary.paragraphs.forEach { paragraphResult ->
            val card = ParagraphCard(requireContext())
            val layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 48) }
            card.layoutParams = layoutParams
            card.displayParagraph(paragraphResult)
            attachUserReplayHandler(card, paragraphResult.userUrl)
            binding.paragraphList.addView(card)
        }
    }

    private fun attachUserReplayHandler(card: ParagraphCard, recordingSource: String) {
        card.setOnClickUserSound {
            when {
                recordingSource.isBlank() ->
                    Toast.makeText(requireContext(), getString(R.string.record_not_found), Toast.LENGTH_SHORT).show()
                recordingSource.startsWith("http", ignoreCase = true) ->
                    audioManager.playFromSource(recordingSource)
                File(recordingSource).exists() ->
                    audioManager.playAudio(recordingSource)
                else ->
                    Toast.makeText(requireContext(), getString(R.string.record_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCacheToDatabase() {
        val cacheData = sharedViewModel.sessionCache.values.toList()
        if (speakingDayId != -1L) {
            dailyResultViewModel.submitFinalResult(speakingDayId, cacheData)
        } else {
            Toast.makeText(requireContext(), getString(R.string.lesson_id_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
        dailyResultViewModel.uploadStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                sharedViewModel.clearCache()
                Toast.makeText(requireContext(), getString(R.string.save_result_success), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.courseFragment, false)
            } else if (isSuccess == false) {
                Toast.makeText(requireContext(), getString(R.string.save_result_error), Toast.LENGTH_SHORT).show()
            }
        }

        dailyResultViewModel.dailyResultData.observe(viewLifecycleOwner) { summary ->
            binding.layoutResultScreen.visibility = View.VISIBLE

            if (sharedViewModel.sessionCache.isEmpty() && summary != null) {
                displayDailySummary(summary)
                expectingRemoteSummary = false
                binding.progressSpinner.visibility = View.GONE
            }
        }

        dailyResultViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (expectingRemoteSummary) {
                binding.progressSpinner.visibility = if (loading == true) View.VISIBLE else View.GONE
                if (loading == false && sharedViewModel.sessionCache.isEmpty() && dailyResultViewModel.dailyResultData.value == null) {
                    Toast.makeText(requireContext(), getString(R.string.error_retry), Toast.LENGTH_SHORT).show()
                    expectingRemoteSummary = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioManager.release()
    }
}
