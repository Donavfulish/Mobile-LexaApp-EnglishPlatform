package com.home.lexa.ui.course.student_course_list

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentStudentCourseListBinding
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.StudentCourseFilter
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudentCourseListFragment : BaseFragment<FragmentStudentCourseListBinding>(FragmentStudentCourseListBinding::inflate) {
    private val viewModel: StudentCourseListModel by viewModel()
    private val courseAdapter by lazy {
        StudentCourseListAdapter(emptyList()) { course ->
            val bundle = Bundle().apply {
                putLong("courseId", course.id)
            }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun updateFilterUI(filter: StudentCourseFilter) {
        fun setActive(btn: MaterialButton) {
            btn.setBackgroundResource(R.drawable.bg_filter_active)
            btn.backgroundTintList = null
            btn.setTextColor(resources.getColor(R.color.white, null))
            btn.iconTint = ColorStateList.valueOf(resources.getColor(R.color.white, null))
        }

        fun setInactive(btn: MaterialButton) {
            btn.setBackgroundResource(R.drawable.bg_filter_inactive)
            btn.backgroundTintList = null
            btn.setTextColor(resources.getColor(R.color.black, null))
            btn.iconTint = ColorStateList.valueOf(resources.getColor(R.color.black, null))
        }

        setInactive(binding.btnAll)
        setInactive(binding.btnFavorite)
        setInactive(binding.btnLearning)
        setInactive(binding.btnFinished)

        when (filter) {
            StudentCourseFilter.ALL -> setActive(binding.btnAll)
            StudentCourseFilter.FAVORITE -> setActive(binding.btnFavorite)
            StudentCourseFilter.LEARNING -> setActive(binding.btnLearning)
            StudentCourseFilter.FINISHED -> setActive(binding.btnFinished)
        }
    }

    override fun setupViews() {
        binding.headerSection.setHeaderData(
            title = "Khoá học của tôi",
            actionText = "0 tất cả",
            onActionClick = {}
        )

        val filterArg = arguments?.getString("filter")
        val initialFilter = try {
            StudentCourseFilter.valueOf(filterArg ?: "ALL")
        } catch (e: Exception) {
            StudentCourseFilter.ALL
        }

        viewModel.changeFilter(initialFilter, SearchInfo(
            query= "",
            sortBy= "",
            order= "",
            limit = 10
        ),
            null)

        binding.rvCourses.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.btnAll.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.ALL)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
            viewModel.changeFilter(
                StudentCourseFilter.ALL,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        binding.btnFavorite.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.FAVORITE)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
            viewModel.changeFilter(StudentCourseFilter.FAVORITE,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        binding.btnLearning.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.LEARNING)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
            viewModel.changeFilter(StudentCourseFilter.LEARNING,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvCourses.apply {
            this.layoutManager = layoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        val threshold = 3
                        if (viewModel.isLoading.value == false && !viewModel.isLastPage) {
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
        viewModel.fetchAllCourses(
            isLoadMore = false,
            searchInfo = SearchInfo(query = "", limit = 10),
            nextCursor = null
        )
    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.courses.observe(viewLifecycleOwner) { shortCourse ->
            courseAdapter.updateData(shortCourse.data)

            val filter = shortCourse.status
            val title = when (filter) {
                is StudentCourseFilter -> {
                    when (filter) {
                        StudentCourseFilter.ALL -> "Tất cả khoá học"
                        StudentCourseFilter.FAVORITE -> "Khoá học yêu thích"
                        StudentCourseFilter.LEARNING -> "Khoá học đang học"
                        StudentCourseFilter.FINISHED -> "Khoá học đã hoàn thành"
                    }
                }
                else -> "Khoá học"
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
