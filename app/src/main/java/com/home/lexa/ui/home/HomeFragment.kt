package com.home.lexa.ui.home


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
                viewModel.toggleFavorite(course, isFavorite)
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
                Log.d("Nhieu luot hoc", "hoc")
                viewModel.toggleFavorite(course, isFavorite)
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

        binding.featuredCoursesHeader.setHeaderData(getString(R.string.featured_course), iconRes = R.drawable.ic_star_outline, getString(R.string.see_all), {
            findNavController().navigate(R.id.action_homeFragment_to_courseFragment)
        })
        if(isTeacher){
            binding.tvTitleStats.text = getString(R.string.teaching_achievements_upper)
            binding.tvFirstText.text = getString(R.string.  total_studied)
            binding.tvSecondText.text = getString(R.string.vocabulary_downloads)
            binding.commonCoursesHeader.setHeaderData(getString(R.string.most_studied), R.drawable.ic_play_circle)
            binding.rvStudyingCoursesCard.visibility= View.GONE
            binding.rvTopStudiedCoursesCard.visibility= View.VISIBLE

        }else{
            binding.tvTitleStats.text = getString(R.string.study_time_upper)
            binding.tvFirstText.text = getString(R.string.this_week)
            binding.tvSecondText.text = getString(R.string.this_month)
            binding.commonCoursesHeader.setHeaderData(getString(R.string.studying), R.drawable.ic_play_circle)
            binding.rvStudyingCoursesCard.visibility= View.VISIBLE
            binding.rvTopStudiedCoursesCard.visibility= View.GONE

        }
        binding.myFavoriteHeader.setHeaderData(getString(R.string.your_favorite), iconRes = R.drawable.ic_outline_book_2)

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessageFlow.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
                binding.tvStreakDays.text = getString(R.string.streak_days_count, stats.streakDays)
                if(isTeacher){
                    binding.tvFirstNumber.text = stats.weeklyHours.toString()
                    binding.tvSecondNumber.text = stats.monthlyHours.toString()
                }else{

                    binding.tvFirstNumber.text = getString(R.string.hours_short, stats.weeklyHours)
                    binding.tvSecondNumber.text = getString(R.string.hours_short, stats.monthlyHours)
                }
            }
        }
    }
}
