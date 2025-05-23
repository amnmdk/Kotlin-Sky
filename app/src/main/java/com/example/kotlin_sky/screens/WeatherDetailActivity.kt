package com.example.kotlin_sky.screens

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kotlin_sky.R
import com.example.kotlin_sky.model.WeatherResponse
import com.example.kotlin_sky.network.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var cityNameTextView: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var backButton: ImageButton
    
    // TextViews for weather details
    private lateinit var tempTextView: TextView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var minMaxTextView: TextView
    private lateinit var weatherDescTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var windDirTextView: TextView
    private lateinit var sunriseTextView: TextView
    private lateinit var sunsetTextView: TextView
    
    private val apiKey = "c0a6d33c227080b7d564e89e11a0c946"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)
        
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        
        initializeViews()
        
        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }
        
        val cityName = intent.getStringExtra("city_name") ?: return
        cityNameTextView.text = cityName
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val useFahrenheit = prefs.getBoolean("isFahrenheit", false)
                val unit = if (useFahrenheit) "imperial" else "metric"
                
                val response = ApiClient.weatherApi.getWeatherByCity(cityName, apiKey, unit)
                
                withContext(Dispatchers.Main) {
                    displayWeatherDetails(response, useFahrenheit)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    weatherDescTextView.text = "Erreur : ${e.message}"
                }
            }
        }
    }
    
    private fun initializeViews() {
        cityNameTextView = findViewById(R.id.cityNameTextView)
        weatherIcon = findViewById(R.id.weatherIcon)
        backButton = findViewById(R.id.backButton)
        
        // Initialize all detail text views
        tempTextView = findViewById(R.id.temperatureTextView)
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView)
        minMaxTextView = findViewById(R.id.minMaxTextView)
        weatherDescTextView = findViewById(R.id.weatherDescTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        pressureTextView = findViewById(R.id.pressureTextView)
        cloudsTextView = findViewById(R.id.cloudsTextView)
        windTextView = findViewById(R.id.windTextView)
        windDirTextView = findViewById(R.id.windDirectionTextView)
        sunriseTextView = findViewById(R.id.sunriseTextView)
        sunsetTextView = findViewById(R.id.sunsetTextView)
    }
    
    // Handle back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun displayWeatherDetails(weather: WeatherResponse, isFahrenheit: Boolean) {
        val tempUnit = if (isFahrenheit) "°F" else "°C"
        val speedUnit = if (isFahrenheit) "mph" else "m/s"
        
        // Load weather icon from OpenWeatherMap API
        val iconCode = weather.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        Picasso.get().load(iconUrl).into(weatherIcon)
        
        // Set city name and country
        cityNameTextView.text = "${weather.name}, ${weather.sys.country}"
        
        // Set all detail texts with bold and underlined labels
        setDetailText(tempTextView, "Température: ", "${weather.main.temp}$tempUnit")
        setDetailText(feelsLikeTextView, "Ressenti: ", "${weather.main.feels_like}$tempUnit")
        setDetailText(minMaxTextView, "Min/Max: ", "${weather.main.temp_min}$tempUnit / ${weather.main.temp_max}$tempUnit")
        setDetailText(weatherDescTextView, "Météo: ", weather.weather[0].description)
        setDetailText(humidityTextView, "Humidité: ", "${weather.main.humidity}%")
        setDetailText(pressureTextView, "Pression: ", "${weather.main.pressure} hPa")
        setDetailText(cloudsTextView, "Nuages: ", "${weather.clouds.all}%")
        setDetailText(windTextView, "Vent: ", "${weather.wind.speed} $speedUnit")
        setDetailText(windDirTextView, "Direction: ", "${weather.wind.deg}°")
        setDetailText(sunriseTextView, "Lever du soleil: ", formatTime(weather.sys.sunrise))
        setDetailText(sunsetTextView, "Coucher du soleil: ", formatTime(weather.sys.sunset))
    }
    
    private fun setDetailText(textView: TextView, label: String, value: String) {
        val spannableString = SpannableString(label + value)
        
        // Make label bold and underlined
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), 
            0, 
            label.length, 
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        spannableString.setSpan(
            UnderlineSpan(), 
            0, 
            label.length, 
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Add color to headers based on their meaning
        when {
            label.contains("Température") -> spannableString.setSpan(ForegroundColorSpan(Color.RED), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            label.contains("Ressenti") -> spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            label.contains("Humidité") -> spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.teal)), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            label.contains("Vent") -> spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.accentBlue)), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            label.contains("Direction") -> spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.accentBlue)), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            label.contains("Nuages") -> spannableString.setSpan(ForegroundColorSpan(Color.GRAY), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        textView.text = spannableString
    }
    
    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}