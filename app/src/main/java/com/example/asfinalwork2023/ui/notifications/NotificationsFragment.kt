package com.example.asfinalwork2023.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentNotificationsBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread


class NotificationsFragment : Fragment() {


    private var preLongitude: Double = 113.26
    private var preLatitude: Double = 23.13
    private var _binding: FragmentNotificationsBinding? = null
    private var mMapView: MapView? = null//地图组件
    private var mBaiduMap: BaiduMap? = null //地图总控制器
    private var mLocationClient: LocationClient? = null //定位服务
    private var preStatus: MapStatusUpdate? = null //当前定位状态
    private val citiesRain = mutableListOf<City>()//城市降雨数据
    private var preAddress: String? = null//当前地址
    private var maxRainIndex: Int = 0 //当前地址最大降雨的小时
    private var maxRainValue: Double = 0.0 //当前地址24小时内最大降雨概率
    private var mInfoWindow: InfoWindow? = null//信息窗

    //降雨 和风天气
    var hourlyPop: List<Double>? = null
    val key = "9b0c92686ed14dceaa9a3ab0607ccb21" //旗源

    //val key="ea11663a22f34113a5a05b5c810b182f" //蛋源
    private lateinit var progressBar: ProgressBar

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val progressText = binding.progressText //进度条提示文字

        // 获取地图组件
        mMapView = binding.bmapView
        mBaiduMap = mMapView!!.map
        mBaiduMap?.isMyLocationEnabled = true
        //进度条
        progressBar = binding.determinateBar
        //信息窗
        val messageBtn = binding.messageBtn
        messageBtn.setBackgroundResource(R.drawable.btn_background);
        //显示定位
        initloc()
        //获取降雨数据
        getRainData()
        //显示降雨热力图
        val showHeatBtn = binding.showHeatBtn
        showHeatBtn.setOnClickListener {
            //显示降雨热力图
            showheat()
            //信息窗
            //计算最大降雨概率
            countRainpro()
            if (maxRainValue == 0.0) {
                //24小时内不会下雨
                messageBtn.text = "$preAddress \n24小时内不会下雨"//信息窗显示当前地址

            } else {
                var rat = maxRainValue.toInt()
                messageBtn.text = "$preAddress \n $maxRainIndex 小时后有$rat %概率会下雨"
            }
            //构造InfoWindow
            //point 描述的位置点
            //-100 InfoWindow相对于point在y轴的偏移量
            mInfoWindow = InfoWindow(messageBtn, LatLng(preLatitude, preLongitude), -100)

        }
        // 回调动态热力图帧索引
        mBaiduMap?.setOnHeatMapDrawFrameCallBack(object : BaiduMap.OnHeatMapDrawFrameCallBack {
            override fun frameIndex(indexCallBack: Int) {
                // 更新进度条和帧数
                progressBar.progress = indexCallBack
//                Log.d("帧数",indexCallBack.toString())
                progressText.text = "$indexCallBack 小时后"
            }
        })

