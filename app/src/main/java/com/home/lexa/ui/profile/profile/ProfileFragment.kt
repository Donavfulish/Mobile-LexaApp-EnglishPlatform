package com.home.lexa.ui.profile.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentProfileBinding
import com.home.lexa.ui.profile.ProfileViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModel()
    private val userManager: UserManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialUI()
        observeData()

        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        // Mỗi khi quay lại màn hình Profile (từ màn hình chỉnh sửa), gọi lại API
        viewModel.fetchProfile()
    }

    private fun setupInitialUI() {
        binding.tvUserName.text = userManager.getUserName() ?: "Người dùng Lexa"
        binding.tvUserRole.text = userManager.getUserRole()?.name ?: "Học sinh"

        binding.statsCourses.setCardData(R.drawable.ic_book, 0, "Khóa đang học")
        binding.statsCourses.setIconStyle(tintColorHex = "#4285F4", bgColorHex = "#E8F0FE")

        binding.statsVocab.setCardData(R.drawable.ic_cup, 0, "Từ vựng")
        binding.statsVocab.setIconStyle(tintColorHex = "#F4B400", bgColorHex = "#FEF7E0")

        binding.statsVocabSets.setCardData(R.drawable.ic_folder, 0, "Bộ từ vựng")
        binding.statsVocabSets.setIconStyle(tintColorHex = "#0F9D58", bgColorHex = "#E6F4EA")
        
        binding.menuPersonalInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profilePersonalInformationFragment)
        }

        binding.menuNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileNotificationFragment)
        }
    }

    private fun observeData() {
        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            if (profile == null) return@observe
            binding.apply {
                tvUserName.text = profile.fullName ?: userManager.getUserName()
                tvUserEmail.text = profile.email

                statsCourses.setCardData(R.drawable.ic_book, profile.activeCourses, "Khóa đang học")
                statsVocab.setCardData(R.drawable.ic_cup, profile.vocabularies, "Từ vựng")
                statsVocabSets.setCardData(R.drawable.ic_folder, profile.vocabSets, "Bộ từ vựng")

                menuEmail.setMenuValue(profile.email ?: "")
                menuPersonalInfo.setMenuValue(formatDate(profile.DoB))

                ivAvatar.load(profile.avatarUrl) { 
                    placeholder(R.drawable.ic_person)
                    error(R.drawable.ic_person)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogout() {
        userManager.clearUser()
        findNavController().navigate(R.id.loginFragment)
        activity?.finish()
    }

    private fun formatDate(date: java.util.Date?): String {
        if (date == null) return "Chỉnh sửa"
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}