package com.example.asfinalwork2023.ui.personal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(loginToorbar)
    }
}