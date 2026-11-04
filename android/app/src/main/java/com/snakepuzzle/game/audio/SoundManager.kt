package com.snakepuzzle.game.audio

import android.content.Context
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
    private val vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    private val sampleRate = 44100

    var isSoundEnabled by mutableStateOf(true)

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
                Thread.sleep((totalSamples * 1000L / sampleRate) + 50)
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio glitches gracefully
            }
        }
    }

    private fun vibrate(durationMs: Long) {
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
        vibrate(15)
        playTone(listOf(Pair(320.0, 0.05)))
    }

    fun playEat() {
        vibrate(30)
        playTone(listOf(Pair(523.25, 0.06), Pair(659.25, 0.06), Pair(783.99, 0.12)))
    }

    fun playFall() {
        vibrate(40)
        playTone(listOf(Pair(260.0, 0.06), Pair(180.0, 0.08)), isSquare = true)
    }

    fun playSpike() {
        vibrate(80)
        playTone(listOf(Pair(140.0, 0.15), Pair(90.0, 0.2)), isSquare = true)
    }

    fun playWin() {
        vibrate(60)
        playTone(
            listOf(
                Pair(523.25, 0.1),
                Pair(659.25, 0.1),
                Pair(783.99, 0.1),
                Pair(1046.50, 0.25)
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
