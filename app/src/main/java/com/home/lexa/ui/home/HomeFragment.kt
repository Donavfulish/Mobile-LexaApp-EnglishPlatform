package com.home.lexa.ui.home


import androidx.lifecycle.lifecycleScope
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.repository.mockParagraphData
import com.home.lexa.databinding.FragmentHomeBinding
import com.home.lexa.ui.components.CourseData
import com.home.lexa.ui.components.CourseProgressData
import com.home.lexa.ui.components.Popup
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    private val viewModel: HomeViewModel by viewModel()

    override fun setupViews() {

    }

    override fun observeData() {
        //
    }
}