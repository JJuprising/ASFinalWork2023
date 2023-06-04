package com.example.asfinalwork2023.ui.home

data class Weather (
    var now: Now
)
data class Now(
    var icon: String,
    var temp: String,       //温度，默认单位：摄氏度
    var feelsLike: String,  //体感温度，默认单位：摄氏度
    var text: String,       //天气状况的文字描述，包括阴晴雨雪等天气状态的描述
    var windDir: String,    //风向
    var windScale: String,  //风力等级
    var humidity: String,   //相对湿度，百分比数值
)