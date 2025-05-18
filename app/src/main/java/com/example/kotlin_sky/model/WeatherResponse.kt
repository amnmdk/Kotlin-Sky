package com.example.kotlin_sky.model

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val sys: Sys
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val country: String
)
