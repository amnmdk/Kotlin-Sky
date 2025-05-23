package com.example.kotlin_sky.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_sky.R
import com.example.kotlin_sky.data.FavoriteManager
import com.example.kotlin_sky.model.WeatherResponse
import com.squareup.picasso.Picasso

class SearchAdapter(
    private val results: List<WeatherResponse>,
    private val onFavoriteClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private lateinit var context: Context
    private var onItemClickListener: ((WeatherResponse) -> Unit)? = null

    // Set click listener for items
    fun setOnItemClickListener(listener: (WeatherResponse) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = results[position]

        holder.textCityName.text = item.name
        holder.textTemperature.text = "${item.main.temp.toInt()}°"

        // Load the colorful weather icon
        val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
        Picasso.get().load(iconUrl).into(holder.imageWeather)

        val isFav = FavoriteManager.isFavorite(context, item.name)
        val heartIcon = if (isFav) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        holder.buttonFavorite.setImageResource(heartIcon)

        holder.buttonFavorite.setOnClickListener {
            onFavoriteClick(item.name)
            notifyItemChanged(position)
        }

        // Set item click listener for the whole card
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = results.size

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCityName: TextView = itemView.findViewById(R.id.textCityName)
        val textTemperature: TextView = itemView.findViewById(R.id.textTemperature)
        val imageWeather: ImageView = itemView.findViewById(R.id.imageWeatherSearch)
        val buttonFavorite: ImageButton = itemView.findViewById(R.id.buttonFavorite)
    }
}
