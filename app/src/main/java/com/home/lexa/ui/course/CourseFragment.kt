package com.home.lexa.ui.course

import androidx.fragment.app.commit
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentCourseBinding
import com.home.lexa.ui.course.student_course_list.StudentCourseListFragment
import com.home.lexa.ui.course.teacher_course_list.TeacherCourseListFragment
import org.koin.android.ext.android.inject
import kotlin.getValue
import com.home.lexa.domain.models.UserRole

class CourseFragment : BaseFragment<FragmentCourseBinding>(FragmentCourseBinding::inflate) {

    private val userManager: UserManager by inject()
    private val isTeacher: Boolean get() = userManager.getUserRole() == UserRole.TEACHER
    override fun setupViews() {
        val existingFragment = childFragmentManager.findFragmentById(binding.courseContainer.id)
        if (existingFragment == null) {
            val fragment = if (isTeacher) TeacherCourseListFragment() else StudentCourseListFragment()
            childFragmentManager.commit {
                replace(binding.courseContainer.id, fragment)
            }
        }
    }

    override fun observeData() {

    }
}