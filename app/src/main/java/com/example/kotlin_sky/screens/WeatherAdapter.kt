package com.example.kotlin_sky.screens.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_sky.R
import com.example.kotlin_sky.model.WeatherResponse
import com.example.kotlin_sky.screens.WeatherDetailActivity
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

        holder.textCity.text = "${item.name}, ${item.sys.country}"
        holder.textTemp.text = "${item.main.temp}°C - ${item.weather[0].description}"

        // Load the weather icon
        val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
        Picasso.get().load(iconUrl).into(holder.imageWeather)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, WeatherDetailActivity::class.java)
            intent.putExtra("city_name", item.name)
            context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener {
            onDeleteFavoriteClick(item.name)
        }
    }

    override fun getItemCount(): Int = data.size
}
