package com.home.lexa.ui.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false

    fun startRecording(fileName: String): String? {
        if (isRecording) return null

        // Sử dụng filesDir để lưu trữ lâu dài
        val file = File(context.filesDir, "$fileName.mp3")
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
                isRecording = true
            }
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi startRecording: ${e.message}")
            return null
        }
    }

    fun stopRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.stop()
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Dừng ghi âm quá sớm hoặc lỗi: ${e.message}")
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }

    fun playAudio(path: String, onComplete: () -> Unit = {}) {
        resetMediaPlayer() // Dọn dẹp cái cũ đang chạy (nếu có) trước khi phát cái mới

        val file = File(path)
        if (!file.exists()) {
            Log.e("AudioManager", "File không tồn tại: $path")
            onComplete()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener {
                    onComplete()
                    resetMediaPlayer()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioManager", "MediaPlayer Error: $what, $extra")
                    resetMediaPlayer()
                    onComplete()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi playAudio: ${e.message}")
            onComplete()
        }
    }

    fun resetMediaPlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi resetPlayer: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }

    fun release() {
        stopRecording()
        resetMediaPlayer()
    }
}