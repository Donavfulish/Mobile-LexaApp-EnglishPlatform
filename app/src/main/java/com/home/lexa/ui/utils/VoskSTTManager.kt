package com.home.lexa.ui.utils

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException

class VoskSTTManager(private val context: Context) {
    private var model: Model? = null
    private var recognizer: Recognizer? = null

    // Khởi tạo Model (nên gọi khi start App hoặc vào màn hình học)
    fun initModel(onComplete: () -> Unit) {
        // Vosk unpack cần: context, modelPath (trong assets), targetPath, và callback
        StorageService.unpack(context, "model-en-us", "model",
            { model: Model -> // Success callback
                this.model = model
                onComplete()
            },
            { exception: IOException -> // Error callback (Sửa lỗi thiếu parameter)
                Log.e("VOSK_ERROR", "Failed to unpack model", exception)
            }
        )
    }

    fun startListening(sampleRate: Float, onReady: (Boolean) -> Unit) {
        if (model == null) {
            // Nếu model chưa xong, ta không thể tạo recognizer
            Log.e("Vosk", "Model chưa load xong!")
            onReady(false)
            return
        }

        try {
            if (recognizer == null) {
                recognizer = Recognizer(model, sampleRate)
            }
            Log.d("Vosk", "Recognizer đã sẵn sàng.")
            onReady(true) // Thông báo cho AudioManager là "Bào" được rồi!
        } catch (e: Exception) {
            Log.e("Vosk", "Lỗi: ${e.message}")
            onReady(false)
        }
    }

    // Quan trọng: Hàm này nhận byte array từ chính AudioManager của bạn
    fun feedAudioData(buffer: ByteArray, numRead: Int): String {
        val isReady = recognizer?.acceptWaveForm(buffer, numRead) ?: false

        // DEBUG ==========================
        Log.d("Vosk", "Is Voice Ready: $isReady") // Nếu luôn là false, nghĩa là Vosk không nhận diện được âm thanh

        if (!isReady) {
            // NGAY CẢ KHI FALSE, HÃY LOG THỬ CÁI NÀY
            Log.d("Vosk", "Partial: ${recognizer?.partialResult}")
        } else {
            Log.d("Vosk", "Final: ${recognizer?.result}")
        }

        var sum = 0.0
        for (i in 0 until numRead) {
            sum += buffer[i].toInt() * buffer[i].toInt()
        }
        val amplitude = Math.sqrt(sum / numRead)
        Log.d("Vosk", "Amplitude: $amplitude | NumRead: $numRead")

        // ================================

        val jsonStr = if (isReady) {
            recognizer?.result
        } else {
            recognizer?.partialResult
        }

        // KIỂM TRA QUAN TRỌNG: Chỉ parse nếu chuỗi không rỗng và không null
        if (jsonStr.isNullOrBlank()) return ""

        return try {
            val json = JSONObject(jsonStr)
            if (isReady) json.optString("text", "") else json.optString("partial", "")
        } catch (e: Exception) {
            Log.e("Vosk", "Lỗi parse JSON: $jsonStr")
            ""
        }
    }

    fun getFinalResult(): String {
        val jsonResult = recognizer?.finalResult ?: "" // Lấy chuỗi từ Vosk

        if (jsonResult.isBlank()) {
            Log.d("Vosk", "Kết quả trống, không parse JSON")
            return ""
        }

        stop()

        return try {
            val json = JSONObject(jsonResult)
            json.optString("text", "") // Lấy field "text" an toàn
        } catch (e: JSONException) {
            Log.e("Vosk", "Lỗi parse JSON: ${e.message} với chuỗi: '$jsonResult'")
            ""
        }
    }

    fun stop() {
        recognizer?.reset()
    }

    fun releaseModel() {
        try {
            // 1. Giải phóng Recognizer trước (nếu đang tồn tại)
            recognizer?.let {
                it.close()
                recognizer = null
                Log.d("VoskSTTManager", "Recognizer đã được giải phóng.")
            }

            // 2. Giải phóng Model
            // Lưu ý: Đối tượng Model trong Vosk không có hàm .close(),
            // nhưng việc gán null giúp Garbage Collector (GC) thu hồi bộ nhớ dễ dàng hơn.
            model = null
            Log.d("VoskSTTManager", "Model đã được gán null để giải phóng RAM.")

        } catch (e: Exception) {
            Log.e("VoskSTTManager", "Lỗi khi giải phóng tài nguyên: ${e.message}")
        }
    }
}