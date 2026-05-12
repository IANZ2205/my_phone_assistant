package ug.ac.ndejje.nova.data.repository

import kotlinx.coroutines.flow.Flow
import ug.ac.ndejje.nova.data.local.MemoryDao
import ug.ac.ndejje.nova.data.remote.NovaApiService
import ug.ac.ndejje.nova.service.RootService
import ug.ac.ndejje.nova.domain.model.Memory
import ug.ac.ndejje.nova.domain.model.NovaResponse
import ug.ac.ndejje.nova.domain.repository.NovaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovaRepositoryImpl @Inject constructor(
    private val apiService: NovaApiService,
    private val memoryDao: MemoryDao,
    private val rootService: RootService
) : NovaRepository {

    override suspend fun think(query: String): Result<NovaResponse> {
        return try {
            val response = apiService.think(query)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkStatus(): Result<NovaResponse> {
        return try {
            val response = apiService.getStatus()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveMemory(key: String, value: String) {
        memoryDao.insertMemory(Memory(key = key, value = value))
    }

    override suspend fun getMemory(key: String): String? {
        return memoryDao.getMemoryByKey(key)?.value
    }

    override fun getAllMemories(): Flow<List<Memory>> {
        return memoryDao.getAllMemories()
    }

    override suspend fun executeRootCommand(command: String): Result<String> {
        return rootService.execute(command)
    }

    override suspend fun isRootAvailable(): Boolean {
        return rootService.isRootAvailable()
    }
}
