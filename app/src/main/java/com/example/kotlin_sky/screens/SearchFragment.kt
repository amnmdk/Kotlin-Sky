package com.example.kotlin_sky.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var searchBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter

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

        adapter = SearchAdapter(results) { city ->
            val isFav = FavoriteManager.isFavorite(requireContext(), city)
            if (isFav) {
                FavoriteManager.removeFavorite(requireContext(), city)
                Toast.makeText(requireContext(), "$city retirée des favoris", Toast.LENGTH_SHORT).show()
            } else {
                FavoriteManager.addFavorite(requireContext(), city)
                Toast.makeText(requireContext(), "$city ajoutée aux favoris", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        searchBtn.setOnClickListener {
            val city = searchInput.text.toString().trim()
            if (city.isNotBlank()) {
                searchCity(city)
            } else {
                Toast.makeText(requireContext(), "Entrez une ville", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchCity(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val useFahrenheit = prefs.getBoolean("isFahrenheit", false)
                val unit = if (useFahrenheit) "imperial" else "metric"

                val response = ApiClient.weatherApi.getWeatherByCity(city, apiKey, unit)

                withContext(Dispatchers.Main) {
                    results.clear()
                    results.add(response)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", "Erreur lors de la recherche", e)
                    Toast.makeText(requireContext(), "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