        //点击按钮回到当前位置
        val setStatusBtn = binding.setStatusBtn
        setStatusBtn.setOnClickListener {
            //设置当前视图位置
            mBaiduMap?.animateMapStatus(preStatus) //动画的方式到中间

        }
        return root
    }

    //计算当前位置最大降雨概率
    private fun countRainpro() {
        val preCity = citiesRain.firstOrNull()
        maxRainIndex = -1
        maxRainValue = Double.MIN_VALUE

        preCity?.let { city ->
            val attributeList = city.hourlyProbabilities

            if (attributeList != null) {
                for ((index, value) in attributeList.withIndex()) {
                    if (value > maxRainValue) {
                        maxRainValue = value
                        maxRainIndex = index
                    }
                }
            }
        }

//        // 输出索引和最大值
//        println("最大值的索引：$maxIndex")
//        println("最大值：$maxValue")

    }

    private fun showheat() {
        // 创建热力图数据
        val builder = HeatMap.Builder()

        // 添加热力图数据点
        // 添加热力图数据点


        //构造帧数据
        //帧数据

        //24帧数据
        val frames = MutableList(24) { mutableListOf<WeightedLatLng>() }

        // 遍历每个城市
        citiesRain.forEach { city ->
            val hourProbabilities = city.hourlyProbabilities

            // 遍历每个小时的降雨概率
            hourProbabilities?.forEachIndexed { index, probability ->
                val weightedLatLng = WeightedLatLng(city.latLng, probability)

//                Log.d("数据",weightedLatLng.mLatLng.toString())
                frames[index].add(weightedLatLng)
                // 打印每一帧的数据
                //println("Frame $index: ${weightedLatLng.mLatLng.latitude}, ${weightedLatLng.mLatLng.longitude}: ${weightedLatLng.intensity}")
            }
        }

        Log.d("测试", "1")
        builder.weightedDatas(frames)

// 设置开始动画属性：开启初始动画，时长100毫秒，动画缓动函数类型为线性
        // 设置开始动画属性：开启初始动画，时长500毫秒，动画缓动函数类型为线性
        val init = HeatMapAnimation(true, 100, HeatMapAnimation.AnimationType.Linear)
        // 设置帧动画属性：开启帧动画，时长10000毫秒，动画缓动函数类型为线性
        val frame = HeatMapAnimation(true, 10000, HeatMapAnimation.AnimationType.Linear)
        builder.initAnimation(init)
        builder.frameAnimation(frame)
// 设置热力图半径范围
        builder.radius(35)

// 设置热力图渐变颜色
        val colors = intArrayOf(
            Color.rgb(255, 0, 0), Color.rgb(0, 225, 0), Color.rgb(0, 0, 200)
        )
        builder.gradient(Gradient(colors, floatArrayOf(0.2f, 0.5f, 1.0f)))
        builder.maxIntensity(100.0f)
        builder.opacity(0.8)
        val heatMapData = builder.build()
        Log.d("showHeat", "添加覆盖物")
        // 添加热力图覆盖物
        mBaiduMap?.addHeatMap(heatMapData)
        mBaiduMap?.startHeatMapFrameAnimation()

    }

    private fun getRainData() {
        thread {
            //城市列表
            val cities = listOf(
                Pair(preLatitude, preLongitude),//当前位置
                Pair(39.90, 116.41),  // 北京
                Pair(31.23, 121.47),  // 上海
                Pair(23.13, 113.26),  // 广州
                Pair(23.16, 113.04),//华师
                Pair(30.57, 104.07),  // 成都
                Pair(39.91, 116.36),  // 北京
                Pair(22.54, 114.06),  // 深圳
                Pair(31.97, 99.90),   // 西宁
                Pair(34.34, 108.94),  // 西安
                Pair(25.04, 102.71),  // 昆明
                Pair(22.82, 108.37),  // 南宁
                Pair(30.27, 120.15),  // 杭州
                Pair(36.67, 117.00),  // 济南
                Pair(38.04, 114.51),  // 石家庄
                Pair(39.08, 117.20),  // 天津
                Pair(39.47, 106.21),  // 呼和浩特
                Pair(30.55, 114.34),  // 武汉
                Pair(22.20, 113.55),  // 澳门
                Pair(38.91, 121.62),  // 大连
                Pair(34.26, 108.95)   // 咸阳

            ) // 假设要查询的城市经纬度列表
            //天气数据 City( LatLng(39.904989, 116.405285), listOf(0.5, 0.8, 1.0, ...)), // 城市1的数据

            Log.d("纬度1", preLatitude.toString())
            Log.d("经度1", preLongitude.toString())

            //获取每个城市降雨数据，先放入citiesRain中
            for (city in cities) {
                val location = city.second.toString() + "," + city.first.toString()
                println(location)
                val url = "https://devapi.qweather.com/v7/weather/24h?location=$location&key=$key"
                println(url)

                readJSONData(url, city)
//                val hourlyRainProb = hourlyPop?.toList()
//
////            val cityWeather = CityWeather(city.first, city.second) //每个城市的经纬度
////            val hourlyRainProb = cityWeather.getHourlyRainProb() //每个城市逐小时降雨数据
//                val thiscity = City(LatLng(city.first, city.second), hourlyRainProb)
//                Log.d("降雨数据",thiscity.hourlyProbabilities.toString()) //输出是null
//                citiesRain.add(thiscity)
            }
        }
    }

    //地图初始化
    private fun initloc() {
        //定位初始化
        //mLocationClient = LocationClient(this)

        //通过LocationClientOption设置LocationClient相关参数

        //定位初始化
        //定位初始化
        mLocationClient = LocationClient(requireActivity())

        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps

        option.setCoorType("bd09ll") // 设置坐标类型

        option.setScanSpan(1000)
        option.setAddrType("all")
        option.setIsNeedAddress(true) // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true) // 可选，设置是否需要地址描述


        //设置locationClientOption
        mLocationClient!!.locOption = option
        //注册LocationListener监听器
        val myLocationListener = MyLocationListener()
        mLocationClient!!.registerLocationListener(myLocationListener)

        //设置缩放
        //缩放级别
        val builder = MapStatus.Builder()
        builder.zoom(8.0f)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))

        //开启地图定位图层
        mLocationClient!!.start()
        //设置当前视图位置
        mBaiduMap?.animateMapStatus(preStatus) //动画的方式到中间
    }

    //构造地图数据
