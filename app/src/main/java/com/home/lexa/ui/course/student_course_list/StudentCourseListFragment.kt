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
import com.home.lexa.domain.models.TeacherCourseFilter
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudentCourseListFragment : BaseFragment<FragmentStudentCourseListBinding>(FragmentStudentCourseListBinding::inflate) {
    private val viewModel: StudentCourseListModel by activityViewModel()
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
        binding.searchbarFilter.apply {
            onSearchAction { q ->
                viewModel.searchInfo = viewModel.searchInfo.copy(query = q)
                viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
            }
            onTextChanged { q ->
                if (q.isNotEmpty()) {
                    viewModel.getSuggestions(q)
                }
            }
            setOnSortOptionChanged { options ->
                viewModel.searchInfo = viewModel.searchInfo.copy(order = options.order, sortBy = options.sortBy)
                viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
            }
        }

        binding.headerSection.setHeaderData(
            title = getString(R.string.my_courses),
            actionText = getString(R.string.all_count, 0),
            onActionClick = {}
        )

        binding.btnAll.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.ALL)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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

        if (viewModel.courses.value == null || viewModel.courses.value?.data.isNullOrEmpty()) {
            val filterArg = arguments?.getString("filter")
            val initialFilter = try {
                StudentCourseFilter.valueOf(filterArg ?: "MYCOURSE")
            } catch (e: Exception) {
                StudentCourseFilter.ALL
            }
            viewModel.changeFilter(initialFilter, viewModel.searchInfo, null)
        } else {
            binding.searchbarFilter.setTextSearch(viewModel.searchInfo.query ?: "")
            viewModel.currentFilter.value?.let { updateFilterUI(it) }
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
                                    searchInfo = viewModel.searchInfo,
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
    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            binding.searchbarFilter.setSuggestions(suggestions)
        }

        viewModel.courses.observe(viewLifecycleOwner) { shortCourse ->
            courseAdapter.updateData(shortCourse.data)

            val filter = shortCourse.status
            val title = when (filter) {
                is StudentCourseFilter -> {
                    when (filter) {
                        StudentCourseFilter.ALL -> getString(R.string.all_courses)
                        StudentCourseFilter.FAVORITE -> getString(R.string.favorite_courses)
                        StudentCourseFilter.LEARNING -> getString(R.string.learning_courses)
                        StudentCourseFilter.FINISHED -> getString(R.string.finished_courses)
                    }
                }
                else -> getString(R.string.course)
            }

            binding.headerSection.setHeaderData(
                title = title,
                actionText = getString(R.string.all_count, viewModel.totalPages),
                onActionClick = {}
            )
        }
        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            updateFilterUI(filter)
        }
    }
}
