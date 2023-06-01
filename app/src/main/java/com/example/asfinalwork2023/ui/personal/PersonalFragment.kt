package com.example.asfinalwork2023.ui.personal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentPersonalBinding
import com.example.asfinalwork2023.ui.home.HomeViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout

class PersonalFragment : Fragment() {

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
        val collapsingToolbar = binding.personalCollapsingtoolbar

//        collapsingToolbar.title = "Collapsingtoolbar"

        val imageView = binding.personalBackground
        Glide.with(this).load(R.drawable.personalbackground).centerCrop().into(imageView)

//        val testText = binding.testText

        PersonalViewModel.text.observe(viewLifecycleOwner) {
//            testText.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}