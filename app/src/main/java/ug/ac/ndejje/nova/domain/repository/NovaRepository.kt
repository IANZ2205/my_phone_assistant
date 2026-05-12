package ug.ac.ndejje.nova.domain.repository

import kotlinx.coroutines.flow.Flow
import ug.ac.ndejje.nova.domain.model.Memory
import ug.ac.ndejje.nova.domain.model.NovaResponse

interface NovaRepository {
    suspend fun think(query: String): Result<NovaResponse>
    suspend fun checkStatus(): Result<NovaResponse>
    
    // Memory operations
    suspend fun saveMemory(key: String, value: String)
    suspend fun getMemory(key: String): String?
    fun getAllMemories(): Flow<List<Memory>>

    // Root operations
    suspend fun executeRootCommand(command: String): Result<String>
    suspend fun isRootAvailable(): Boolean
}
