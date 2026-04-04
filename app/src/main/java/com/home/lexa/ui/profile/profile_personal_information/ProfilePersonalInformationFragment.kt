package com.home.lexa.ui.profile.profile_personal_information

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentProfilePersonalInformationBinding
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.ui.profile.profile.ProfileViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfilePersonalInformationFragment : BaseFragment<FragmentProfilePersonalInformationBinding>(FragmentProfilePersonalInformationBinding::inflate) {

    // Sử dụng activityViewModel để chia sẻ dữ liệu với ProfileFragment
    private val viewModel: ProfileViewModel by activityViewModel()
    private val userManager: UserManager by inject()

    private fun formatDate(date: java.util.Date?): String {
        if (date == null) return "1995-05-20"
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()


        binding.btnSave.setOnClickAction {
            val updateData = UpdateProfileRequest(
                id = userManager.getUserId(),
                fullName = binding.fullNameInput.getText(),
                DoB = binding.birthDateInput.getText(),
                address = binding.addressInput.getText()
            )
            viewModel.updateProfile(updateData)
        }
    }

    override fun setupViews() {
        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setText("Thông tin cá nhân")
            setBackButtonVisible(true)
        }

        binding.fullNameInput.apply {
            setLabel("HỌ VÀ TÊN")
            setPlaceHolderText("Nhập họ và tên...")
        }

        binding.birthDateInput.apply {
            setLabel("NGÀY SINH")
            setEndIcon(R.drawable.ic_calendar)
            
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                setText(selectedDate)
            }

            val showDatePicker = {
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            setOnInputClickListener { showDatePicker() }
            setOnEndIconClickListener { showDatePicker() }
        }

        binding.addressInput.apply {
            setInputHeight(150)
            setLabel("ĐỊA CHỈ")
            setPlaceHolderText("Nhập địa chỉ của bạn...")
        }

        val colorPink = 0xFF636AE8.toInt() // Dùng màu purple_paragraph của app
        val colorWhite = 0xFFFFFFFF.toInt()
        binding.btnSave.apply {
            setText("Lưu thay đổi", colorWhite)
            setBackground(colorPink)
        }
    }

    override fun observeData() {
        // Lắng nghe dữ liệu profile
        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.fullNameInput.setText(profile.fullName ?: "")
                binding.birthDateInput.setText(formatDate(profile.DoB))
                binding.addressInput.setText(profile.address ?: "")
            }
        }

        // Lắng nghe trạng thái loading để cập nhật button
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSave.setLoading(isLoading)
        }

        // Lắng nghe cập nhật thành công
        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Lưu thông tin thành công", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
                // Tự động quay lại màn hình Profile
                parentFragmentManager.popBackStack()
            }
        }

        // Lắng nghe lỗi
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}