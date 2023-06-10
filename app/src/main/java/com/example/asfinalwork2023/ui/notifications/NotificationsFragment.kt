package com.example.asfinalwork2023.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.example.asfinalwork2023.databinding.FragmentNotificationsBinding


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private var mMapView: MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var mLocationClient: LocationClient? = null

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

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

       // 获取地图组件
        mMapView=binding.bmapView
        mBaiduMap = mMapView!!.map
        mBaiduMap?.isMyLocationEnabled = true
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
        mBaiduMap?.setOnHeatMapDrawFrameCallBack { indexCallBack -> // 更新进度条和帧数
            progressBar.progress = indexCallBack
            //                Log.d("帧数",indexCallBack.toString())
            progressText.text = "$indexCallBack 小时后"
        }

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
    }

    private fun showheat() {
        // 创建热力图数据
        val builder = HeatMap.Builder()

        // 添加热力图数据点
        //构造24帧数据
        val frames = MutableList(24) { mutableListOf<WeightedLatLng>() }

        // 遍历每个城市
        citiesRain.forEach { city ->
            val hourProbabilities = city.hourlyProbabilities

            // 遍历每个小时的降雨概率
            hourProbabilities?.forEachIndexed { index, probability ->
                val weightedLatLng = WeightedLatLng(city.latLng, probability)
                frames[index].add(weightedLatLng)
            }
        }

        Log.d("测试", "1")
        builder.weightedDatas(frames)

        // 设置开始动画属性：开启初始动画，时长100毫秒，动画缓动函数类型为线性
        val init = HeatMapAnimation(true, 100, HeatMapAnimation.AnimationType.Linear)
// 设置帧动画属性：开启帧动画，时长800毫秒，动画缓动函数类型为线性
// 设置帧动画属性：开启帧动画，时长800毫秒，动画缓动函数类型为线性
        val frame = HeatMapAnimation(true, 800, HeatMapAnimation.AnimationType.Linear)
        builder.initAnimation(init)
        builder.frameAnimation(frame)
        // 设置热力图半径范围
        builder.radius(35)
        // 设置热力图渐变颜色
        val colors = intArrayOf(
            Color.parseColor("#00FF00"), // 绿色
            Color.parseColor("#FFFF00"), // 黄色
            Color.parseColor("#FF0000")  // 红色
        )
        builder.gradient(Gradient(colors, floatArrayOf(0.2f, 0.5f, 1.0f)))
        builder.maxIntensity(3.1f)
        builder.opacity(0.9)
        val heatMapData = builder.build()

// 添加热力图覆盖物
        mBaiduMap?.addHeatMap(heatMapData)
        mBaiduMap?.startHeatMapFrameAnimation();

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


            //获取每个城市降雨数据，先放入citiesRain中
            for (city in cities) {
                val location = city.second.toString() + "," + city.first.toString()
                println(location)
                val url = "https://devapi.qweather.com/v7/weather/24h?location=$location&key=$key"
                println(url)
                readJSONData(url, city)
            }
        }
    }

    //地图初始化
    private fun initloc() {
        //定位初始化
        mLocationClient = LocationClient(requireActivity())

//通过LocationClientOption设置LocationClient相关参数

//通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps

        option.setCoorType("bd09ll") // 设置坐标类型

        option.setScanSpan(1000)
        option.setAddrType("all");
        option.setIsNeedAddress(true); // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true); // 可选，设置是否需要地址描述


//设置locationClientOption

//设置locationClientOption
        mLocationClient!!.locOption = option

//注册LocationListener监听器

//注册LocationListener监听器
        val myLocationListener = MyLocationListener()
        mLocationClient!!.registerLocationListener(myLocationListener)
//开启地图定位图层
//开启地图定位图层
        mLocationClient!!.start()
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
            //获取经纬度
            val ll = LatLng(location.latitude, location.longitude)
            val status = MapStatusUpdateFactory.newLatLng(ll)

            //mBaiduMap.setMapStatus(status);//直接到中间
            mBaiduMap?.animateMapStatus(status) //动画的方式到中间
//            Log.d("经度", location.latitude.toString())
//            Log.d("纬度", location.longitude.toString())
//            Log.d("位置",location.addrStr)
        }
    }


    private fun readJSONData(url: String, city: Pair<Double, Double>) {
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
        val hourlyRainProb = hourlyPop?.toList()
        val thiscity = City(LatLng(city.first, city.second), hourlyRainProb) //单个城市降雨数据
        citiesRain.add(thiscity) //插入城市降雨数据列表
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
        mMapView?.onResume();
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

private fun Fragment.onCreate() {
    TODO("Not yet implemented")
}
