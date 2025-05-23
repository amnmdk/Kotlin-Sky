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
                    searchJob?.cancel()

                    //Lance une nouvelle recherche
                    searchJob = coroutineScope.launch {
                        try {
                            val query = constraint.toString().trim()
                            Log.d("CityAutoComplete", "Searching for: $query")
                            
                            val suggestions = withContext(Dispatchers.IO) {
                                ApiClient.geoApi.getCitiesByPrefix(query, limit = 10, apiKey = apiKey, "fr")
                            }
                            clear()
                            
                            // ajouter des nouvelles suggestions et filtrer
                            if (suggestions.isNotEmpty()) {
                                val uniqueCities = suggestions
                                    .distinctBy { it.name to it.country }
                                    .take(5)
                                    .map { city ->
                                        val countryCode = city.country
                                        val countryName = getCountryNameInFrench(countryCode)
                                        
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

    private fun getCountryNameInFrench(countryCode: String): String {
        val frenchLocale = Locale("fr", "FR")
        return Locale("", countryCode).getDisplayCountry(frenchLocale)
    }

    fun cleanup() {
        coroutineScope.cancel()
    }
    
    fun extractCityName(suggestion: String): String {
        return suggestion.split(",")[0].trim()
    }
}