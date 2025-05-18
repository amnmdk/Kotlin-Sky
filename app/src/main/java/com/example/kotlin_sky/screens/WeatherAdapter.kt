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
import com.example.kotlin_sky.model.WeatherResponse
import com.squareup.picasso.Picasso

class WeatherAdapter(
    private val data: List<WeatherResponse>,
    private val onDeleteFavoriteClick: (String) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textCity: TextView = view.findViewById(R.id.textCityName)
        val textTemp: TextView = view.findViewById(R.id.textTemperature)
        val imageWeather: ImageView = view.findViewById(R.id.imageWeather)
        val deleteBtn: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = data[position]
        val context = holder.itemView.context
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isFahrenheit = prefs.getBoolean("isFahrenheit", false)

        val temperature = if (isFahrenheit) {
            val fahrenheitTemp = item.main.temp * 9 / 5 + 32
            String.format("%.1f°F", fahrenheitTemp)
        } else {
            String.format("%.1f°C", item.main.temp)
        }

        holder.textCity.text = "${item.name}, ${item.sys.country}"
        holder.textTemp.text = "$temperature - ${item.weather[0].description}"

        val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
        Picasso.get().load(iconUrl).into(holder.imageWeather)

        holder.deleteBtn.setOnClickListener {
            onDeleteFavoriteClick(item.name)
        }
    }

    override fun getItemCount(): Int = data.size
}
