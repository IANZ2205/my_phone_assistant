package ug.ac.ndejje.nova.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,    // e.g., "user_name"
    val value: String,  // e.g., "Nasro"
    val timestamp: Long = System.currentTimeMillis()
)
