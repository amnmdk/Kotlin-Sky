package com.example.kotlin_sky.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_sky.R
import com.example.kotlin_sky.data.FavoriteManager
import com.example.kotlin_sky.model.WeatherResponse
import com.example.kotlin_sky.network.ApiClient
import com.example.kotlin_sky.screens.adapter.WeatherAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout
    private lateinit var emptyMessage: TextView
    private lateinit var emptyIcon: ImageView
    private lateinit var adapter: WeatherAdapter
    private val weatherList = mutableListOf<WeatherResponse>()
    private val apiKey = "c0a6d33c227080b7d564e89e11a0c946"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewWeather)
        emptyLayout = view.findViewById(R.id.emptyLayout)
        emptyMessage = view.findViewById(R.id.textEmptyMessage)
        emptyIcon = view.findViewById(R.id.emptyIcon)

        adapter = WeatherAdapter(weatherList) { city ->
            FavoriteManager.removeFavorite(requireContext(), city)
            Toast.makeText(requireContext(), "$city retirée des favoris", Toast.LENGTH_SHORT).show()
            loadWeatherForFavorites()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        loadWeatherForFavorites()
    }

    private fun loadWeatherForFavorites() {
        weatherList.clear()
        val favorites = FavoriteManager.getFavorites(requireContext())

        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isFahrenheit = prefs.getBoolean("isFahrenheit", false)
        val units = if (isFahrenheit) "imperial" else "metric"

        CoroutineScope(Dispatchers.IO).launch {
            for (city in favorites) {
                try {
                    val response = ApiClient.weatherApi.getWeatherByCity(city, apiKey, units)
                    withContext(Dispatchers.Main) {
                        weatherList.add(response)
                        adapter.notifyDataSetChanged()
                        updateEmptyState()
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Erreur API pour $city", e)
                }
            }

            withContext(Dispatchers.Main) {
                updateEmptyState()
            }
        }
    }

    private fun updateEmptyState() {
        if (weatherList.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
