package com.home.lexa.ui.home


import android.view.View
import androidx.lifecycle.lifecycleScope
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch
import com.home.lexa.ui.adapter.FeaturedCourseAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.home.lexa.ui.adapter.StudyingCourseAdapter

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private  val isTeacher = true;
    private val viewModel: HomeViewModel by viewModel()


    private val featuredCourseAdapter by lazy {
        FeaturedCourseAdapter(
            onCardClick = { course ->
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment)
            },
            onFavoriteToggle = { course, isFavorite ->

                // viewModel.toggleFavorite(course.id, isFavorite)
            },

        )
    }
    private val topStudiedCourseAdapter by lazy {
        FeaturedCourseAdapter(
            onCardClick = { course ->
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment)

            },
            onFavoriteToggle = { course, isFavorite ->
                // Gọi API lưu trạng thái yêu thích
                // viewModel.toggleFavorite(course.id, isFavorite)
            }

        )
    }
    private val studyingAdapter by lazy {
        StudyingCourseAdapter(
            onCardClick = { course ->
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment)

            }
        )
    }
    override fun setupViews() {
        binding.rvFeaturedCoursesCard.apply {
            adapter = featuredCourseAdapter
        }
        binding.rvTopStudiedCoursesCard.apply {
            adapter = topStudiedCourseAdapter
        }
        binding.rvStudyingCoursesCard.apply { adapter = studyingAdapter }

        binding.featuredCoursesHeader.setHeaderData("Khóa học nổi bật", iconRes = R.drawable.ic_star_outline,"Xem tất cả",{
            findNavController().navigate(R.id.action_homeFragment_to_courseFragment)

        })
        if(isTeacher){
            binding.tvTitleStats.text = "THÀNH TỰU DẠY"
            binding.tvFirstText.text = "Tổng số lượt học"
            binding.tvSecondText.text = "Lượt tải từ vựng"
            binding.commonCoursesHeader.setHeaderData("Nhiều lượt học nhất", R.drawable.ic_play_circle)
            binding.rvStudyingCoursesCard.visibility= View.GONE
            binding.rvTopStudiedCoursesCard.visibility= View.VISIBLE

        }else{
            binding.tvTitleStats.text = "THỜI GIAN HỌC"
            binding.tvFirstText.text = "Tuần này"
            binding.tvSecondText.text = "Tháng náy"
            binding.commonCoursesHeader.setHeaderData("Đang học", R.drawable.ic_play_circle)
            binding.rvStudyingCoursesCard.visibility= View.VISIBLE
            binding.rvTopStudiedCoursesCard.visibility= View.GONE

        }
        binding.myFavoriteHeader.setHeaderData("Yêu thích của bạn", iconRes = R.drawable.ic_outline_book_2)

        binding.cardVocabulary.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_libraryFragment)

        }
        binding.cardLessons.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_libraryFragment)

        }

    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.featuredCoursesFlow.collect { courses ->
                featuredCourseAdapter.submitList(courses)

            }
        }

          if(isTeacher){
              viewLifecycleOwner.lifecycleScope.launch {
                  viewModel.topStudiedCoursesFlow.collect { courses ->
                      topStudiedCourseAdapter.submitList(courses)

                  }
              }


          }else{
              viewLifecycleOwner.lifecycleScope.launch {
                  viewModel.studyingCoursesFlow.collect { courses ->
                      studyingAdapter.submitList(courses)
                  }
              }
          }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userStatsFlow.collect { stats ->
                binding.tvStreakDays.text = "${stats.streakDays} ngày"
                if(isTeacher){
                    binding.tvFirstNumber.text = "${stats.weeklyHours}"
                    binding.tvSecondNumber.text = "${stats.monthlyHours}"
                }else{

                    binding.tvFirstNumber.text = "${stats.weeklyHours} h"
                    binding.tvSecondNumber.text = "${stats.monthlyHours} h"
                }

            }
        }


    }
}