package com.example.kotlin_sky.adapters

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.kotlin_sky.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CityAutoCompleteAdapter(
    context: Context,
    private val apiKey: String
) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = ArrayList<String>()
                filterResults.count = 0
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (constraint != null && constraint.length >= 2) {
                    // Cancel any ongoing job
                    searchJob?.cancel()
                    
                    // Start a new search
                    searchJob = coroutineScope.launch {
                        try {
                            val query = constraint.toString().trim()
                            Log.d("CityAutoComplete", "Searching for: $query")
                            
                            val suggestions = withContext(Dispatchers.IO) {
                                // Include lang=fr parameter for French results when possible
                                ApiClient.geoApi.getCitiesByPrefix(query, limit = 10, apiKey = apiKey, "fr")
                            }
                            
                            // Clear previous suggestions
                            clear()
                            
                            // Add new suggestions - filter out duplicates
                            if (suggestions.isNotEmpty()) {
                                val uniqueCities = suggestions
                                    .distinctBy { it.name to it.country } // Remove exact duplicates
                                    .take(5) // Only take top 5 results
                                    .map { city ->
                                        // Use French country name if available
                                        val countryCode = city.country
                                        val countryName = getCountryNameInFrench(countryCode)
                                        
                                        // Format suggestion as "City, Country" without state/region
                                        "${city.name}, ${countryName}"
                                    }
                                
                                addAll(uniqueCities)
                                notifyDataSetChanged()
                                Log.d("CityAutoComplete", "Found ${uniqueCities.size} suggestions: $uniqueCities")
                            } else {
                                Log.d("CityAutoComplete", "No suggestions found")
                            }
                        } catch (e: Exception) {
                            Log.e("CityAutoComplete", "Error fetching suggestions", e)
                        }
                    }
                }
            }
        }
    }

    // Get French country names from country codes
    private fun getCountryNameInFrench(countryCode: String): String {
        val frenchLocale = Locale("fr", "FR")
        return Locale("", countryCode).getDisplayCountry(frenchLocale)
    }

    // Clean up resources when adapter is no longer used
    fun cleanup() {
        coroutineScope.cancel()
    }
    
    // Helper method to extract just the city name from a formatted suggestion
    fun extractCityName(suggestion: String): String {
        return suggestion.split(",")[0].trim()
    }
}