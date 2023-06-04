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
        //显示热力图
        showheat()
        return root
    }

    private fun showheat() {
        // 创建热力图数据
        val builder = HeatMap.Builder()

// 添加热力图数据点
// 添加热力图数据点

        builder.data(listOf(
            LatLng(39.904989, 116.405285),  // 北京
            LatLng(31.230416, 121.473701),  // 上海
            LatLng(23.129110, 113.264381),  // 广州
            LatLng(30.572269, 104.066541),  // 成都
            LatLng(39.913818, 116.363625),  // 北京
            LatLng(22.543099, 114.057868),  // 深圳
            LatLng(31.968599, 99.901813),   // 西宁
            LatLng(34.341574, 108.939770),  // 西安
            LatLng(25.042884, 102.712891),  // 昆明
            LatLng(22.823610, 108.372117)   // 南宁
        ))
// 设置开始动画属性：开启初始动画，时长100毫秒，动画缓动函数类型为线性
        // 设置开始动画属性：开启初始动画，时长100毫秒，动画缓动函数类型为线性
        val init = HeatMapAnimation(true, 100, HeatMapAnimation.AnimationType.Linear)
// 设置帧动画属性：开启帧动画，时长800毫秒，动画缓动函数类型为线性
// 设置帧动画属性：开启帧动画，时长800毫秒，动画缓动函数类型为线性
        val frame = HeatMapAnimation(true, 800, HeatMapAnimation.AnimationType.Linear)
        builder.initAnimation(init)
        builder.frameAnimation(frame)
// 设置热力图半径范围
        builder.radius(50)

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

    private fun initloc(){
        //定位初始化
//        mLocationClient = LocationClient(this)

        //通过LocationClientOption设置LocationClient相关参数

        //定位初始化
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
