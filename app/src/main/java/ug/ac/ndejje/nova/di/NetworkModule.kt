package ug.ac.ndejje.nova.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ug.ac.ndejje.nova.data.remote.NovaApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideNovaApiService(okHttpClient: OkHttpClient): NovaApiService {
        // IMPORTANT: Replace with your phone's local IP or 127.0.0.1 if using adb reverse
        // For Termux on the same device, 127.0.0.1 usually works if you use 'adb reverse'
        // or the device's actual local IP address.
        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/") 
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NovaApiService::class.java)
    }
}
