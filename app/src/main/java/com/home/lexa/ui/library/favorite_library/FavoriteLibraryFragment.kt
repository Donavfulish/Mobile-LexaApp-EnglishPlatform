package com.home.lexa.ui.library.favorite_library

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentFavoriteLibraryBinding
import com.home.lexa.domain.models.UserRole
import com.home.lexa.ui.course.student_course_list.CourseFilter
import com.home.lexa.ui.library.LibraryFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteLibraryFragment : BaseFragment<FragmentFavoriteLibraryBinding>(FragmentFavoriteLibraryBinding::inflate) {
    private val viewModel: FavoriteLibraryModel by viewModel()
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
            title = "BỘ TỪ VỰNG YÊU THÍCH",
            actionText = "Xem tất cả",
            onActionClick = {
                val role = userManager.getUserRole()

                val bundle = Bundle().apply {
                    putString("filter", CourseFilter.FAVORITE.name)
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

        binding.rvCourses.apply {
            adapter = deckAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.fetchAllCourses()
    }

    override fun observeData() {
        viewModel.courses.observe(viewLifecycleOwner) { list ->
            deckAdapter.updateData(list)
        }
    }
}