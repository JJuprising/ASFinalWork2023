package com.example.asfinalwork2023.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentHomeBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.checkDuration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.thread
import kotlin.math.log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var url: String
    private lateinit var locate: String
    private val cityMap = mutableMapOf<String, City>()

    private val key = "9b0c92686ed14dceaa9a3ab0607ccb21"

    private lateinit var weatherBackground: ImageView
    private lateinit var temperature: TextView
    private lateinit var weather: TextView
    private lateinit var feelsLike: TextView
    private lateinit var wind: TextView
    private lateinit var air: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherContent: TextView
    private lateinit var location: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //url设置
        locate = "101280803"
        url = "https://devapi.qweather.com/v7/weather/now?location=$locate&key=$key"

        //绑定
        weatherBackground = binding.weatherBackground
        temperature = binding.temperature
        weather = binding.weather
        feelsLike = binding.feelsLike
        wind = binding.wind
        air = binding.air
        weatherIcon = binding.weatherIcon
        weatherContent = binding.weatherContent
        location = binding.location
        //线程读取JSON
        readJSONData()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readCityMap()
        //读取城市列表
        val locateName = cityMap[locate]?.Location_Name
        location.text = locateName + "区"
        //实现Fragment跳转
        location.setOnClickListener {
            val intent = Intent(requireContext(), SelectCityActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume(){
        super.onResume()
        //更新URL
        locate = LocateID.locate
        Log.d("home","onResume")
        Log.d("home",locate)
        url = "https://devapi.qweather.com/v7/weather/now?location=$locate&key=$key"
        val locateName = cityMap[locate]?.Location_Name
        val cityName = cityMap[locate]?.cityName
        location.text = "$locateName $cityName"
        readJSONData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //读取JSON数据
    private fun readJSONData(){
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null){
                    dealWithJSONData(responseData)
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    //处理JSON数据
    private fun dealWithJSONData(JsData: String){
        val gson = Gson()
        val data: Weather = gson.fromJson(JsData, Weather::class.java)
        val code = data.now.icon
        var resourceId = resources.getIdentifier("ic_$code", "drawable", context?.packageName)
        if (code == "151"){
            resourceId = resources.getIdentifier("ic_101", "drawable", context?.packageName)
        }
        if (code == "152"){
            resourceId = resources.getIdentifier("ic_102", "drawable", context?.packageName)
        }
        requireActivity().runOnUiThread{
            weatherIcon.setImageResource(resourceId)
            when (code.toInt()) {
                100, 103 -> {
                    Glide.with(this).load(R.drawable.bg_clear_day).centerCrop().into(weatherBackground)
                }
                101, 104, 151 -> {
                    Glide.with(this).load(R.drawable.bg_cloudy).centerCrop().into(weatherBackground)
                }
                102 -> {
                    Glide.with(this).load(R.drawable.bg_partly_cloudy_day).centerCrop().into(weatherBackground)
                }
                150, 153 -> {
                    Glide.with(this).load(R.drawable.bg_clear_night).centerCrop().into(weatherBackground)
                }
                152 -> {
                    Glide.with(this).load(R.drawable.bg_partly_cloudy_night).centerCrop().into(weatherBackground)
                }
                in 300..399 -> {
                    Glide.with(this).load(R.drawable.bg_rain).centerCrop().into(weatherBackground)
                }
                in 400..499 -> {
                    Glide.with(this).load(R.drawable.bg_snow).centerCrop().into(weatherBackground)
                }
                in 500..515 -> {
                    Glide.with(this).load(R.drawable.bg_fog).centerCrop().into(weatherBackground)
                }
                in 900..999 -> {
                    Glide.with(this).load(R.drawable.bg_clear_day).centerCrop().into(weatherBackground)
                }
            }
            temperature.text = data.now.temp + "℃"
            weather.text = data.now.text
            feelsLike.text = "体感温度：" + data.now.feelsLike + "℃"
            wind.text = data.now.windDir + data.now.windScale + "级 | 湿度：" + data.now.humidity + "%"
        }
    }

    private fun readCityMap(){
        val csvFile = context?.assets?.open("citylist.csv")
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
                    cityMap[locationId] = city
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}