package com.example.asfinalwork2023.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentHomeBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var urlWeatherNow: String          //获取实时天气的URL
    private lateinit var urlAir: String                 //获取空气质量的URL
    private lateinit var urlWeather24: String           //获取24小时天气的URL
    private lateinit var urlWeather7: String            //获取7天天气的URL
    private lateinit var urlLive: String                //获取生活指数URL
    private lateinit var locate: String
    private val cityMap = mutableMapOf<String, City>()          //城市列表
    private val hourlyWeather = mutableListOf<HourlyData>()     //未来24小时天气数据
    private val dailyWeather = mutableListOf<DailyData>()       //未来7天天气数据
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var dailyRecyclerView: RecyclerView

    private val key = "ea11663a22f34113a5a05b5c810b182f"

    private lateinit var weatherBackground: ImageView   //天气背景图
    private lateinit var temperature: TextView          //实时温度
    private lateinit var weather: TextView              //实时天气
    private lateinit var feelsLike: TextView            //体感温度
    private lateinit var wind: TextView                 //风向
    private lateinit var air: TextView                  //空气质量
    private lateinit var weatherIcon: ImageView         //天气图标
    private lateinit var location: TextView
    //生活质量的各个TextView<<<<
    private lateinit var washCar: TextView
    private lateinit var sport: TextView
    private lateinit var clothes: TextView
    private lateinit var UV: TextView
    private lateinit var trip: TextView
    private lateinit var comft: TextView
    private lateinit var traffic: TextView
    private lateinit var spi: TextView
    private lateinit var sportRelative: RelativeLayout
    private lateinit var washCarRelative: RelativeLayout
    private lateinit var clothesRelative: RelativeLayout
    private lateinit var UVRelative: RelativeLayout
    private lateinit var tripRelative: RelativeLayout
    private lateinit var comftRelative: RelativeLayout
    private lateinit var trafficRelative: RelativeLayout
    private lateinit var spiRelative: RelativeLayout
    //>>>>
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

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
        locate = "101280803"//最开始默认南海区
        urlWeatherNow = "https://devapi.qweather.com/v7/weather/now?location=$locate&key=$key"
        urlAir = "https://devapi.qweather.com/v7/air/now?location=$locate&key=$key"
        urlWeather24 = "https://devapi.qweather.com/v7/weather/24h?location=$locate&key=$key"
        urlWeather7 = "https://devapi.qweather.com/v7/weather/7d?location=$locate&key=$key"
        urlLive = "https://devapi.qweather.com/v7/indices/1d?type=0&location=$locate&key=$key"

        //绑定
        weatherBackground = binding.weatherBackground
        temperature = binding.temperature
        weather = binding.weather
        feelsLike = binding.feelsLike
        wind = binding.wind
        air = binding.air
        weatherIcon = binding.weatherIcon
        location = binding.location
        hourlyRecyclerView = binding.hourlyRecyclerView
        dailyRecyclerView = binding.dailyRecyclerView
        washCar = binding.washCarText
        sport = binding.sportText
        clothes = binding.clothesText
        UV = binding.UVText
        trip = binding.tripText
        comft = binding.comftText
        traffic = binding.trafficText
        spi = binding.spiText
        sportRelative = binding.sportRelative
        washCarRelative = binding.washCarRelative
        clothesRelative = binding.clothesRelative
        UVRelative = binding.UVRelative
        tripRelative = binding.tripRelative
        comftRelative = binding.comftRelative
        trafficRelative = binding.trafficRelative
        spiRelative = binding.spiRelative
        //读取JSON
        readJSONData(urlWeatherNow)
        readJSONData(urlAir)
        readJSONData(urlWeather24)
        readJSONData(urlWeather7)
        readJSONData(urlLive)
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
//        Log.d("home","onResume")
//        Log.d("home",locate)
        urlWeatherNow = "https://devapi.qweather.com/v7/weather/now?location=$locate&key=$key"
        urlAir = "https://devapi.qweather.com/v7/air/now?location=$locate&key=$key"
        urlWeather24 = "https://devapi.qweather.com/v7/weather/24h?location=$locate&key=$key"
        urlWeather7 = "https://devapi.qweather.com/v7/weather/7d?location=$locate&key=$key"
        urlLive = "https://devapi.qweather.com/v7/indices/1d?type=0&location=$locate&key=$key"
        val locateName = cityMap[locate]?.Location_Name
        val cityName = cityMap[locate]?.cityName
        location.text = "$locateName $cityName"
        readJSONData(urlWeatherNow)
        readJSONData(urlAir)
        readJSONData(urlWeather24)
        readJSONData(urlWeather7)
        readJSONData(urlLive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //读取JSON数据
    private fun readJSONData(url:String){
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null){
                    when(url){
                        urlWeatherNow->{weatherNow(responseData)}
                        urlAir->{air(responseData)}
                        urlWeather24->{weather24Hour(responseData)}
                        urlWeather7->{weather7Day(responseData)}
                        urlLive->{live(responseData)}
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

    }
    //处理实时天气JSON数据
    private fun weatherNow(JsData: String){
        val gson = Gson()
        val data: Weather = gson.fromJson(JsData, Weather::class.java)
        requireActivity().runOnUiThread {
            val code = data.now.icon
            var resourceId = resources.getIdentifier("ic_$code", "drawable", context?.packageName)
            if (code == "151"){
                resourceId = resources.getIdentifier("ic_101", "drawable", context?.packageName)
            }
            if (code == "152"){
                resourceId = resources.getIdentifier("ic_102", "drawable", context?.packageName)
            }
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
            temperature.text = data.now.temp
            weather.text = data.now.text
            feelsLike.text = "体感 " + data.now.feelsLike + "℃"
            wind.text = data.now.windDir + data.now.windScale + "级 | 湿度:" + data.now.humidity + "%"
        }
    }
    //处理空气质量JSON数据
    private fun air(JsData: String){
        requireActivity().runOnUiThread {
            val gson = Gson()
            val data: Air = gson.fromJson(JsData, Air::class.java)
            air.text = data.now.category
            when(air.text){
                "优"->{air.setTextColor(Color.rgb(0,228,0))}
                "良"->{air.setTextColor(Color.rgb(255,255,0))}
                "轻度污染"->{air.setTextColor(Color.rgb(255,126,0))}
                "中度污染"->{air.setTextColor(Color.rgb(255,0,0))}
                "重度污染"->{air.setTextColor(Color.rgb(153,0,76))}
                "严重污染"->{air.setTextColor(Color.rgb(126,0,35))}
            }
        }
    }
    //处理24小时天气
    private fun weather24Hour(JsData: String){
        requireActivity().runOnUiThread(){
            val gson = Gson()
            val data: Hourly = gson.fromJson(JsData, Hourly::class.java)
            hourlyWeather.clear()
            for (i in 0 until data.hourly.size){
                hourlyWeather.add(data.hourly[i])
                var resourceId = resources.getIdentifier("ic_${hourlyWeather[i].icon}", "drawable", context?.packageName)
                if (hourlyWeather[i].icon == "151"){
                    resourceId = resources.getIdentifier("ic_101", "drawable", context?.packageName)
                }
                if (hourlyWeather[i].icon == "152"){
                    resourceId = resources.getIdentifier("ic_102", "drawable", context?.packageName)
                }
                hourlyWeather[i].icon = resourceId.toString()
                hourlyWeather[i].fxTime = dealHour(data.hourly[i].fxTime)
            }
            hourlyAdapter = HourlyAdapter(hourlyWeather)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            hourlyRecyclerView.layoutManager = layoutManager
            hourlyRecyclerView.adapter = hourlyAdapter
        }
    }
    //处理7天天气
    private fun weather7Day(JsData: String){
        requireActivity().runOnUiThread(){
            val gson = Gson()
            val data: Daily = gson.fromJson(JsData, Daily::class.java)
            dailyWeather.clear()
            for (i in 0 until data.daily.size){
                dailyWeather.add(data.daily[i])
                val resourceIdDay = resources.getIdentifier("ic_${dailyWeather[i].iconDay}", "drawable", context?.packageName)
                var resourceIdNight = resources.getIdentifier("ic_${dailyWeather[i].iconNight}", "drawable", context?.packageName)
                if (dailyWeather[i].iconNight == "151"){
                    resourceIdNight = resources.getIdentifier("ic_101", "drawable", context?.packageName)
                }
                if (dailyWeather[i].iconNight == "152"){
                    resourceIdNight = resources.getIdentifier("ic_102", "drawable", context?.packageName)
                }
                dailyWeather[i].iconDay = resourceIdDay.toString()
                dailyWeather[i].iconNight = resourceIdNight.toString()
                dailyWeather[i].fxDate = dealDate(data.daily[i].fxDate)
                dailyAdapter = DailyAdapter(dailyWeather)
                dailyRecyclerView.layoutManager = LinearLayoutManager(context)
                dailyRecyclerView.adapter = dailyAdapter
            }
        }
    }
    //处理生活指数
    private fun live(JsData: String){
        requireActivity().runOnUiThread {
            val gson = Gson()
            val data:Live = gson.fromJson(JsData, Live::class.java)
            sport.text = data.daily[0].category
            sportRelative.setOnClickListener{
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[0].name)
                    setMessage(data.daily[0].text)
                    setCancelable(true)
                    show()
                } }
            }
            washCar.text = data.daily[1].category
            washCarRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[1].name)
                    setMessage(data.daily[1].text)
                    setCancelable(true)
                    show()
                } }
            }
            clothes.text = data.daily[2].category
            clothesRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[2].name)
                    setMessage(data.daily[2].text)
                    setCancelable(true)
                    show()
                } }
            }
            UV.text = data.daily[4].category
            UVRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[4].name)
                    setMessage(data.daily[4].text)
                    setCancelable(true)
                    show()
                } }
            }
            trip.text = data.daily[5].category
            tripRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[5].name)
                    setMessage(data.daily[5].text)
                    setCancelable(true)
                    show()
                } }
            }
            comft.text = data.daily[7].category
            comftRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[7].name)
                    setMessage(data.daily[7].text)
                    setCancelable(true)
                    show()
                } }
            }
            traffic.text = data.daily[14].category
            trafficRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[14].name)
                    setMessage(data.daily[14].text)
                    setCancelable(true)
                    show()
                } }
            }
            spi.text = data.daily[15].category
            spiRelative.setOnClickListener {
                context?.let { it1 -> AlertDialog.Builder(it1).apply {
                    setTitle(data.daily[15].name)
                    setMessage(data.daily[15].text)
                    setCancelable(true)
                    show()
                } }
            }
        }
    }
    //读取城市列表
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
    //处理时间->小时
    private fun dealHour(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateTime:Date = inputFormat.parse(dateTimeString)!!
        return outputFormat.format(dateTime)
    }
    //处理时间->日期
    private fun dealDate(dateTimeString: String): String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        val dateTime:Date = inputFormat.parse(dateTimeString)!!
        return outputFormat.format(dateTime)
    }
}