package com.home.lexa.ui.course.teacher_course_list

import android.net.Uri
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.button.MaterialButton
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentTeacherCourseListBinding
import com.home.lexa.domain.models.SearchInfo
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.home.lexa.domain.models.TeacherCourseFilter


class TeacherCourseListFragment : BaseFragment<FragmentTeacherCourseListBinding>(FragmentTeacherCourseListBinding::inflate) {
    private val viewModel: TeacherCourseListModel by viewModel()
    private var searchInfo = SearchInfo(
        query = null,
        sortBy = null,
        order = null,
        limit = 10
    )
    private val courseAdapter by lazy {
        TeacherCourseListAdapter(emptyList())
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
        setInactive(binding.btnMyCourse)

        when (filter) {
            TeacherCourseFilter.MYCOURSE -> setActive(binding.btnMyCourse)
            TeacherCourseFilter.ALL -> setActive(binding.btnAll)
            TeacherCourseFilter.FAVORITE -> setActive(binding.btnFavorite)
            TeacherCourseFilter.LEARNING -> setActive(binding.btnLearning)
        }
    }

    override fun setupViews() {
        binding.searchbarFilter.apply {
            onSearchAction { q ->
                searchInfo = searchInfo.copy(query = q)
                viewModel.fetchAllCourses(false, searchInfo, null)
            }
            setOnSortOptionChanged { options ->
                searchInfo = searchInfo.copy(order = options.order, sortBy = options.sortBy)
                viewModel.fetchAllCourses(false, searchInfo, null)
            }
        }

        binding.headerSection.setHeaderData(
            title = "Khoá học của tôi",
            actionText = "0 tất cả",
            onActionClick = {}
        )

        val filterArg = arguments?.getString("filter")
        val initialFilter = try {
            TeacherCourseFilter.valueOf(filterArg ?: "MYCOURSE")
        } catch (e: Exception) {
            TeacherCourseFilter.MYCOURSE
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
            if(viewModel.currentFilter.value == TeacherCourseFilter.ALL)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
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
            if(viewModel.currentFilter.value == TeacherCourseFilter.MYCOURSE)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
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
            if(viewModel.currentFilter.value == TeacherCourseFilter.FAVORITE)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
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
            if(viewModel.currentFilter.value == TeacherCourseFilter.LEARNING)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = "Khoá học của tôi",
                actionText = "0 tất cả",
                onActionClick = {}
            )
            viewModel.changeFilter(TeacherCourseFilter.LEARNING,
                SearchInfo(
                    query= "",
                    sortBy= "",
                    order= "",
                    limit = 10
                ),
                null)
        }

        if (viewModel.courses.value.data.isNullOrEmpty()) {
            viewModel.fetchAllCourses(
                isLoadMore = false,
                searchInfo = searchInfo,
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

                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        val threshold = 3
                        if (viewModel.isLoading.value == false && !viewModel.isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)) {

                                viewModel.fetchAllCourses(
                                    isLoadMore = true,
                                    searchInfo = searchInfo,
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

        viewModel.courses.observe(viewLifecycleOwner) { list ->
            if(list.status != viewModel.currentFilter.value){
                return@observe
            }
            courseAdapter.updateData(list.data)
            if(list.status == TeacherCourseFilter.MYCOURSE){
                binding.rvCourses.post{
                    courseAdapter.ToggleDeleteBtn(binding.rvCourses, true)
                }
            } else {
                binding.rvCourses.post{
                    courseAdapter.ToggleDeleteBtn(binding.rvCourses, false)
                }
            }
            val filter = viewModel.currentFilter.value ?: TeacherCourseFilter.MYCOURSE

            val title = when (filter) {
                TeacherCourseFilter.MYCOURSE -> "Khoá học của tôi"
                TeacherCourseFilter.ALL -> "Tất cả khoá học"
                TeacherCourseFilter.FAVORITE -> "Khoá học yêu thích"
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
