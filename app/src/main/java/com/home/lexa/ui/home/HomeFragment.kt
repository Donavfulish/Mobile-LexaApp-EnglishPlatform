package com.home.lexa.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentHomeBinding
import com.home.lexa.ui.components.CourseData
import com.home.lexa.ui.components.CourseProgressData
import com.home.lexa.ui.components.Popup
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // Koin tự động tiêm ViewModel vào đây cực gọn
    private val viewModel: HomeViewModel by viewModel()

    override fun setupViews() {
        // 1. Tương tác với Custom Component LexaButton
        binding.btnLoadCourses.setText("Tải danh sách khóa học")

        binding.btnLoadCourses.setOnLexaClickListener {
            viewModel.getCourses() // Bắn event cho ViewModel xử lý
        }

        // 2. Lắng nghe State thay đổi từ ViewModel để update UI (giống useEffect)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { stateText ->
                binding.txtStatus.text = stateText
            }
        }

        binding.progressBar.setProgress(80);
        binding.tag.setTagData("Ngày 04", "#000000", false)
        binding.smallRing.setProgress(40)
        binding.cardKhoaHoc.setCardData(R.drawable.ic_language, 15, "Đây là một" +
                "")
        binding.headerSection.setHeaderData("Danh sách khóa học", R.drawable.ic_language,"Xem them"){}
        binding.txtStatus.setOnClickListener {
            // 1. Khởi tạo Popup (truyền Context của Fragment vào)
            val myPopup = Popup(requireContext())

            // 2. Gọi hàm showDialog bằng cú pháp "Named Arguments" cho dễ đọc
            myPopup.showDialog(
                title = "Thông báo hệ thống",
                subTitle = "Bạn có chắc chắn muốn tải lại danh sách khóa học không? Dữ liệu cũ sẽ bị ghi đè.",
                isWarning = true, // Set true để nút Xác nhận chuyển màu đỏ, false thì màu tím
                confirmText = "Tải lại",
                onConfirm = {
                    // Xử lý khi bấm nút "Tải lại"
                    viewModel.getCourses()
                },
                onCancel = {
                    // Xử lý khi bấm nút "Hủy" (nếu không cần làm gì thêm thì để trống, dialog tự đóng)
                }
            )
        }
        val sampleCourse = CourseData(
            title = "Tiếng Anh Giao Tiếp Cơ Bản ",
            description = "Nắm vững 500 cấu trúc câu thông dụng nhất trong giao tiếp hàng ngày với người bản xứ.",
            authorName = "John Smith",
            userCount = 1240,
            heartCount = 450,
            wordCountText = "12 TỪ",
            tagTitle = "Giao tiếp",
            tagColorHex = "#6A65E9",
            thumbnailRes = R.drawable.ic_launcher_background, // Sửa thành ảnh thực tế
            authorAvatarRes = R.drawable.ic_launcher_background  // Sửa thành ảnh thực tế
        )


        binding.deckCard.setDeckCardData(data = sampleCourse,
            onCardClick = {
                println("Chuyển sang màn hình Chi tiết khóa học!")
            },
            onOptionsClick = {
                println("Mở menu Tùy chọn (Lưu khóa học, Chia sẻ...)")
            })
        binding.teacherCourseCard.setCourseData(data = sampleCourse,
            onCardClick = {
                println("Chuyển sang màn hình Chi tiết khóa học!")
            },
            onOptionsClick = {
                println("Mở menu Tùy chọn (Lưu khóa học, Chia sẻ...)")
            })
        val myCourse = CourseProgressData(
            title = "Luyện Thi IELTS Speaking 7.0+",
            description = "Chiến thuật trả lời các phần thi Speaking và bộ từ vựng nâng cấp cho các chủ đề",
            authorName = "John Smith",
            userCount = 850,
            heartCount = 320,
            progressPercent = 10, // Truyền 10% vào đây
            actionText = "HỌC NGAY", // Hoặc đổi thành "TIẾP TỤC" nếu % lớn hơn 0
            tagTitle = "Luyện thi",
            tagColorHex = "#6A65E9", // Màu xanh tím
            thumbnailRes = R.drawable.ic_launcher_background,
            authorAvatarRes = R.drawable.ic_launcher_background
        )


        binding.studentCourseCard.setCourseData( data = myCourse,
            onActionClick = {
                println("User bấm nút HỌC NGAY -> Mở video bài giảng!")
            },
            onCardClick = {
                println("User bấm vào thẻ -> Mở màn hình Giới thiệu khóa học!")
            })
    }

    override fun observeData() {
        //
    }
}