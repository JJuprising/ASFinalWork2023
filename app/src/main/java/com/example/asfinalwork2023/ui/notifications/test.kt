//package com.example.asfinalwork2023.ui.notifications
//
////import com.baidu.mapapi.map.WeightedLatLng
//import com.baidu.mapapi.model.LatLng
//import com.google.gson.Gson
//import com.google.gson.JsonObject
//import okhttp3.OkHttpClient
//import okhttp3.Request
//
//var hourlyPop: List<Double>? = null
//const val key = "9b0c92686ed14dceaa9a3ab0607ccb21"
//fun main() {
//// 示例实例化
//
//
//    val locate = "101280803"
//    val url = "https://devapi.qweather.com/v7/weather/now?location=$locate&key=$key"
//
//    //城市列表
//    val cities = listOf(
//        Pair(39.90, 116.40), // 北京的经纬度
//        Pair(31.23, 121.47), // 上海的经纬度
//        Pair(23.12, 113.26)  // 广州的经纬度
//    ) // 假设要查询的城市经纬度列表
//
//    //天气数据 City( LatLng(39.904989, 116.405285), listOf(0.5, 0.8, 1.0, ...)), // 城市1的数据
//    val citiesRain = mutableListOf<City>()
//
//
//    val frameData = mutableListOf<WeightedLatLng>()
//    //获取每个城市降雨数据，先放入citiesRain中
//    for (city in cities) {
//        val location = city.second.toString() + "," + city.first.toString()
//        println(location)
//        val url = "https://devapi.qweather.com/v7/weather/24h?location=$location&key=$key"
//        println(url)
//        readJSONData(url)
//        val hourlyRainProb = hourlyPop?.toList()
//
////            val cityWeather = CityWeather(city.first, city.second) //每个城市的经纬度
////            val hourlyRainProb = cityWeather.getHourlyRainProb() //每个城市逐小时降雨数据
//        val thiscity = City(LatLng(city.first, city.second), hourlyRainProb)
//        citiesRain.add(thiscity)
//    }
//
//    //构造帧数据
//    //帧数据
//
//    //24帧
//    val frames = MutableList(24) { mutableListOf<WeightedLatLng>() }
//
//    // 遍历每个城市
//    citiesRain.forEach { city ->
//        val hourProbabilities = city.hourlyProbabilities
//
//        // 遍历每个小时的降雨概率
//        hourProbabilities?.forEachIndexed { index, probability ->
//            println(probability)
//            val weightedLatLng = WeightedLatLng(city.latLng, probability)
//            frames[index].add(weightedLatLng)
//        }
//    }
//// 打印每一帧的数据
//    frames.forEachIndexed { frameIndex, frameData ->
//        println("Frame $frameIndex:")
//        frameData.forEach { weightedLatLng ->
//            println("${weightedLatLng.mLatLng.latitude}, ${weightedLatLng.mLatLng.longitude}: ${weightedLatLng.weight}")
//        }
//    }
//
//}
//
//data class HourlyData(
//    val fxTime: String,
//    val temp: String,
//    val icon: String,
//    val text: String,
//    val wind360: String,
//    val windDir: String,
//    val windScale: String,
//    val windSpeed: String,
//    val humidity: String,
//    val pop: String,
//    val precip: String,
//    val pressure: String,
//    val cloud: String,
//    val dew: String
//)
//
//class CityRainProbability(private val hourlyData: List<HourlyData>) {
//    fun getHourlyPop(): List<Double> {
//        return hourlyData.map { it.pop.toDouble() }
//    }
//}
//
////读取JSON数据 处理一个城市的所有降雨概率
//private fun readJSONData(url: String) {
//
//    try {
//        val client = OkHttpClient()
//        val request = Request.Builder().url(url).build()
//        val response = client.newCall(request).execute()
//        val responseData = response.body?.string()
//        if (responseData != null) {
//            dealRainData(responseData)
//            println(responseData)
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//}
//
//fun dealRainData(JsData: String) {
//    val gson = Gson()
//    val cityData = gson.fromJson(JsData, JsonObject::class.java)
//    val hourlyData =
//        gson.fromJson(cityData.getAsJsonArray("hourly"), Array<HourlyData>::class.java).toList()
//
//    val city = CityRainProbability(hourlyData)
//    hourlyPop = city.getHourlyPop()// 获取到一个城市24小时天气数据
//
//}
//
////每一个数据的类，经纬度+权重
//data class WeightedLatLng(
//    val mLatLng: LatLng,
//    val weight: Double
//)
//
//class CityWeather(private val latitude: Double, private val longitude: Double) {
//    fun getHourlyRainProb(): List<HourlyData> {
//        // 使用天气数据的 API 根据经纬度获取城市的天气数据，提取逐小时降雨概率数据
//        // 这里省略 API 请求的代码，假设获取到了城市的逐小时降雨概率数据
//        val hourlyDataList = mutableListOf<HourlyData>()
//
//        // 假设这里获取到了城市的逐小时降雨概率数据并存储在 hourlyDataList 中
//
//        return hourlyDataList //返回逐小时降雨数据
//    }
//}
//
////城市名称，降雨概率
//data class City(
//    val latLng: LatLng,
//    val hourlyProbabilities: List<Double>?
//)
