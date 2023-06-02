package com.example.asfinalwork2023.ui.personal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //设定Toolbar
        setSupportActionBar(loginToorbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }

        loginBtn.setOnClickListener {
            val account:String = accountEdit.text.toString()
            val password:String = passwordEdit.text.toString()
            val isValidAccount:Boolean = (android.util.Patterns.EMAIL_ADDRESS.matcher(account).matches())||(android.util.Patterns.PHONE.matcher(account).matches())
            val isValidPassword:Boolean = password.length > 5 && password.length < 13
            if(account.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "账号或密码为空", Toast.LENGTH_SHORT).show();
            }else if (!isValidAccount || !isValidPassword){
                Toast.makeText(this, "账号或密码的格式不正确", Toast.LENGTH_SHORT).show()
            }else{

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