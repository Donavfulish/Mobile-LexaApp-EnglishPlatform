package com.home.lexa.ui.library.favorite_library

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentFavoriteLibraryBinding
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.UserRole
import com.home.lexa.domain.models.StudentCourseFilter
import com.home.lexa.ui.components.SearchbarFilter
import com.home.lexa.ui.library.LibraryFragment
import com.home.lexa.ui.library.LibraryModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteLibraryFragment : BaseFragment<FragmentFavoriteLibraryBinding>(FragmentFavoriteLibraryBinding::inflate) {
    private val viewModel: FavoriteLibraryModel by viewModel()
    private val parentViewModel: LibraryModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )
    private val userManager by lazy {
        UserManager(requireContext())
    }
    private val deckAdapter by lazy {
        FavoriteLibraryAdapter(emptyList())
        { course ->
            val bundle = Bundle().apply {
                putLong("courseId", course.id)
            }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
    }

    override fun setupViews() {
        binding.headerSection.setHeaderData(
            title = getString(R.string.favorite_deck),
            actionText = getString(R.string.see_all),
            onActionClick = {
                val role = userManager.getUserRole()

                val bundle = Bundle().apply {
                    putString("filter", StudentCourseFilter.FAVORITE.name)
                }

                when (role) {
                    UserRole.TEACHER -> {
                        findNavController().navigate(R.id.teacherCourseListFragment, bundle)
                    }

                    UserRole.STUDENT -> {
                        findNavController().navigate(R.id.studentCourseListFragment, bundle)
                    }

                    else -> {
                        // fallback nếu null
                        findNavController().navigate(R.id.studentCourseListFragment, bundle)
                    }
                }
            }
        )

        binding.tvGoToPersonal.setOnClickListener {
            (parentFragment as? LibraryFragment)?.let { libraryFragment ->
                libraryFragment.navigateToTab(1)
            }
        }

        if (viewModel.courses.value.isNullOrEmpty()) {
            viewModel.fetchAllCourses(
                isLoadMore = false,
                searchInfo = SearchInfo(query = "", limit = 10),
                nextCursor = null
            )
        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvCourses.apply {
            adapter = deckAdapter
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
                                    searchInfo = viewModel.searchInfor,
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
        viewModel.courses.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }

        parentViewModel.searchInfo.observe(viewLifecycleOwner){ infor ->
            viewModel.updateInfor(infor)
            viewModel.fetchAllCourses(
                isLoadMore = false,
                searchInfo = viewModel.searchInfor,
                nextCursor = null
            )
        }
    }
}