//    inner class MyLocationListener : BDAbstractLocationListener() {
//        override fun onReceiveLocation(location: BDLocation) {
//            //mapView 销毁后不在处理新接收的位置
//            if (location == null || mMapView == null) {
//                return
//            }
//            val locData = MyLocationData.Builder()
//                .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(location.direction).latitude(location.latitude)
//                .longitude(location.longitude).build()
//            mBaiduMap?.setMyLocationData(locData)
//        }
//    }
    //定位监听器
    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return
            }
            val locData = MyLocationData.Builder()
                .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.direction).latitude(location.latitude)
                .longitude(location.longitude).build()
            mBaiduMap?.setMyLocationData(locData)
            //获取经纬度 并保留两位小数''
            preLatitude = String.format("%.2f", location.latitude).toDouble()
            preLongitude = String.format("%.2f", location.longitude).toDouble()

            //获取当前地址
            preAddress = location.addrStr
            val ll = LatLng(location.latitude, location.longitude)

            preStatus = MapStatusUpdateFactory.newLatLng(ll)

            //实时更新信息窗位置
            if (mInfoWindow != null) {
                mBaiduMap?.showInfoWindow(mInfoWindow)
            }

            //mBaiduMap.setMapStatus(status);//直接到中间
            //mBaiduMap?.animateMapStatus(status) //动画的方式到中间


//            Log.d("经度", location.latitude.toString())
//            Log.d("纬度", location.longitude.toString())
//            Log.d("位置",location.addrStr)
        }
    }


    fun readJSONData(url: String, city: Pair<Double, Double>) {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    dealRainData(responseData, city)
                    Log.d("responseData", responseData)
//                  println(responseData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    //提取降雨数据
    private fun dealRainData(JsData: String, city: Pair<Double, Double>) {
        val gson = Gson()
        val cityData = gson.fromJson(JsData, JsonObject::class.java)
        val hourlyData =
            gson.fromJson(cityData.getAsJsonArray("hourly"), Array<HourlyData>::class.java).toList()

        val City = CityRainProbability(hourlyData)
        hourlyPop = City.getHourlyPop()// 获取到一个城市24小时天气数据
        Log.d("hourlyPop", hourlyPop.toString())
        val hourlyRainProb = hourlyPop?.toList()

//            val cityWeather = CityWeather(city.first, city.second) //每个城市的经纬度
//            val hourlyRainProb = cityWeather.getHourlyRainProb() //每个城市逐小时降雨数据
        val thiscity = City(LatLng(city.first, city.second), hourlyRainProb)
        Log.d("降雨数据", thiscity.hourlyProbabilities.toString()) //输出是null
        citiesRain.add(thiscity)

    }

    //城市名称，降雨概率
    data class City(
        val latLng: LatLng,
        val hourlyProbabilities: List<Double>?
    )

    //接收api逐小时数据
    data class HourlyData(
        val fxTime: String,
        val temp: String,
        val icon: String,
        val text: String,
        val wind360: String,
        val windDir: String,
        val windScale: String,
        val windSpeed: String,
        val humidity: String,
        val pop: String,
        val precip: String,
        val pressure: String,
        val cloud: String,
        val dew: String
    )

    //获取降雨概率
    class CityRainProbability(private val hourlyData: List<HourlyData>) {
        //降雨概率
        fun getHourlyPop(): List<Double> {
            return hourlyData.map { it.pop.toDouble() }
        }

        //时间
        fun getHourlyTime(): List<String> {
            return hourlyData.map { it.fxTime.substring(11, 16) }
        }

        //温度
        fun getHourlyTemp(): List<Int> {
            return hourlyData.map { it.temp.toInt() }
        }
    }


    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView!!.onPause()
    }

    override fun onDestroyView() {
        mLocationClient?.stop()
        mBaiduMap?.isMyLocationEnabled = false
        mMapView?.onDestroy()
        mMapView = null
        _binding = null
        super.onDestroyView()

//        mMapView?.onDestroy();
    }

}
