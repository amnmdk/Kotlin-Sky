package com.example.kotlin_sky.screens

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_sky.R
import com.example.kotlin_sky.data.FavoriteManager
import com.example.kotlin_sky.model.WeatherResponse
import com.example.kotlin_sky.network.ApiClient
import com.example.kotlin_sky.screens.adapter.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private val results = mutableListOf<WeatherResponse>()
    private val apiKey = "c0a6d33c227080b7d564e89e11a0c946"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.editTextCity)
        searchButton = findViewById(R.id.buttonSearch)
        recyclerView = findViewById(R.id.recyclerViewSearch)

        adapter = SearchAdapter(results) { city ->
            val isFav = FavoriteManager.isFavorite(this, city)
            if (isFav) {
                FavoriteManager.removeFavorite(this, city)
                Toast.makeText(this, "$city retiré des favoris", Toast.LENGTH_SHORT).show()
            } else {
                FavoriteManager.addFavorite(this, city)
                Toast.makeText(this, "$city ajouté aux favoris", Toast.LENGTH_SHORT).show()
            }
            adapter.notifyDataSetChanged() // 🔁 MAJ visuelle du cœur
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val city = searchInput.text.toString().trim()
            if (city.isNotBlank()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this, "Entrez un nom de ville", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val useFahrenheit = prefs.getBoolean("isFahrenheit", false)
        val unit = if (useFahrenheit) "imperial" else "metric"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.weatherApi.getWeatherByCity(city, apiKey, unit)
                withContext(Dispatchers.Main) {
                    results.clear()
                    results.add(response)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SearchActivity, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", "fetchWeather", e)
                }
            }
        }
    }
}
