package ug.ac.ndejje.nova.ui.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ug.ac.ndejje.nova.domain.usecase.GetNovaResponseUseCase
import ug.ac.ndejje.nova.service.SpeechService
import ug.ac.ndejje.nova.service.VoiceService
import javax.inject.Inject

@HiltViewModel
class NovaViewModel @Inject constructor(
    private val getNovaResponseUseCase: GetNovaResponseUseCase,
    private val voiceService: VoiceService,
    private val speechService: SpeechService
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaUiState(messages = listOf(ChatMessage("Hello, I am Nova. Ready for God Mode?", false))))
    val uiState = _uiState.asStateFlow()

    val isListening = speechService.isListening

    fun processCommand(query: String) {
        if (query.isBlank()) return

        val userMessage = ChatMessage(query, true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true
        )

        viewModelScope.launch {
            val response = getNovaResponseUseCase(query)
            val novaMessage = ChatMessage(response, false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + novaMessage,
                isLoading = false
            )
            voiceService.speak(response)
        }
    }

    fun startListening() {
        speechService.startListening { recognizedText ->
            processCommand(recognizedText)
        }
    }

    fun stopListening() {
        speechService.stopListening()
    }

    fun killEverything(context: android.content.Context) {
        // Stop speech and voice services
        speechService.destroy() // Use destroy() instead of stopListening() to release resources
        voiceService.shutdown()

        // Stop the foreground service
        val intent = Intent(context, ug.ac.ndejje.nova.service.NovaService::class.java).apply {
            action = ug.ac.ndejje.nova.service.NovaService.ACTION_STOP_SERVICE
        }
        context.startService(intent)
        
        // Add a system message about the kill switch
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + ChatMessage("EMERGENCY KILL SWITCH ACTIVATED. All systems offline.", false)
        )
    }

    override fun onCleared() {
        super.onCleared()
        voiceService.shutdown()
        speechService.destroy()
    }
}

data class NovaUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
