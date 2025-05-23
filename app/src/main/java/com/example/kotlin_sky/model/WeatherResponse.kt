package com.example.kotlin_sky.model

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val sys: Sys,
    val wind: Wind,
    val timezone: Int,
    val clouds: Clouds
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Clouds(
    val all: Int
)
