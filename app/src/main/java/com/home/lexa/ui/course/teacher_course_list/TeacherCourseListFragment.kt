package com.home.lexa.ui.course.teacher_course_list

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.home.lexa.databinding.FragmentStudentCourseListBinding
import com.home.lexa.R



import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentFavoriteLibraryBinding
import com.home.lexa.databinding.FragmentLoginBinding
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.course.student_course_list.CourseFilter
import com.home.lexa.ui.course.student_course_list.StudentCourseListAdapter
import com.home.lexa.ui.course.student_course_list.StudentCourseListModel
import com.home.lexa.ui.library.LibraryFragment
import com.home.lexa.ui.library.personal_library.PersonalLibraryAdapter
import com.home.lexa.ui.library.personal_library.PersonalLibraryModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TeacherCourseListFragment : BaseFragment<FragmentStudentCourseListBinding>(FragmentStudentCourseListBinding::inflate) {
    private val viewModel: StudentCourseListModel by viewModel()
    private val courseAdapter by lazy {
        StudentCourseListAdapter(emptyList())
        { course ->
            val bundle = Bundle().apply {
                putLong("courseId", course.id)
            }
            findNavController().navigate(R.id.action_courseFragment_to_courseDetailFragment, bundle)
        }
    }

    private fun updateFilterUI(filter: CourseFilter) {

        fun setActive(btn: Button) {
            btn.setBackgroundResource(R.drawable.bg_filter_active)
            btn.setTextColor(resources.getColor(R.color.white, null))
        }

        fun setInactive(btn: Button) {
            btn.setBackgroundResource(R.drawable.bg_filter_inactive)
            btn.setTextColor(resources.getColor(R.color.black, null))
        }

        setInactive(binding.btnAll)
        setInactive(binding.btnFavorite)
        setInactive(binding.btnLearning)

        when (filter) {
            CourseFilter.ALL -> setActive(binding.btnAll)
            CourseFilter.FAVORITE -> setActive(binding.btnFavorite)
            CourseFilter.LEARNING -> setActive(binding.btnLearning)
        }
    }

    override fun setupViews() {
        binding.headerSection.setHeaderData(
            title = "Tất cả khoá học",
            actionText = "4 tất cả",
            onActionClick = {}
        )



        binding.rvCourses.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.btnAll.setOnClickListener {
            viewModel.changeFilter(CourseFilter.ALL)
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.changeFilter(CourseFilter.FAVORITE)
        }

        binding.btnLearning.setOnClickListener {
            Log.d("LEARNING", "123");
            viewModel.changeFilter(CourseFilter.LEARNING)
        }

        viewModel.fetchAllCourses()
    }

    override fun observeData() {
        viewModel.courses.observe(viewLifecycleOwner) { list ->
            courseAdapter.updateData(list)

            val filter = viewModel.currentFilter.value ?: CourseFilter.ALL

            val title = when (filter) {
                CourseFilter.ALL -> "Tất cả khoá học"
                CourseFilter.FAVORITE -> "Khoá học yêu thích của tôi"
                CourseFilter.LEARNING -> "Khoá học đang học"
            }



            binding.headerSection.setHeaderData(
                title = title,
                actionText = "${list.size} tất cả",
                onActionClick = {}
            )
        }
        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            updateFilterUI(filter)
        }
    }
}