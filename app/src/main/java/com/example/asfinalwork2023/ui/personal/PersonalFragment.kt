package com.example.asfinalwork2023.ui.personal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        //root为PersonalFragment的根视图
        val root: View = binding.root
        val toolbar = root.findViewById<Toolbar>(R.id.personalToolbar)
        toolbar.title = "Personal"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}