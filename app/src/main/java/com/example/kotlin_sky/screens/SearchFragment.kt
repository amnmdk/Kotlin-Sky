package com.example.kotlin_sky.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_sky.R
import com.example.kotlin_sky.adapters.CityAutoCompleteAdapter
import com.example.kotlin_sky.data.FavoriteManager
import com.example.kotlin_sky.model.WeatherResponse
import com.example.kotlin_sky.network.ApiClient
import com.example.kotlin_sky.screens.adapter.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var searchInput: AutoCompleteTextView
    private lateinit var searchBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var autoCompleteAdapter: CityAutoCompleteAdapter

    private val results = mutableListOf<WeatherResponse>()
    private val apiKey = "c0a6d33c227080b7d564e89e11a0c946"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchInput = view.findViewById(R.id.editTextCity)
        searchBtn = view.findViewById(R.id.buttonSearch)
        recyclerView = view.findViewById(R.id.recyclerViewResults)

        // Setup autocomplete adapter
        autoCompleteAdapter = CityAutoCompleteAdapter(requireContext(), apiKey)
        searchInput.setAdapter(autoCompleteAdapter)
        searchInput.threshold = 2 // Start showing suggestions after 2 characters

        // Setup search results adapter
        adapter = SearchAdapter(results) { city ->
            val isFav = FavoriteManager.isFavorite(requireContext(), city)
            if (isFav) {
                FavoriteManager.removeFavorite(requireContext(), city)
                Toast.makeText(requireContext(), "$city retirée des favoris", Toast.LENGTH_SHORT).show()
            } else {
                FavoriteManager.addFavorite(requireContext(), city)
                Toast.makeText(requireContext(), "$city ajoutée aux favoris", Toast.LENGTH_SHORT).show()
            }
            adapter.notifyDataSetChanged()
        }

        // Add item click listener to show detail view
        adapter.setOnItemClickListener { weatherResponse ->
            // Navigate to detail activity with the city name
            val intent = Intent(requireContext(), WeatherDetailActivity::class.java)
            intent.putExtra("city_name", weatherResponse.name)
            startActivity(intent)
            
            // Optional: Add a log to verify the click is working
            Log.d("SearchFragment", "Navigating to details for: ${weatherResponse.name}")
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        searchBtn.setOnClickListener {
            val fullCityText = searchInput.text.toString().trim()
            if (fullCityText.isNotBlank()) {
                // Extract just the city name if it's from autocomplete
                val cityName = if (fullCityText.contains(",")) {
                    fullCityText.split(",")[0].trim()
                } else {
                    fullCityText
                }
                searchCity(cityName)
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_city_prompt), Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::autoCompleteAdapter.isInitialized) {
            autoCompleteAdapter.cleanup()
        }
    }

    private fun searchCity(city: String) {
        // Log the city name for debugging
        Log.d("SearchFragment", "Searching for city: $city")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val useFahrenheit = prefs.getBoolean("isFahrenheit", false)
                val unit = if (useFahrenheit) "imperial" else "metric"

                // Add language parameter explicitly for French
                val response = ApiClient.weatherApi.getWeatherByCity(
                    cityName = city,
                    apiKey = apiKey,
                    units = unit,
                    language = "fr"  // Explicitly request French
                )

                withContext(Dispatchers.Main) {
                    results.clear()
                    results.add(response)
                    adapter.notifyDataSetChanged()
                    Log.d("SearchFragment", "Search successful for: $city")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", "Error searching for city: $city", e)
                    Toast.makeText(requireContext(), "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
