package com.home.lexa.ui.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    /**
     * Kiểm tra chuỗi có đúng định dạng dd/MM/yyyy và là ngày hợp lệ hay không
     */
    fun isValidDate(dateStr: String?): Boolean {
        if (dateStr.isNullOrBlank()) return false

        val regex = Regex("""^\d{2}/\d{2}/\d{4}$""")
        if (!regex.matches(dateStr)) return false

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false // Cực kỳ quan trọng: Chặn ngày 31/02 hoặc 32/01

        return try {
            sdf.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }

    /**
     * Chuyển đổi từ dd/MM/yyyy sang yyyy-MM-dd để gửi lên Backend
     * Ví dụ: 27/11/2005 -> 2005-11-27
     */
    fun convertToBackendFormat(dateStr: String?): String? {
        if (dateStr.isNullOrBlank()) return null

        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            null // Trả về null nếu parse lỗi để Backend xử lý an toàn
        }
    }
}