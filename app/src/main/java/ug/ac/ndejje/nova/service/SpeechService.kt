package ug.ac.ndejje.nova.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    private fun restartListening() {
        if (!isWakeWordMode) return
        val intent = createRecognizerIntent()
        speechRecognizer.startListening(intent)
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
        speechRecognizer.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}
