package com.home.lexa.ui.speaking.daily_result

import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentDailyResultBinding
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.domain.models.ParagraphWord

class DailyResultFragment : BaseFragment<FragmentDailyResultBinding>(FragmentDailyResultBinding::inflate) {

    private var speakingDayId = -1L

    override fun setupViews() {
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
        
        setupSummary()
        setupMockParagraphs()
        
        binding.btnRetry.setOnClickListener {
        }
        
        binding.btnBackToCourse.setOnClickListener {
        }
    }

    private fun setupSummary() {
        binding.progressRing.setProgress(78)
        binding.tvGoodCount.text = "253"
        binding.tvAcceptedCount.text = "39"
        binding.tvBadCount.text = "12"
    }

    private fun setupMockParagraphs() {
        // Mock data for Paragraph 1
        val paragraph1 = ParagraphResult(
            id = 1,
            order = "1",
            paragraph = listOf(
                ParagraphWord("This", "green"),
                ParagraphWord("is", "green"),
                ParagraphWord("the", "green"),
                ParagraphWord("first", "green"),
                ParagraphWord("paragraph", "yellow"),
                ParagraphWord("for", "green"),
                ParagraphWord("today.", "green"),
                ParagraphWord("Please", "green"),
                ParagraphWord("read", "green")
            ),
            audioUrl = "123",
            userUrl = "456"
        )

        // Mock data for Paragraph 2
        val paragraph2 = ParagraphResult(
            id = 2,
            order = "2",
            paragraph = listOf(
                ParagraphWord("This", "green"),
                ParagraphWord("is", "green"),
                ParagraphWord("the", "green"),
                ParagraphWord("first", "green"),
                ParagraphWord("paragraph", "yellow"),
                ParagraphWord("for", "green"),
                ParagraphWord("today.", "green"),
                ParagraphWord("Please", "green"),
                ParagraphWord("read", "green")
            ),
            audioUrl = "123",
            userUrl = "456"
        )


        binding.cardParagraph1.displayParagraph(paragraph1)
        binding.cardParagraph2.displayParagraph(paragraph2)
    }

    override fun observeData() {

    }
}