package ug.ac.ndejje.nova.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ug.ac.ndejje.nova.data.repository.NovaRepositoryImpl
import ug.ac.ndejje.nova.domain.repository.NovaRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNovaRepository(
        novaRepositoryImpl: NovaRepositoryImpl
    ): NovaRepository
}
