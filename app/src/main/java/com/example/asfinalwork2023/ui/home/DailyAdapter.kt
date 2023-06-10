package com.example.asfinalwork2023.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asfinalwork2023.R

class DailyAdapter (val dailyWeather: List<DailyData>) : RecyclerView.Adapter<DailyAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val dailyDate: TextView = view.findViewById(R.id.dailyDate)
        val dailyIconDay: ImageView = view.findViewById(R.id.dailyIconDay)
//        val dailyTextDay: TextView = view.findViewById(R.id.dailyTextDay)
        val dailyIconNight: ImageView = view.findViewById(R.id.dailyIconNight)
//        val dailyTextNight: TextView = view.findViewById(R.id.dailyTextNight)
        val tempMin: TextView = view.findViewById(R.id.dailyTempMin)
        val tempMax: TextView = view.findViewById(R.id.dailyTempMax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyAdapter.ViewHolder, position: Int) {
        val daily = dailyWeather[position]
        holder.dailyDate.text = daily.fxDate
        holder.dailyIconDay.setImageResource(daily.iconDay.toInt())
//        holder.dailyTextDay.text = daily.textDay
        holder.dailyIconNight.setImageResource(daily.iconNight.toInt())
//        holder.dailyTextNight.text = daily.textNight
        holder.tempMin.text = daily.tempMin + "℃"
        holder.tempMax.text = daily.tempMax + "℃"
    }

    override fun getItemCount() = dailyWeather.size
}