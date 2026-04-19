package com.home.lexa.ui.utils

import com.home.lexa.domain.models.WordEvaluationItem

object SpeechEvaluator {

    fun evaluate(originalSentence: String, recognizedText: String): List<WordEvaluationItem> {
        // 1. Làm sạch chuỗi: Chuyển chữ thường và loại bỏ các dấu câu (, . ? !) để so sánh chính xác hơn
        val cleanOriginal = originalSentence.replace(Regex("[^a-zA-Z0-9\\s]"), "").lowercase()
        val cleanRecognized = recognizedText.replace(Regex("[^a-zA-Z0-9\\s]"), "").lowercase()

        // 2. Tách thành các mảng từ vựng
        val originalWords = cleanOriginal.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val recognizedWords = cleanRecognized.split(Regex("\\s+")).filter { it.isNotEmpty() }

        // 3. Tiến hành so khớp từng từ
        return originalWords.map { original ->

            // 1. Kiểm tra khớp hoàn toàn
            val isExactMatch = recognizedWords.any { it == original }

            // 2. Kiểm tra khớp một phần (ví dụ: sai 's', 'ed' hoặc gần giống)
            // Bạn có thể dùng thuật toán Levenshtein ở đây nếu muốn xịn hơn
            val isPartialMatch = recognizedWords.any {
                it.contains(original) || original.contains(it)
            }

            // 4. Định chuẩn Điểm số (Score) và Trạng thái (Status)
            val (score, status) = when {
                isExactMatch -> Pair(100, "GOOD")     // Hoàn hảo: 100 điểm
                isPartialMatch -> Pair(50, "MEDIUM")  // Tạm được: 50 điểm
                else -> Pair(0, "BAD")                // Sai/Bỏ sót: 0 điểm
            }

            // Trả về Object theo đúng yêu cầu
            WordEvaluationItem(
                word = original,
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