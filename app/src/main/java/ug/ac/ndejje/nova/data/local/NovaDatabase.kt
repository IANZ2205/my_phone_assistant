package ug.ac.ndejje.nova.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ug.ac.ndejje.nova.domain.model.Memory

@Database(entities = [Memory::class], version = 1, exportSchema = false)
abstract class NovaDatabase : RoomDatabase() {
    abstract val memoryDao: MemoryDao

    companion object {
        const val DATABASE_NAME = "nova_db"
    }
}
