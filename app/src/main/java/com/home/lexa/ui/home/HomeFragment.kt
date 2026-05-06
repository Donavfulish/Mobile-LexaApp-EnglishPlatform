package com.home.lexa.ui.home


import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import java.util.Calendar
import kotlin.getValue
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val userManager: UserManager by inject()
    private val isTeacher: Boolean get() = userManager.getUserRole() == UserRole.TEACHER
    private val viewModel: HomeViewModel by viewModel()

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
        if (!isTeacher) {
            loadStudyTimeData()
        }
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
                    binding.tvFirstNumber.text = stats.studentCount.toString()
                    binding.tvSecondNumber.text = stats.favoriteCount.toString()

                }else{
                    binding.tvFirstNumber.text = getString(R.string.hours_short, stats.weeklyHours)
                    binding.tvSecondNumber.text = getString(R.string.hours_short, stats.monthlyHours)
                }
            }
        }
    }
    private fun loadStudyTimeData() {
        if (!hasUsagePermission(requireContext())) {
            viewModel.updateStudyTime(0f, 0f)
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val (weeklyHours, monthlyHours) = withContext(Dispatchers.IO) {
                val weekStats = getUsageTime(getStartOfWeek(), System.currentTimeMillis())
                val monthStats = getUsageTime(getStartOfMonth(), System.currentTimeMillis())
                Pair(weekStats, monthStats)
            }

            // Làm tròn đến 1 chữ số thập phân (VD: 1.5 giờ)
            val formatWeek = (weeklyHours * 10f).roundToInt() / 10f
            val formatMonth = (monthlyHours * 10f).roundToInt() / 10f

            viewModel.updateStudyTime(formatWeek, formatMonth)
        }
    }

    private fun getUsageTime(startTime: Long, endTime: Long): Float {
        val usageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val totalTimeMillis = stats.filter { it.packageName == requireContext().packageName }
            .sumOf { it.totalTimeInForeground }

        // Chuyển đổi từ mili-giây sang giờ
        return totalTimeMillis / (1000f * 60f * 60f)
    }

    private fun getStartOfWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun hasUsagePermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Dùng cho Android 10 trở lên
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            // Dùng cho các bản Android cũ hơn (dưới Android 10)
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
