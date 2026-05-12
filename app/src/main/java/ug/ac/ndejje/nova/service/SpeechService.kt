package ug.ac.ndejje.nova.service

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText = _recognizedText.asStateFlow()

    private var onResultCallback: ((String) -> Unit)? = null
    private var isWakeWordMode = false
    private var onWakeWordDetected: (() -> Unit)? = null

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _isListening.value = false
                if (isWakeWordMode) {
                    restartListening()
                }
            }

            override fun onError(error: Int) {
                _isListening.value = false
                if (isWakeWordMode) {
                    restartListening()
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    if (isWakeWordMode) {
                        if (text.contains("Nova", ignoreCase = true)) {
                            onWakeWordDetected?.invoke()
                        } else {
                            restartListening()
                        }
                    } else {
                        _recognizedText.value = text
                        onResultCallback?.invoke(text)
                    }
                } else if (isWakeWordMode) {
                    restartListening()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (isWakeWordMode) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        if (text.contains("Nova", ignoreCase = true)) {
                            speechRecognizer.stopListening() // Stop current session
                            onWakeWordDetected?.invoke()
                        }
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening(callback: (String) -> Unit) {
        isWakeWordMode = false
        onResultCallback = callback
        val intent = createRecognizerIntent()
        speechRecognizer.startListening(intent)
    }

    fun startWakeWordDetection(callback: () -> Unit) {
        isWakeWordMode = true
        onWakeWordDetected = callback
        val intent = createRecognizerIntent()
        speechRecognizer.startListening(intent)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private fun restartListening() {
        if (!isWakeWordMode) return

        val delay = when {
            isCharging() -> 100L // Fast restart if charging
            !powerManager.isInteractive -> 3000L // Screen off: wait 3 seconds
            powerManager.isPowerSaveMode -> 5000L // Power save: wait 5 seconds
            else -> 1000L // Normal: wait 1 second
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isWakeWordMode) {
                try {
                    val intent = createRecognizerIntent()
                    speechRecognizer.startListening(intent)
                } catch (e: Exception) {
                    handler.postDelayed({ restartListening() }, 5000)
                }
            }
        }, delay)
    }

    private fun isCharging(): Boolean {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    fun stopListening() {
        isWakeWordMode = false
        handler.removeCallbacksAndMessages(null)
        speechRecognizer.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        isWakeWordMode = false
        handler.removeCallbacksAndMessages(null)
        speechRecognizer.destroy()
    }
}
