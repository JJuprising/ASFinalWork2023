package com.example.asfinalwork2023.ui.personal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentPersonalBinding
import kotlinx.android.synthetic.main.activity_login.*

class PersonalFragment : Fragment(){

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val PersonalViewModel = ViewModelProvider(this).get(PersonalViewModel::class.java)
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //控制文本内容修改，暂不需
        //        val testText = binding.testText
        PersonalViewModel.text.observe(viewLifecycleOwner) {
//2            testText.text = it
        }

        // 添加一张图片到CollapsingToolbarLayout中作为上方背景
        val imageView = binding.personalBackground
        Glide.with(this).load(R.drawable.personalbackground).centerCrop().into(imageView)

        // 点击登录按钮或头像跳转到Login登录页
        val userImage = binding.userImage
        userImage.setOnClickListener {
            val intent = Intent(requireContext(),Login::class.java)
            startActivity(intent)
        }
        val login = binding.login
        login.setOnClickListener {
            val intent = Intent(requireContext(),Login::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}