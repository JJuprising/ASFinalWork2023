package com.example.asfinalwork2023.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asfinalwork2023.R

class HourlyAdapter (val hourlyWeather: List<HourlyData>) : RecyclerView.Adapter<HourlyAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val hourlyTemp: TextView = view.findViewById(R.id.hourTemp)
        val hourlyIcon: ImageView = view.findViewById(R.id.hourWeatherIcon)
        val hourlyWeather: TextView = view.findViewById(R.id.hourWeather)
        val hourTime: TextView = view.findViewById(R.id.hourTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourly = hourlyWeather[position]
        holder.hourlyTemp.text = hourly.temp + "℃"
        holder.hourlyIcon.setImageResource(hourly.icon.toInt())
        holder.hourlyWeather.text = hourly.text
        holder.hourTime.text = hourly.fxTime
    }

    override fun getItemCount() = hourlyWeather.size
}