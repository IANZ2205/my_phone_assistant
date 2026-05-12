package ug.ac.ndejje.nova.data.remote

import ug.ac.ndejje.nova.domain.model.NovaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NovaApiService {
    @GET("/think")
    suspend fun think(
        @Query("query") query: String
    ): NovaResponse

    @GET("/")
    suspend fun getStatus(): NovaResponse
}
