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
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class StudentCourseListFragment : BaseFragment<FragmentStudentCourseListBinding>(FragmentStudentCourseListBinding::inflate) {
    private val viewModel: StudentCourseListModel by activityViewModel()

    private val courseAdapter by lazy {
        StudentCourseListAdapter(
            if(viewModel.courses.value?.data != null)
                viewModel.courses.value!!.data
            else emptyList()
        ) { course ->
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
            actionText = "",
            onActionClick = {}
        )

        binding.btnAll.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.ALL)
                return@setOnClickListener
            viewModel.changeFilter(
                StudentCourseFilter.ALL,
                SearchInfo(query= "", sortBy= "", order= "", limit = 10),
                null)
        }

        binding.btnFavorite.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.FAVORITE)
                return@setOnClickListener
            viewModel.changeFilter(StudentCourseFilter.FAVORITE,
                SearchInfo(query= "", sortBy= "", order= "", limit = 10),
                null)
        }

        binding.btnLearning.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.LEARNING)
                return@setOnClickListener
            viewModel.changeFilter(StudentCourseFilter.LEARNING,
                SearchInfo(query= "", sortBy= "", order= "", limit = 10),
                null)
        }

        binding.btnFinished.setOnClickListener {
            if(viewModel.currentFilter.value == StudentCourseFilter.FINISHED)
                return@setOnClickListener
            viewModel.changeFilter(StudentCourseFilter.FINISHED,
                SearchInfo(query= "", sortBy= "", order= "", limit = 10),
                null)
        }

        binding.rvCourses.apply {
            adapter = courseAdapter
            val lm = layoutManager as? LinearLayoutManager ?: LinearLayoutManager(context).also { layoutManager = it }
            
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleItemCount = lm.childCount
                        val totalItemCount = lm.itemCount
                        val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
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
        viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
    }

    override fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateHeaderTitleAndCount()
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            binding.searchbarFilter.setSuggestions(suggestions)
        }

        viewModel.courses.observe(viewLifecycleOwner) { shortCourse ->
            if (shortCourse.status != viewModel.currentFilter.value) {
                return@observe
            }
            courseAdapter.updateData(shortCourse.data)
            updateHeaderTitleAndCount()
        }

        viewModel.currentFilter.observe(viewLifecycleOwner) { filter ->
            updateFilterUI(filter)
            updateHeaderTitleAndCount()
        }
    }

    private fun updateHeaderTitleAndCount() {
        val filter = viewModel.currentFilter.value ?: StudentCourseFilter.ALL
        val title = when (filter) {
            StudentCourseFilter.ALL -> getString(R.string.all_courses)
            StudentCourseFilter.FAVORITE -> getString(R.string.favorite_courses)
            StudentCourseFilter.LEARNING -> getString(R.string.learning_courses)
            StudentCourseFilter.FINISHED -> getString(R.string.finished_courses)
        }

        val isLoading = viewModel.isLoading.value == true
        
        // Chỉ hiển thị số lượng khi không đang trong quá trình tải dữ liệu mới
        // Nếu là load more, có thể vẫn muốn hiện số lượng cũ, nhưng để đơn giản và theo yêu cầu "xong mới hiện":
        val countText = if (isLoading) {
            "" 
        } else {
            getString(R.string.all_count, viewModel.totalPages)
        }

        binding.headerSection.setHeaderData(
            title = title,
            actionText = countText,
            onActionClick = {}
        )
    }
}
