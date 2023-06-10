package com.example.asfinalwork2023.ui.home

data class Weather (
    var now: WeatherNow
)
data class WeatherNow(
    var icon: String,
    var temp: String,       //温度，默认单位：摄氏度
    var feelsLike: String,  //体感温度，默认单位：摄氏度
    var text: String,       //天气状况的文字描述，包括阴晴雨雪等天气状态的描述
    var windDir: String,    //风向
    var windScale: String,  //风力等级
    var humidity: String,   //相对湿度，百分比数值
)
data class Air(
    var now: AirNow
)
data class AirNow(
    var category: String    //空气质量
)
data class Hourly(          //未来24小时气温和天气
    var hourly: List<HourlyData>
)
data class HourlyData(
    var fxTime: String,
    var temp: String,
    var icon: String,
    var text: String
)
data class Daily(
    var daily:List<DailyData>
)
data class DailyData(
    var fxDate: String,
    var tempMax: String,
    var tempMin: String,
    var iconDay: String,
    var textDay: String,
    var iconNight: String,
    var textNight: String
)
data class Live(
    var daily: List<LiveData>
)
data class LiveData(
    var name: String,
    var category: String,
    var text: String
)