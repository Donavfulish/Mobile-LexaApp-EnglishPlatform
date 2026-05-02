// Localization applied
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
import com.home.lexa.domain.models.StudentCourseFilter
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.home.lexa.domain.models.TeacherCourseFilter


class TeacherCourseListFragment : BaseFragment<FragmentTeacherCourseListBinding>(FragmentTeacherCourseListBinding::inflate) {
    // SỬA: Dùng activityViewModel() để share instance và giữ data khi back
    private val viewModel: TeacherCourseListModel by activityViewModel()

    private val courseAdapter by lazy {
        TeacherCourseListAdapter(
            if(viewModel.courses.value?.data != null)
            viewModel.courses.value!!.data
        else emptyList())
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
                viewModel.searchInfo = viewModel.searchInfo.copy(query = q)
                viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
            }
            setOnSortOptionChanged { options ->
                viewModel.searchInfo = viewModel.searchInfo.copy(order = options.order, sortBy = options.sortBy)
                viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
            }
            onTextChanged { query ->
                if(query.length >= 2){
                    viewModel.getSuggestions(query)
                }
            }
        }

        binding.headerSection.setHeaderData(
            title = getString(R.string.my_courses),
            actionText = getString(R.string.all_count, 0),
            onActionClick = {}
        )

        binding.btnAll.setOnClickListener {
            if(viewModel.currentFilter.value == TeacherCourseFilter.ALL)
                return@setOnClickListener
            binding.headerSection.setHeaderData(
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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
                title = getString(R.string.my_courses),
                actionText = getString(R.string.all_count, 0),
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

        if (viewModel.courses.value == null || viewModel.courses.value?.data.isNullOrEmpty()) {
            val filterArg = arguments?.getString("filter")
            val initialFilter = try {
                TeacherCourseFilter.valueOf(filterArg ?: "MYCOURSE")
            } catch (e: Exception) {
                TeacherCourseFilter.MYCOURSE
            }
            viewModel.changeFilter(initialFilter, viewModel.searchInfo, null)
        } else {
            binding.searchbarFilter.setTextSearch(viewModel.searchInfo.query ?: "")
            viewModel.currentFilter.value?.let { updateFilterUI(it) }
        }

        binding.addBtn.setOnClickAction {
            findNavController().navigate(
                R.id.action_courseFragment_to_courseDetailFragment)
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
        // Ưu tiên lấy filter từ arguments nếu có (ví dụ bấm "See all" từ Dashboard)
        val filterArg = arguments?.getString("filter")

        if (filterArg != null) {
            val targetFilter = try {
                TeacherCourseFilter.valueOf(filterArg) } catch (e: Exception) { null }
            if (targetFilter != null && targetFilter != viewModel.currentFilter.value) {
                viewModel.changeFilter(targetFilter, viewModel.searchInfo, null)
                arguments?.remove("filter")
            } else {
                viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
            }
        } else {
            viewModel.fetchAllCourses(false, viewModel.searchInfo, null)
        }

        binding.searchbarFilter.setTextSearch(viewModel.searchInfo.query ?: "")
        viewModel.currentFilter.value?.let { updateFilterUI(it) }
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
                TeacherCourseFilter.MYCOURSE -> getString(R.string.my_courses)
                TeacherCourseFilter.ALL -> getString(R.string.all_courses)
                TeacherCourseFilter.FAVORITE -> getString(R.string.favorite_courses)
                TeacherCourseFilter.LEARNING -> getString(R.string.learning_courses)
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
        viewModel.suggestions.observe(viewLifecycleOwner) { list ->
            binding.searchbarFilter.setSuggestions(list)
        }
    }
}
