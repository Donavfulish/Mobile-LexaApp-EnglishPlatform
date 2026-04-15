package com.home.lexa.ui.profile.profile

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.home.lexa.R
import com.home.lexa.core.network.AuthEventBus
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
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
        Log.d("ProfileFragment", "Start");

        binding.btnLogout.setOnClickListener {
            requireContext().showConfirmDialog(
                title = "Đăng xuất",
                message = "Dữ liệu chưa lưu có thể bị mất. Bạn vẫn muốn đăng xuất?",
                onConfirm = {
                    handleLogout()
                },
                acceptLabel = "Đăng xuất"
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile()
        Log.d("onResume", "Di vao nhe");
        checkAndLoadUsageData()
    }

    private fun checkAndLoadUsageData() {
        if (hasUsagePermission(requireContext())) {

            loadUsageData()
        } else {
            binding.tvChartSub.text = "Chạm để cấp quyền xem thống kê"
            binding.tvChartSub.setOnClickListener {
                requestUsagePermission()
            }
        }
    }

    private fun hasUsagePermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )

        Log.d("hasUsagePermission", "mode: $mode")
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsagePermission() {
        try {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Không thể mở cài đặt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUsageData() {
        Log.d("ProfileFragment", "Di vao")
        lifecycleScope.launch {
            val usageData = withContext(Dispatchers.IO) {
                getAppUsageStats(requireContext())
            }

            Log.d("ProfileFragmentLoad", "Usage Data: $usageData")
            
            val totalMinutes = usageData.sum().toInt()
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            
            binding.tvChartSub.text = if (hours > 0) {
                "Tổng cộng: $hours giờ $mins phút"
            } else {
                "Tổng cộng: $mins phút"
            }
            
            binding.usageChartView.setData(usageData)

            val streak = userManager.getStreakCount()
            binding.tvStreakInfo.text = "$streak ngày 🔥"
        }
    }

    private fun getAppUsageStats(context: Context): List<Float> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()

        // Quay về Thứ 2 của tuần này
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val weekStats = mutableListOf<Float>()
        val packageName = context.packageName

        for (i in 0..6) {
            val startMillis = calendar.timeInMillis
            val endCalendar = calendar.clone() as Calendar
            endCalendar.add(Calendar.DAY_OF_YEAR, 1)
            val endMillis = endCalendar.timeInMillis

            // Truy vấn dữ liệu cho từng ngày cụ thể
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, 
                startMillis, 
                endMillis
            )
            
            val totalTime = stats.filter { it.packageName == packageName }
                .sumOf { it.totalTimeInForeground }

            weekStats.add(totalTime / (1000f * 60f)) // Chuyển sang phút
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        Log.d("ProfileFragment", "weekStats $weekStats")
        return weekStats
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

        binding.menuEmail.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileEmailFragment()
            findNavController().navigate(action)
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
        lifecycleScope.launch {
            AuthEventBus.logout()
        }
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