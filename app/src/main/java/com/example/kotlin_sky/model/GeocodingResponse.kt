package com.example.kotlin_sky.model

data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
    val local_names: Map<String, String>? = null
)