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

    fun startRecording(fileName: String): String? {
        val file = File(context.filesDir, "$fileName.m4a")
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
            }
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi ghi âm: ${e.message}")
            return null
        }
    }

    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    fun playAudio(path: String, onComplete: () -> Unit = {}) {
        mediaPlayer?.release()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener {
                    onComplete()
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi phát âm thanh: ${e.message}")
            onComplete()
        }
    }

    fun release() {
        stopRecording()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}