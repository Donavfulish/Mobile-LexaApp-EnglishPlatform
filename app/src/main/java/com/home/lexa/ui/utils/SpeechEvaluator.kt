package com.home.lexa.ui.utils

import com.home.lexa.domain.models.WordEvaluationItem

object SpeechEvaluator {

    fun evaluate(originalSentence: String, recognizedText: String): List<WordEvaluationItem> {
        // 1. Hàm chuẩn hóa: Chuyển thường, đưa nháy nghiêng về nháy thẳng, và giữ lại dấu nháy trong Regex
        fun normalize(text: String): String {
            return text.lowercase()
                .replace('’', '\'') // Đồng nhất dấu nháy nghiêng thành nháy thẳng
                .replace(Regex("[^a-zA-Z0-9-'\\s]"), "") // Giữ lại chữ, số, khoảng trắng và DẤU NHÁY THẲNG
        }

        val cleanOriginal = normalize(originalSentence)
        val cleanRecognized = normalize(recognizedText)

        // 2. Tách mảng từ vựng
        val originalWords = cleanOriginal.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val recognizedWords = cleanRecognized.split(Regex("\\s+")).filter { it.isNotEmpty() }

        // 3. So khớp
        return originalWords.map { original ->
            val isExactMatch = recognizedWords.any { it == original }

            // Khớp một phần: vẫn nên dùng normalize để tránh dấu nháy làm lệch kết quả
            val isPartialMatch = !isExactMatch && recognizedWords.any {
                it.contains(original) || original.contains(it)
            }

            val (score, status) = when {
                isExactMatch -> Pair(100, "GOOD")
                isPartialMatch -> Pair(50, "MEDIUM")
                else -> Pair(0, "BAD")
            }

            WordEvaluationItem(
                word = original, // Lúc này word sẽ là "i'm", "don't"... giúp bên ngoài khớp đúng vị trí
                score = score,
                status = status
            )
        }
    }
}

// Mẫu xài
//val originalText = "Success is not final."
//val userSpeechText = "Success is not file." // User đọc sai chữ final thành file
//
//val results = SpeechEvaluator.evaluate(originalText, userSpeechText)
//
//// 1. In kết quả từng chữ ra Log
//results.forEach { item ->
//    println("Từ: ${item.word} | Điểm: ${item.score} | Trạng thái: ${item.status}")
//}
//
//// 2. Tính điểm trung bình của cả đoạn văn (Thang 100)
//if (results.isNotEmpty()) {
//    val totalScore = results.sumOf { it.score }
//    val averageScore = totalScore / results.size
//    println("Điểm số bài đọc: $averageScore/100")
//    // Theo ví dụ trên: (100 + 100 + 100 + 0) / 4 = 75/100 điểm
//}