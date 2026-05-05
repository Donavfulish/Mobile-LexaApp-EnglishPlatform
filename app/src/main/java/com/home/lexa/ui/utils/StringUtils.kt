package com.home.lexa.ui.utils

import android.util.Patterns

object StringUtils {
    /**
     * Kiểm tra định dạng Email hợp lệ.
     * Sử dụng Patterns có sẵn của Android để đảm bảo độ chính xác cao mà không cần viết Regex phức tạp.
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Ẩn một phần email theo định dạng: ký tự đầu + **** + ký tự cuối trước @ + domain.
     * Ví dụ: huynhgiaau27112005@gmail.com -> h****5@gmail.com
     */
    fun maskEmail(email: String?): String {
        if (email.isNullOrBlank() || !isValidEmail(email)) return email ?: ""

        val parts = email.split("@")
        val localPart = parts[0] // Phần trước @ (huynhgiaau27112005)
        val domainPart = parts[1] // Phần sau @ (gmail.com)

        return if (localPart.length >= 2) {
            val firstChar = localPart.first()
            val lastChar = localPart.last()
            "$firstChar****$lastChar@$domainPart"
        } else {
            // Trường hợp email quá ngắn (vd: a@gmail.com) thì chỉ thêm sao phía trước
            "****@$domainPart"
        }
    }

    fun findWordRange(fullText: String, word: String, startIndex: Int): Pair<Int, Int>? {
        // Loại bỏ các ký tự đặc biệt khỏi từ để tìm kiếm chính xác hơn
        val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
        if (cleanWord.isEmpty()) return null

        val textToSearch = fullText.lowercase()
        var currentPos = startIndex

        while (currentPos < textToSearch.length) {
            val foundIndex = textToSearch.indexOf(cleanWord, currentPos)
            if (foundIndex == -1) return null

            // Kiểm tra xem đây có phải là một từ độc lập không (tránh tìm "eat" trong "great")
            val endIndex = foundIndex + cleanWord.length
            return Pair(foundIndex, endIndex)
        }
        return null
    }
}