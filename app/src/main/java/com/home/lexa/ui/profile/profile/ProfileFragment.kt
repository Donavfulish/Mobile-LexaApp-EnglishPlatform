package com.home.lexa.ui.profile.profile

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.size.ViewSizeResolver
import com.bumptech.glide.Glide
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.core.network.AuthEventBus
import com.home.lexa.data.local.UserManager
import com.home.lexa.databinding.FragmentProfileBinding
import com.home.lexa.ui.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.stfalcon.imageviewer.StfalconImageViewer

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModel()
    private val userManager: UserManager by inject()

    private var avatarUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            avatarUri = uri
            binding.ivAvatar.load(avatarUri) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
            }
            viewModel.updateAvatar(avatarUri, AVATAR_ACTION.UPDATE)
        } else {
            // Người dùng đóng thư viện mà không chọn ảnh
        }
    }

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

        binding.ivAvatar.apply {
            setOnClickListener {
                showAvatarOptions()
            }
        }

        binding.btnLogout.setOnClickListener {
            requireContext().showConfirmDialog(
                title = getString(R.string.logout),
                message = getString(R.string.popup_warning_logout),
                onConfirm = {
                    handleLogout()
                },
                acceptLabel = getString(R.string.logout)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile()
        checkAndLoadUsageData()
    }

    private fun checkAndLoadUsageData() {
        if (hasUsagePermission(requireContext())) {

            loadUsageData()
        } else {
            binding.tvChartSub.text = getString(R.string.tap_to_permit_statistics)
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
            Toast.makeText(requireContext(), getString(R.string.toast_unable_open_settings), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUsageData() {
        lifecycleScope.launch {
            val usageData = withContext(Dispatchers.IO) {
                getAppUsageStats(requireContext())
            }

            Log.d("ProfileFragmentLoad", "Usage Data: $usageData")
            
            val totalMinutes = usageData.sum().toInt()
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            
            binding.tvChartSub.text = if (hours > 0) {
                getString(R.string.total_hours_minutes, hours, mins)
            } else {
                getString(R.string.total_minutes, mins)
            }
            
            binding.usageChartView.setData(usageData)

            val streak = userManager.getStreakCount()
            binding.tvStreakInfo.text = getString(R.string.streak_days, streak)
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
        binding.tvUserName.text = userManager.getUserName() ?: "Lexa user"
        binding.tvUserRole.text = userManager.getUserRole()?.name ?: "Học sinh"

        binding.statsCourses.setCardData(R.drawable.ic_book, 0, getString(R.string.studying_courses))
        binding.statsCourses.setIconStyle(tintColorRes = R.color.c_4285f4, bgColorRes = R.color.c_e8f0fe)

        binding.statsVocab.setCardData(R.drawable.ic_cup, 0, getString(R.string.vocabulary))
        binding.statsVocab.setIconStyle(tintColorRes = R.color.c_f4b400, bgColorRes = R.color.c_fef7e0)

        binding.statsVocabSets.setCardData(R.drawable.ic_folder, 0, getString(R.string.vocabulary_decks))
        binding.statsVocabSets.setIconStyle(tintColorRes = R.color.c_0f9d58, bgColorRes = R.color.c_e6f4ea)

        binding.menuPersonalInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profilePersonalInformationFragment)
        }

        binding.menuEmail.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEmailFragment)
        }

        binding.menuPassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileChangePasswordFragment)
        }

        binding.menuLanguage.apply {
            val currentLang = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "vi"

            val displayValue = if (currentLang == "vi") "Tiếng Việt" else "English"

            setMenuValue(displayValue)

            setOnClickListener {
                showLanguageBottomSheet()
            }
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

                statsCourses.setCardData(R.drawable.ic_book, profile.activeCourses, getString(R.string.studying_courses))
                statsVocab.setCardData(R.drawable.ic_cup, profile.vocabularies, getString(R.string.vocabulary))
                statsVocabSets.setCardData(R.drawable.ic_folder, profile.vocabSets, getString(R.string.vocabulary_decks))

                menuEmail.setMenuValue(StringUtils.maskEmail(profile.email) ?: "")
                menuPersonalInfo.setMenuValue(formatDate(profile.DoB))

                ivAvatar.load(profile.avatarUrl ?: Constants.DEFAULT_AVATAR_URL) {
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

    private fun showAvatarOptions() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.view_bottom_sheet_avatar, null)

        val currentAvatarUrl = viewModel.profileData.value?.avatarUrl

        view.findViewById<View>(R.id.btnViewAvatar).apply {
            visibility = if (currentAvatarUrl != null) View.VISIBLE else View.GONE

            setOnClickListener {
                showFullAvatar()
                dialog.dismiss()
            }
        }

        view.findViewById<View>(R.id.btnUploadAvatar).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnDeleteAvatar).apply {
            visibility = if (currentAvatarUrl != null) View.VISIBLE else View.GONE

            setOnClickListener {
                requireContext().showConfirmDialog(
                    title = getString(R.string.popup_remove_avatar),
                    message = getString(R.string.warning_remove_avatar),
                    onConfirm = {
                        viewModel.updateAvatar(null, AVATAR_ACTION.DELETE)

                        avatarUri = null

                        binding.ivAvatar.load(Constants.DEFAULT_AVATAR_URL)

                        dialog.dismiss()
                    },
                    acceptLabel = getString(R.string.popup_remove_avatar)
                )
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showFullAvatar() {
        val avatarUrl = viewModel.profileData.value?.avatarUrl ?: Constants.DEFAULT_AVATAR_URL

        StfalconImageViewer.Builder<String>(requireContext(), listOf(avatarUrl)) { view, image ->
            Glide.with(view).load(image).into(view)
        }.show()
    }

    private fun showLanguageBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.view_bottom_sheet_language, null)

        val btnVietnamese = view.findViewById<View>(R.id.btnVietnamese)
        val btnEnglish = view.findViewById<View>(R.id.btnEnglish)

        btnVietnamese.setOnClickListener {
            updateLanguage("vi")
            dialog.dismiss()
        }

        btnEnglish.setOnClickListener {
            updateLanguage("en")
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun updateLanguage(langCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)

        val intent = requireActivity().intent

        AppCompatDelegate.setApplicationLocales(appLocale)

        // Ngay sau khi set, yêu cầu Activity kết thúc và chạy lại với hiệu ứng Fade
        requireActivity().apply {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun handleLogout() {
        userManager.clearUser()
        lifecycleScope.launch {
            AuthEventBus.logout()
        }
    }

    private fun formatDate(date: java.util.Date?): String {
        if (date == null) return getString(R.string.edit)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}