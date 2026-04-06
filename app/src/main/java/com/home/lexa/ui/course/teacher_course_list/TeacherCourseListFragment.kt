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
import com.home.lexa.databinding.FragmentTeacherCourseListBinding
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.ui.course.student_course_list.CourseFilter
import com.home.lexa.ui.course.student_course_list.StudentCourseListAdapter
import com.home.lexa.ui.course.student_course_list.StudentCourseListModel
import com.home.lexa.ui.library.LibraryFragment
import com.home.lexa.ui.library.personal_library.PersonalLibraryAdapter
import com.home.lexa.ui.library.personal_library.PersonalLibraryModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TeacherCourseListFragment : BaseFragment<FragmentTeacherCourseListBinding>(FragmentTeacherCourseListBinding::inflate) {
        private val viewModel: TeacherCourseListModel by viewModel()
    private val courseAdapter by lazy {
        StudentCourseListAdapter(emptyList())
        { course ->
            val bundle = Bundle().apply {
                putLong("courseId", course.id)
            }
            findNavController().navigate(R.id.action_courseFragment_to_courseDetailFragment, bundle)
        }
    }

    private fun updateFilterUI(filter: TeacherCourseFilter) {

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
        setInactive(binding.btnMyCourse)

        when (filter) {
            TeacherCourseFilter.MYCOURSE -> setActive(binding.btnMyCourse)
            TeacherCourseFilter.ALL -> setActive(binding.btnAll)
            TeacherCourseFilter.FAVORITE -> setActive(binding.btnFavorite)
            TeacherCourseFilter.LEARNING -> setActive(binding.btnLearning)
        }
    }

    override fun setupViews() {
        binding.headerSection.setHeaderData(
            title = "Khoá học của tôi",
            actionText = "4 tất cả",
            onActionClick = {}
        )



        binding.rvCourses.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.btnAll.setOnClickListener {
            viewModel.changeFilter(TeacherCourseFilter.ALL)
        }

        binding.btnMyCourse.setOnClickListener {
            viewModel.changeFilter(TeacherCourseFilter.MYCOURSE)
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.changeFilter(TeacherCourseFilter.FAVORITE)
        }

        binding.btnLearning.setOnClickListener {
            Log.d("LEARNING", "123");
            viewModel.changeFilter(TeacherCourseFilter.LEARNING)
        }

        viewModel.fetchAllCourses()
    }

    override fun observeData() {
        viewModel.courses.observe(viewLifecycleOwner) { list ->
            courseAdapter.updateData(list)

            val filter = viewModel.currentFilter.value ?: TeacherCourseFilter.MYCOURSE

            val title = when (filter) {
                TeacherCourseFilter.MYCOURSE -> "Khoá học của tôi"
                TeacherCourseFilter.ALL -> "Tất cả khoá học"
                TeacherCourseFilter.FAVORITE -> "Khoá học yêu thích của tôi"
                TeacherCourseFilter.LEARNING -> "Khoá học đang học"
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