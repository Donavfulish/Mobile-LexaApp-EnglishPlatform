package com.home.lexa.ui.speaking.speaking_practice

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentSpeakingPracticeBinding
import com.home.lexa.ui.components.ParagraphEditCard
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakingPracticeFragment : BaseFragment<FragmentSpeakingPracticeBinding>(FragmentSpeakingPracticeBinding::inflate) {
    private val viewModel: SpeakingPracticeViewModel by viewModel()
    private var speakingDayId = -1L

    override fun setupViews() {
        speakingDayId = arguments?.getLong("speakingDayId") ?: -1L
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

        viewModel.paragraphDetailData.observe(viewLifecycleOwner) { data ->
            data?.let { detail ->
                binding.paragraphTitle.text = detail.title ?: ""
                binding.paragraphInput.setText(detail.title ?: "")

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
                    binding.paragraphLayout.addView(paragraphCard)
                }
            }
        }
    }

}
