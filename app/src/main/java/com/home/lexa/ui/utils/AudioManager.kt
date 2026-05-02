package com.home.lexa.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.plusAssign

class AudioManager(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    val voskSTTManager = VoskSTTManager(context)
    private var mediaPlayer: MediaPlayer? = null
    private var recordingJob: Job? = null
    private var isRecording = false

    private val sampleRate = 16000 //44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

    // Trả về path file WAV sẽ được lưu (ghi async, path trả về ngay)
    @SuppressLint("MissingPermission")
    fun startRecording(fileName: String): String {
        val file = File(context.filesDir, "$fileName.wav")

        audioRecord = AudioRecord(
            // VOICE_RECOGNITION source được thiết kế để chạy song song với SpeechRecognizer
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        isRecording = true
        audioRecord?.startRecording()

        // Ghi file trên IO thread, không block Main thread
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            writePcmToWav(file)
        }

        Log.d("AudioManager", "Bắt đầu ghi âm: ${file.absolutePath}")
        return file.absolutePath
    }

    fun stopRecording(onSaved: (String?) -> Unit = {}) {
        if (!isRecording) {
            onSaved(null)
            return
        }
        isRecording = false

        // Chờ coroutine ghi xong file rồi callback
        CoroutineScope(Dispatchers.IO).launch {
            recordingJob?.join() // Chờ writePcmToWav() hoàn tất
            audioRecord?.release()
            audioRecord = null
            Log.d("AudioManager", "Ghi âm hoàn tất")
            // Callback lên Main thread
            withContext(Dispatchers.Main) {
                onSaved(null) // path đã biết từ startRecording, truyền từ ngoài vào
            }
        }
    }

    private fun writePcmToWav(file: File) {
        voskSTTManager.startListening(16000f) { isReady ->
            if (isReady) {
                // CHỈ KHI READY MỚI CHẠY VÒNG LẶP
                val buffer = ByteArray(bufferSize)
                val outputStream = FileOutputStream(file)

                // Viết WAV header tạm (44 bytes) — sẽ update sau khi biết tổng data size
                outputStream.write(ByteArray(44))

                var totalDataBytes = 0

                while (isRecording) {
                    val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (bytesRead > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalDataBytes += bytesRead
                        voskSTTManager.feedAudioData(buffer, bytesRead)
                    }
                }

                // QUAN TRỌNG: Dừng Mic ở đây để báo hiệu cho phần cứng
                // rằng chúng ta muốn "vét" phần cuối rồi đóng.
                audioRecord?.stop()

                // Đọc nốt buffer còn lại sau khi isRecording = false
                var remaining: Int
                do {
                    remaining = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (remaining > 0) {
                        outputStream.write(buffer, 0, remaining)
                        totalDataBytes += remaining

                        voskSTTManager.feedAudioData(buffer, remaining)
                    }
                } while (remaining > 0)

                outputStream.flush()
                outputStream.close()

                // Ghi lại WAV header đúng với tổng size thực tế
                writeWavHeader(file, totalDataBytes)
                Log.d("AudioManager", "File WAV đã lưu: ${file.absolutePath}, size: $totalDataBytes bytes")
            } else {
                Log.e("AudioManager", "Hủy ghi âm vì Vosk không khởi tạo được.")
                isRecording = false
            }
        }
    }

    private fun writeWavHeader(file: File, totalDataBytes: Int) {
        val raf = RandomAccessFile(file, "rw")
        val totalFileSize = totalDataBytes + 36

        raf.seek(0)
        raf.write("RIFF".toByteArray())
        raf.write(intToLittleEndian(totalFileSize))
        raf.write("WAVE".toByteArray())
        raf.write("fmt ".toByteArray())
        raf.write(intToLittleEndian(16))           // Subchunk1Size (PCM = 16)
        raf.write(shortToLittleEndian(1))           // AudioFormat (PCM = 1)
        raf.write(shortToLittleEndian(1))           // NumChannels (Mono = 1)
        raf.write(intToLittleEndian(sampleRate))    // SampleRate
        raf.write(intToLittleEndian(sampleRate * 2)) // ByteRate = SampleRate * NumChannels * BitsPerSample/8
        raf.write(shortToLittleEndian(2))           // BlockAlign = NumChannels * BitsPerSample/8
        raf.write(shortToLittleEndian(16))          // BitsPerSample
        raf.write("data".toByteArray())
        raf.write(intToLittleEndian(totalDataBytes))
        raf.close()
    }

    private fun intToLittleEndian(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            (value shr 8 and 0xFF).toByte(),
            (value shr 16 and 0xFF).toByte(),
            (value shr 24 and 0xFF).toByte()
        )
    }

    private fun shortToLittleEndian(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            (value shr 8 and 0xFF).toByte()
        )
    }

    fun playAudio(path: String, onComplete: () -> Unit = {}) {
        resetMediaPlayer()
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
                setOnCompletionListener { onComplete(); resetMediaPlayer() }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioManager", "MediaPlayer Error: $what, $extra")
                    resetMediaPlayer(); onComplete(); true
                }
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi playAudio: ${e.message}")
            onComplete()
        }
    }

    fun resetMediaPlayer() {
        try {
            mediaPlayer?.let { if (it.isPlaying) it.stop(); it.release() }
        } catch (e: Exception) {
            Log.e("AudioManager", "Lỗi resetPlayer: ${e.message}")
        } finally {
            mediaPlayer = null
        }
    }

    fun release() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.release()
        audioRecord = null
        voskSTTManager.releaseModel()
        resetMediaPlayer()

    }
}