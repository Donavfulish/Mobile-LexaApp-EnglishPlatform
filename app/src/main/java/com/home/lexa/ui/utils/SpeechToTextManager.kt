package com.home.lexa.ui.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechToTextManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // Ép định dạng en-US thay vì để mặc định
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Lấy kết quả từng phần để STT "nhạy" hơn
    }

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            // 1. Dọn dẹp cái cũ nếu còn tồn tại
            destroy()

            // 2. Khởi tạo mới hoàn toàn để tránh Error 5
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                    onResult(text)
                }

                override fun onError(error: Int) {
                    Log.e("STT_LOG", "Error code: $error")
                    // Chỉ báo lỗi nếu không phải là lỗi "Busy" (đang bận)
                    if (error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                        onError(parseError(error))
                    }
                }

                // Thêm log để debug xem Mic có tín hiệu không
                override fun onRmsChanged(rmsdB: Float) {
                    // Nếu rmsdB > 0 nghĩa là Mic đang nhận tiếng động
                }

                override fun onReadyForSpeech(params: Bundle?) { Log.d("STT", "Mic đã sẵn sàng") }
                override fun onBeginningOfSpeech() { Log.d("STT", "Đang nghe...") }
                override fun onEndOfSpeech() {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun parseError(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Lỗi âm thanh"
            SpeechRecognizer.ERROR_CLIENT -> "Lỗi Client (Error 5)"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Thiếu quyền truy cập"
            SpeechRecognizer.ERROR_NETWORK -> "Lỗi mạng"
            SpeechRecognizer.ERROR_NO_MATCH -> "Không nhận diện được giọng nói (Error 7)"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Hết thời gian chờ (Error 6)"
            else -> "Lỗi $errorCode"
        }
    }
}