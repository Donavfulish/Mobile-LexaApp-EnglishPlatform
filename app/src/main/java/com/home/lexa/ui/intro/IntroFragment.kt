package com.home.lexa.ui.intro

import android.widget.Toast
import androidx.core.view.setMargins
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.repository.mockCourses
import com.home.lexa.data.repository.mockParagraphData
import com.home.lexa.data.repository.mockStudyingCourses
import com.home.lexa.databinding.FragmentIntroBinding
import com.home.lexa.ui.components.FeaturedCourseCard
import com.home.lexa.ui.components.ParagraphCard
import com.home.lexa.ui.components.StudyingCourseCard
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroFragment : BaseFragment<FragmentIntroBinding>(FragmentIntroBinding::inflate) {

    // Inject ViewModel bằng Koin (giữ nguyên)
    private val viewModel: IntroViewModel by viewModel()


    override fun setupViews() {
        // 1. Gắn sự kiện (Thay thế cho setupActions)

        val paragraphCard = ParagraphCard(requireContext()).apply {
            displayParagraph(mockParagraphData)
        }
        binding.root.addView(paragraphCard)

        mockCourses.forEach { course ->
            val courseCard = FeaturedCourseCard(requireContext()).apply {
                setData(course)

                setOnClickToggleFavoriteButton { isSelected ->
                    Toast.makeText(context, "${course.title} favorite: $isSelected", Toast.LENGTH_SHORT).show()
                }
                setOnClickTopic {
                    Toast.makeText(context, "${course.topic}", Toast.LENGTH_SHORT).show()
                }
            }
            binding.root.addView(courseCard)
        }

        mockStudyingCourses.forEach { course ->
            val studyCard = StudyingCourseCard(requireContext()).apply {
                setData(course)

                setOnClickTopic {
                    Toast.makeText(context, "Click Topic: ${course.topic}", Toast.LENGTH_SHORT).show()
                }
            }
            binding.root.addView(studyCard)
        }


        viewModel.loadIntro()
    }

    override fun observeData() {
        // Lưu ý: Trong Fragment, BẮT BUỘC dùng 'viewLifecycleOwner' thay cho 'this'
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            binding.btnNext.visibility = if (isLoading) View.GONE else View.VISIBLE
//        }
//
//        viewModel.introData.observe(viewLifecycleOwner) { data ->
//            binding.tvIntroTitle.text = data.title
//            binding.tvIntroDesc.text = data.description
//            binding.btnNext.setText(data.buttonText, null)
//        }
    }
}