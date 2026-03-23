package com.home.lexa.ui.course



import androidx.fragment.app.commit
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentCourseBinding
import com.home.lexa.ui.course.student_course_list.StudentCourseListFragment
import com.home.lexa.ui.course.teacher_course_list.TeacherCourseListFragment

class CourseFragment : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {

    private val isTeacher = true // đổi theo role user
    override fun setupViews() {
        val fragment = if (isTeacher) {
            TeacherCourseListFragment()
        } else {
            StudentCourseListFragment()
        }

        childFragmentManager.commit {
            replace(binding.courseContainer.id, fragment)
        }
    }

    override fun observeData() {

    }
}