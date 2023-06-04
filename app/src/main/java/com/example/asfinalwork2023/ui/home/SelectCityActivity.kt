package com.example.asfinalwork2023.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.city_item.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class SelectCityActivity : BaseActivity() {

    private val cityList = mutableListOf<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)
        setSupportActionBar(toolBar)
        createCityList()
        cityList.removeAt(0)
        val recyclerView:RecyclerView = findViewById(R.id.cityRecyclerView)
        val adapter = CityAdapter(cityList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun createCityList(){
        val csvFile = assets.open("citylist.csv")
        var line: String?
        val csvSplitBy = ","

        try {
            BufferedReader(InputStreamReader(csvFile)).use { br ->
                // 跳过CSV文件的标题行
                br.readLine()

                while (br.readLine().also { line = it } != null) {
                    val data = line!!.split(csvSplitBy).toTypedArray()

                    val locationId = data[0]
                    val locationName = data[2]
                    val province = data[7]
                    val cityName = data[9]
                    val latitude = data[11]
                    val longitude = data[12]

                    val city = City(locationId, locationName, province, cityName, latitude, longitude)
                    cityList.add(city)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}