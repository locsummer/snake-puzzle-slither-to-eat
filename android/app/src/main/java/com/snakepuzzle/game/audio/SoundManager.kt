package com.snakepuzzle.game.audio

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

class SoundManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SnakePuzzleAudio", Context.MODE_PRIVATE)
    private val vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    private val sampleRate = 44100

    var isSoundEnabled by mutableStateOf(true)
        private set

    var isMusicEnabled by mutableStateOf(true)
        private set

    var isHapticsEnabled by mutableStateOf(true)
        private set

    @Volatile
    private var bgmRunning = false
    private var bgmThread: Thread? = null

    init {
        isSoundEnabled = prefs.getBoolean("sfx_enabled", true)
        isMusicEnabled = prefs.getBoolean("music_enabled", true)
        isHapticsEnabled = prefs.getBoolean("haptics_enabled", true)
        startBgmIfEnabled()
    }

    fun toggleSfx() {
        isSoundEnabled = !isSoundEnabled
        prefs.edit().putBoolean("sfx_enabled", isSoundEnabled).apply()
    }

    fun toggleMusic() {
        isMusicEnabled = !isMusicEnabled
        prefs.edit().putBoolean("music_enabled", isMusicEnabled).apply()
        if (isMusicEnabled) {
            startBgmIfEnabled()
        } else {
            stopBgm()
        }
    }

    fun toggleHaptics() {
        isHapticsEnabled = !isHapticsEnabled
        prefs.edit().putBoolean("haptics_enabled", isHapticsEnabled).apply()
    }

    private fun startBgmIfEnabled() {
        if (!isMusicEnabled || bgmRunning) return
        bgmRunning = true
        bgmThread = thread(name = "SnakePuzzleBGM", isDaemon = true) {
            val bgmNotes = doubleArrayOf(
                261.63, 329.63, 392.00, 523.25, // C - E - G - C
                293.66, 349.23, 440.00, 587.33, // D - F - A - D
                246.94, 293.66, 392.00, 493.88, // B - D - G - B
                261.63, 329.63, 392.00, 523.25  // C - E - G - C
            )
            val noteDuration = 0.28
            val bufferSamples = (sampleRate * noteDuration).toInt()
            val shortBuffer = ShortArray(bufferSamples)

            var audioTrack: AudioTrack? = null
            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                audioTrack.play()

                var noteIndex = 0
                while (bgmRunning) {
                    val freq = bgmNotes[noteIndex % bgmNotes.size]
                    noteIndex++

                    for (i in 0 until bufferSamples) {
                        val t = i.toDouble() / sampleRate
                        val env = max(0.0, 1.0 - (i.toDouble() / bufferSamples)) * 0.12 // Very gentle ambient volume
                        val wave = (sin(2.0 * PI * freq * t) * 0.7 + sin(4.0 * PI * freq * t) * 0.3)
                        shortBuffer[i] = (wave * env * Short.MAX_VALUE).toInt().toShort()
                    }
                    audioTrack.write(shortBuffer, 0, bufferSamples)
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
            }
        }
    }

    private fun stopBgm() {
        bgmRunning = false
        bgmThread?.interrupt()
        bgmThread = null
    }

    private fun playTone(tones: List<Pair<Double, Double>>, isSquare: Boolean = false) {
        if (!isSoundEnabled) return
        thread {
            try {
                var totalSamples = 0
                for (pair in tones) {
                    totalSamples += (sampleRate * pair.second).toInt()
                }
                if (totalSamples <= 0) return@thread

                val buffer = ShortArray(totalSamples)
                var currentSample = 0

                for ((freq, dur) in tones) {
                    val count = (sampleRate * dur).toInt()
                    for (i in 0 until count) {
                        val t = i.toDouble() / sampleRate
                        val envelope = max(0.0, 1.0 - (i.toDouble() / count))
                        val wave = if (isSquare) {
                            if (sin(2.0 * PI * freq * t) >= 0) 0.3 else -0.3
                        } else {
                            sin(2.0 * PI * freq * t)
                        }
                        buffer[currentSample] = (wave * envelope * 0.35 * Short.MAX_VALUE).toInt().toShort()
                        currentSample++
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep((totalSamples * 1000L / sampleRate) + 40)
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun playMove() {
        vibrate(12)
        playTone(listOf(Pair(340.0, 0.04)))
    }

    fun playEat() {
        vibrate(35)
        playTone(listOf(Pair(587.33, 0.05), Pair(783.99, 0.07), Pair(1046.50, 0.10)))
    }

    fun playFall() {
        vibrate(30)
        playTone(listOf(Pair(260.0, 0.05), Pair(180.0, 0.07)), isSquare = true)
    }

    fun playSpike() {
        vibrate(90)
        playTone(listOf(Pair(140.0, 0.12), Pair(80.0, 0.18)), isSquare = true)
    }

    fun playPortalUnlocked() {
        vibrate(40)
        playTone(listOf(Pair(440.0, 0.08), Pair(554.37, 0.08), Pair(659.25, 0.12), Pair(880.0, 0.18)))
    }

    fun playLockedNotice() {
        vibrate(25)
        playTone(listOf(Pair(220.0, 0.06), Pair(180.0, 0.08)), isSquare = true)
    }

    fun playWin() {
        vibrate(60)
        playTone(
            listOf(
                Pair(523.25, 0.09),
                Pair(659.25, 0.09),
                Pair(783.99, 0.09),
                Pair(1046.50, 0.28)
            )
        )
    }

    fun playClick() {
        vibrate(10)
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
