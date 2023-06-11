package com.example.asfinalwork2023.ui.personal

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_luck.*
import java.util.*
import kotlin.math.sqrt

class LuckActivity : AppCompatActivity(),SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private val shakeThreshold = 50 // 定义摇晃的阈值
    private var lastShakeTime: Long = 0 // 上一次摇晃的时间

    private lateinit var goodView: TextView
    private lateinit var badView: TextView
    val date = Calendar.getInstance()  // 获取当前日期
    val year = date.get(Calendar.YEAR)  // 年
    val month = date.get(Calendar.MONTH) + 1  // 月,因为Calendar月份从0开始所以+1
    val day = date.get(Calendar.DATE)   // 日

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luck)
        //设定Toolbar
        setSupportActionBar(luckToorbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        goodView = findViewById(R.id.Good)
        badView = findViewById(R.id.Bad)
        dateText.text = "农历${year}年${month}月${day}日"

        // 获取设备的加速度传感器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        // 注册加速度传感器的监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // 取消注册加速度传感器的监听器
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val currentTime = System.currentTimeMillis() // 获取当前时间
            if ((currentTime - lastShakeTime) > 1000) { // 限制摇晃的频率
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]
                val acceleration = sqrt(x * x + y * y + z * z) // 计算加速度向量的模长
                if (acceleration > shakeThreshold) { // 如果加速度向量的模长大于阈值，则认为发生了摇晃
                    // 更新上一次摇晃的时间
                    lastShakeTime = currentTime

                    // TODO: 获取今天的运势信息，并更新 UI
                    val goodLuck = "乔迁 入学 耕种 搬公司"
                    val badLuck = "结婚 开业 提车 签合同"
                    goodView.text = goodLuck
                    badView.text = badLuck
                }
            }
        }
    }

    //Home按钮，用于返回到登陆页面
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}