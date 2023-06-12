package com.example.asfinalwork2023.ui.personal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentPersonalBinding
import com.example.asfinalwork2023.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_personal.*

class PersonalFragment : Fragment(){
    var loginState:String = "未登录"
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!
    private lateinit var personalViewModel: PersonalViewModel

    var box1Check: Boolean = false
    var box2Check: Boolean = false
    var box3Check: Boolean = false

    private val loginStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isLogin = intent?.getBooleanExtra("isLogin", false) ?: false
            if (isLogin) {
                personalViewModel.updateLoginState("已登录")
                personalViewModel.updateUserImageRes(R.drawable.user)
                personalViewModel.setLoginText("七班张达")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        personalViewModel = ViewModelProvider(this).get(PersonalViewModel::class.java)
        val root: View = binding.root
        // 注册广播接收器
        requireContext().registerReceiver(loginStatusReceiver, IntentFilter("LOGIN_STATUS"))

        val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        personalViewModel.loginText.observe(viewLifecycleOwner) {
            login.text = it
        }
        personalViewModel.loginState.observe(viewLifecycleOwner) { state ->
            Glide.with(this@PersonalFragment).load(personalViewModel.userImageRes.value).centerCrop().into(binding.userImage)
            loginState = state
        }

        // 添加一张图片到CollapsingToolbarLayout中作为上方背景
        val imageView = binding.personalBackground
        Glide.with(this).load(R.drawable.personalbackground).centerCrop().into(imageView)

        // 点击头像跳转到Login登录页
        val userImage = binding.userImage
        userImage.setOnClickListener {
            val intent = Intent(requireContext(),Login::class.java)
            startActivity(intent)
        }

        //点击“点击登录”文本可以跳转到Login登录页
        val login = binding.login
        login.setOnClickListener {
            val intent = Intent(requireContext(),Login::class.java)
            startActivity(intent)
        }

        //点击“运势”图标可以跳转到运势页
        val luckIcon = binding.luckIcon
        luckIcon.setOnClickListener {
            val intent = Intent(requireContext(),LuckActivity::class.java)
            startActivity(intent)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("notification","天气预警",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        //点击"开启通知"按钮会进行登录确认，没有登录就会弹出窗口要求登录，登陆了就将选择的checkBox进行提醒
        val openMessage = binding.openMessage
        openMessage.setOnClickListener {
            if (loginState == "已登录") {
                // TODO: 提醒选择的checkBox
                box1Check =checkBox1.isChecked
                box2Check =checkBox2.isChecked
                box3Check =checkBox3.isChecked
                personalViewModel.boxCheckLiveData.value = Triple(box1Check, box2Check, box3Check)
                if (box1Check||box2Check||box3Check){
                    Toast.makeText(requireContext(), "已开启", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "未选择", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
            }
        }
        personalViewModel.boxCheckLiveData.observe(viewLifecycleOwner) { boxCheck ->
            box1Check = boxCheck.first
            box2Check = boxCheck.second
            box3Check = boxCheck.third

            if(box1Check){
                val notification = NotificationCompat.Builder(requireContext(),"notification")
                    .setContentTitle("高温预警").setContentText("未来七天内温度较高，注意降温")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()
                manager.notify(1,notification)
            }
            if(box2Check){
                val notification = NotificationCompat.Builder(requireContext(),"notification")
                    .setContentTitle("暴雨预警").setContentText("未来——天有连续暴雨")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()
                manager.notify(2,notification)
            }
            if(box3Check){
                val notification = NotificationCompat.Builder(requireContext(),"notification")
                    .setContentTitle("低温预警").setContentText("未来七天内存在低温，注意保暖!")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()
                manager.notify(3,notification)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 取消广播接收器的注册
        requireContext().unregisterReceiver(loginStatusReceiver)
        _binding = null
    }
}