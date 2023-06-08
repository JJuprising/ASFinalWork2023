package com.example.asfinalwork2023.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentHomeBinding
import com.example.asfinalwork2023.ui.notifications.NotificationsFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.LineChartView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.thread

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


    //折线图
    private lateinit var chart: LineChartView
    private var maxNumberOfLines: Int = 4;
    private var numberOfPoints: Int = 24; //显示几个点
    private var number: Int = 50; //纵轴最大值
    private var yTop: Int = 50; //纵轴最大值
    private var yBottom: Int = 50; //纵轴最大值
    private val randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }
    private var shape: ValueShape = ValueShape.CIRCLE
    private var hourlyTime: List<String>?=null
    private var hourlyTemp: List<Int>?=null
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


        //折线图
        var hourlyurl="https://devapi.qweather.com/v7/weather/24h?location=$locate&key=$key"
        readHourlyJSONData(hourlyurl)

        var hourlyTempBtn=binding.hourlyBtn
        hourlyTempBtn.setOnClickListener {
            chart = binding.chart
            generateData();
            resetViewport();
        }

        return root
    }

    // 折线图
    //读取逐小时天气
    private fun readHourlyJSONData(url: String) {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                Log.d("responseData", responseData.toString())
                if (responseData != null) {
                    dealHourlyData(responseData)
                    Log.d("responseData", responseData)
                    Log.d("url",url)
//                  println(responseData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dealHourlyData(responseData: String) {
        val gson = Gson()
        val cityData = gson.fromJson(responseData, JsonObject::class.java)
        val hourlyData =
            gson.fromJson(
                cityData.getAsJsonArray("hourly"),
                Array<NotificationsFragment.HourlyData>::class.java
            ).toList()

        val City = NotificationsFragment.CityRainProbability(hourlyData)
        hourlyTime = City.getHourlyTime().toList() //时间列表
        hourlyTemp = City.getHourlyTemp().toList()//温度列表
        for (t in hourlyTemp!!){
            Log.d("hourlyTemp", t.toString())
        }
        var maxTemp=-100
        var minTemp=100
        //获取温度最大值和最小值，设置折线图显示范围
        for(i in 0 until numberOfPoints){
            if(hourlyTemp!![i]<minTemp){
                minTemp=hourlyTemp!![i]
            }
            if(hourlyTemp!![i]>maxTemp){
                maxTemp=hourlyTemp!![i]
            }
        }
        Log.d("最大值", maxTemp.toString())
        Log.d("最小值", minTemp.toString())
        yTop=maxTemp+10
        yBottom=minTemp-10

    }

    private fun generateData() {
        val lines: MutableList<Line> = ArrayList()
        val axisXValues: MutableList<AxisValue> = ArrayList()
        //横坐标
        for (i in 0 until numberOfPoints) {
            axisXValues.add(i, AxisValue(i.toFloat()).setLabel(hourlyTime!![i]))
        }
        //纵坐标
        val values: MutableList<PointValue> = ArrayList()
        for (j in 0 until numberOfPoints) {

                values.add(PointValue(j.toFloat(), hourlyTemp!![j].toFloat()))


        }

        val line = Line(values)
        line.setColor(ChartUtils.pickColor())    //设置颜色随机
        line.setShape(shape)         //设置形状
        line.setCubic(true)          //设置线为曲线，反之为折线
        line.setFilled(true)          //设置填满
        line.setHasLabels(true)    //显示便签
        line.setHasLabelsOnlyForSelected(true)
        line.setHasLines(true)
        line.setHasPoints(true)
        lines.add(line)


        val data = LineChartData(lines)

        data.setAxisXBottom(
            Axis(axisXValues)
                .setHasLines(true)
                .setTextColor(Color.BLACK)
                .setName("时间")
                .setHasTiltedLabels(true)
                .setMaxLabelChars(4)
        )
        data.setAxisYLeft(
            Axis()
                .setHasLines(true)
                .setName("温度")
                .setTextColor(Color.BLACK)
                .setMaxLabelChars(2)
        )
        data.setBaseValue(Float.NEGATIVE_INFINITY)
        chart.setLineChartData(data)
    }



    //折线图
    private fun resetViewport() {
        var v: Viewport = Viewport(chart.maximumViewport);
        v.bottom = yBottom.toFloat()
        v.top = yTop.toFloat()
        v.left = 1.0F;
        v.right = (numberOfPoints - 1).toFloat(); //显示点数
        chart.maximumViewport = v;
        chart.currentViewport = v;

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