package ug.ac.ndejje.nova.domain.usecase

import ug.ac.ndejje.nova.domain.repository.NovaRepository
import javax.inject.Inject

class GetNovaResponseUseCase @Inject constructor(
    private val repository: NovaRepository
) {
    suspend operator fun invoke(query: String): String {
        val normalizedQuery = query.lowercase().trim()

        // 1. Check for Learning Patterns (e.g., "My name is...")
        if (normalizedQuery.startsWith("my name is ")) {
            val name = query.substring(11).trim()
            repository.saveMemory("user_name", name)
            return "Got it! I'll remember that your name is $name."
        }

        // 2. Check for Memory Retrieval (e.g., "What is my name?")
        if (normalizedQuery.contains("what is my name")) {
            val name = repository.getMemory("user_name")
            return if (name != null) "Your name is $name." else "I don't know your name yet! You can tell me by saying 'My name is...'"
        }

        // 3. Check for Local Command Patterns
        return when {
            normalizedQuery.contains("battery") -> {
                val batteryLevel = repository.executeRootCommand("dumpsys battery | grep level").getOrNull()
                batteryLevel?.trim() ?: "I couldn't retrieve the battery level."
            }
            normalizedQuery.contains("reboot") -> {
                if (normalizedQuery.contains("nova secure 2205")) {
                    repository.executeRootCommand("reboot")
                    "Rebooting device..."
                } else {
                    "Rebooting is a critical task. Please provide your security code to proceed."
                }
            }
            normalizedQuery.contains("shutdown") -> {
                if (normalizedQuery.contains("nova secure 2205")) {
                    repository.executeRootCommand("reboot -p")
                    "Shutting down device..."
                } else {
                    "Shutdown is restricted. Please authenticate with your security code."
                }
            }
            normalizedQuery == "who are you" -> {
                "I am Nova, your God Mode AI assistant."
            }
            normalizedQuery == "nova secure 2205" -> {
                "Security cleared. How can I help you today?"
            }
            
            // 4. Default: Route to AI Brain (Termux)
            else -> {
                repository.think(query).fold(
                    onSuccess = { it.response },
                    onFailure = { "Brain Error: ${it.message}. Is Termux running?" }
                )
            }
        }
    }
}
