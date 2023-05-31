package com.example.asfinalwork2023.ui.personal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.asfinalwork2023.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 初始化ViewModel，通过ViewModelProvider获取与之对应的数据监听ViewModel，这里是PersonalViewModel
        val personalViewModel =
            ViewModelProvider(this).get(PersonalViewModel::class.java)


        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        val textView: TextView = binding.textPersonal
//
//        //在Fragment中观察ViewModel中的LiveData,一旦数据变化,Fragment的UI也会自动更新。这里是textView。
//        personalViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}