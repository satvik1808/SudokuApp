package com.sudoku.app

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticManager {
    private var vibrator: Vibrator? = null

    fun init(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // Feather-light — cell tap / theme toggle
    fun tick() = oneShot(10, 45)

    // Crisp click — tool buttons (undo, erase, pencil)
    fun click() = predefined(VibrationEffect.EFFECT_CLICK) ?: oneShot(22, 110)

    // Satisfying thud — correct number placed
    fun heavyClick() = predefined(VibrationEffect.EFFECT_HEAVY_CLICK) ?: oneShot(38, 210)

    // Double sting — wrong number / mistake
    fun error() = predefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
        ?: waveform(longArrayOf(0, 55, 55, 55), intArrayOf(0, 210, 0, 180), -1)

    // Single escalating pulse — called per cell during wave, intensity 0f–1f
    fun wavePulse(intensity: Float) {
        val amp = (70 + (intensity * 155)).toInt().coerceIn(70, 225)
        oneShot(16, amp)
    }

    // Rising celebration — puzzle complete
    fun victory() = waveform(
        longArrayOf(0, 45, 35, 65, 35, 95, 35, 160),
        intArrayOf(0,  90,  0, 140,  0, 190,  0, 255),
        -1
    )

    // Descending dread — game over / time up
    fun gameOver() = waveform(
        longArrayOf(0, 110, 70, 150, 70, 260),
        intArrayOf(0,  210,  0, 160,  0,  90),
        -1
    )

    // Soft double tap — hint used
    fun hint() = waveform(
        longArrayOf(0, 30, 40, 30),
        intArrayOf(0, 120,  0, 80),
        -1
    )

    // ── private helpers ──────────────────────────────────────────────────────

    private fun predefined(effectId: Int): Unit? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        return try {
            vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
        } catch (_: Exception) { null }
    }

    private fun oneShot(ms: Long, amplitude: Int) {
        vibrator?.vibrate(VibrationEffect.createOneShot(ms, amplitude))
    }

    private fun waveform(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeat))
    }
}
