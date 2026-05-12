package ug.ac.ndejje.nova.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ug.ac.ndejje.nova.data.local.MemoryDao
import ug.ac.ndejje.nova.data.local.NovaDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNovaDatabase(@ApplicationContext context: Context): NovaDatabase {
        return Room.databaseBuilder(
            context,
            NovaDatabase::class.java,
            NovaDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMemoryDao(database: NovaDatabase): MemoryDao {
        return database.memoryDao
    }
}
