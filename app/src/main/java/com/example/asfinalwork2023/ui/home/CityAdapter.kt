package com.example.asfinalwork2023.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asfinalwork2023.R

class CityAdapter(val cityList: List<City>) : RecyclerView.Adapter<CityAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val locateId: TextView = view.findViewById(R.id.locateId)
        val cityItem: TextView = view.findViewById(R.id.cityItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.city_item, parent, false)
        val holder = ViewHolder(view)
        holder.cityItem.setOnClickListener {
            val bundle = Bundle()
            val value = holder.locateId.text
            Log.d("adapter",value.toString())
            LocateID.locate = value.toString()
            ActivityCollector.finishAll()
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cityList[position]
        holder.locateId.text = city.Location_ID
        holder.locateId.visibility = View.GONE
        holder.cityItem.text = city.Location_Name+"  "+city.cityName+"  "+city.Province
    }

    override fun getItemCount() = cityList.size
}