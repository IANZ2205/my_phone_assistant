package ug.ac.ndejje.nova.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ug.ac.ndejje.nova.domain.model.Memory

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories WHERE `key` = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): Memory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory)

    @Query("DELETE FROM memories WHERE `key` = :key")
    suspend fun deleteMemoryByKey(key: String)

    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<Memory>>
}
