package com.home.lexa.ui.home


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.home.lexa.MainActivity
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.UserRole
import org.koin.android.ext.android.inject
import kotlin.getValue

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val userManager: UserManager by inject()
    private val isTeacher: Boolean get() = userManager.getUserRole() == UserRole.TEACHER
    private val viewModel: HomeViewModel by viewModel()
    private val activityBinding by lazy { (requireActivity() as MainActivity).binding }

    private val featuredCourseAdapter by lazy {
        FeaturedCourseAdapter(
            onCardClick = { course ->
                val bundle = Bundle().apply {
                    putLong("courseId", course.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment,
                    bundle)
            },
            onFavoriteToggle = { course, isFavorite ->

                // viewModel.toggleFavorite(course.id, isFavorite)
            },

        )
    }
    private val topStudiedCourseAdapter by lazy {
        FeaturedCourseAdapter(
            onCardClick = { course ->
                val bundle = Bundle().apply {
                    putLong("courseId", course.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment,
                    bundle)
            },
            onFavoriteToggle = { course, isFavorite ->

                // viewModel.toggleFavorite(course.id, isFavorite)
            }

        )
    }
    private val studyingAdapter by lazy {
        StudyingCourseAdapter(
            onCardClick = { course ->
                val bundle = Bundle().apply {
                    putLong("courseId", course.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment,
                    bundle)
            }
        )
    }

    override fun setupViews() {
        userManager.updateStreak()

        Log.d("Gia tri streak", "${userManager.getStreakCount()}")
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
            findNavController().navigate(R.id.action_homeFragment_to_courseFragment)

        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFeaturedCourses()
        viewModel.fetchStudyingCourses()
        viewModel.fetchTopStudiedCourses()
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