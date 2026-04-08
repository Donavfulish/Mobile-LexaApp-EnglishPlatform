package com.home.lexa.ui.course.teacher_course_list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.home.lexa.databinding.FragmentStudentCourseListBinding
import com.home.lexa.R



import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentFavoriteLibraryBinding
import com.home.lexa.databinding.FragmentLoginBinding
import com.home.lexa.databinding.FragmentTeacherCourseListBinding
import com.home.lexa.domain.models.SearchInfo
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
        }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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

        binding.btnAll.setOnClickListener {
            viewModel.changeFilter(
                TeacherCourseFilter.ALL,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        binding.btnMyCourse.setOnClickListener {
            viewModel.changeFilter(TeacherCourseFilter.MYCOURSE,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.changeFilter(TeacherCourseFilter.FAVORITE,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        binding.btnLearning.setOnClickListener {
            Log.d("LEARNING", "123");
            viewModel.changeFilter(TeacherCourseFilter.LEARNING,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        if (viewModel.courses.value.isNullOrEmpty()) {
            viewModel.fetchAllCourses(
                isLoadMore = false,
                searchInfo = SearchInfo(query = "", limit = 10),
                nextCursor = null
            )
        }

        binding.addBtn.setOnClickAction {
            findNavController().navigate(
                R.id.action_courseFragment_to_courseDetailFragment)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvCourses.apply {
            adapter = courseAdapter
            this.layoutManager = layoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // dy > 0 nghĩa là đang lướt xuống
                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        val threshold = 3
                        if (!viewModel.isLoading.value!! && !viewModel.isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)) {

                                viewModel.fetchAllCourses(
                                    isLoadMore = true,
                                    searchInfo = SearchInfo(limit = 10),
                                    nextCursor = viewModel.lastId
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAllCourses();
    }
    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

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
                actionText = "${viewModel.totalPages} tất cả",
                onActionClick = {}
            )
        }
        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            updateFilterUI(filter)
        }
    }
}