package com.example.kotlin_sky.network

import com.example.kotlin_sky.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("direct")
    suspend fun getCitiesByPrefix(
        @Query("q") cityPrefix: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String,
        @Query("lang") language: String = "fr"  // Default to French
    ): List<GeocodingResponse>
}