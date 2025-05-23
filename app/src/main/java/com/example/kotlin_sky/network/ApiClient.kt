package com.example.kotlin_sky.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val GEO_URL = "https://api.openweathermap.org/geo/1.0/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val geoRetrofit = Retrofit.Builder()
        .baseUrl(GEO_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val weatherApi: WeatherApi = weatherRetrofit.create(WeatherApi::class.java)
    val geoApi: GeocodingApi = geoRetrofit.create(GeocodingApi::class.java)
}
