package com.home.lexa.ui.utils
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object TTSManager : TextToSpeech.OnInitListener{
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    // Khoi tao TTS khi mo app
    fun init(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this)
        }
    }

    // Callback khi TTS khoi tao xong
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTSManager", "Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu trên máy này.")
            } else {
                isInitialized = true
                Log.d("TTSManager", "Khởi tạo TTS thành công!")
            }
        } else {
            Log.e("TTSManager", "Khởi tạo TTS thất bại.")
        }
    }

    // Ham phat am thanh
    fun speak(text: String) {
        if (!isInitialized) {
            Log.w("TTS Manager", "TTS not ready")
            return
        }

        // Neu dang doc ddo tu truoc ma bam tu moi, thi ngat luon de doc tu moi
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
