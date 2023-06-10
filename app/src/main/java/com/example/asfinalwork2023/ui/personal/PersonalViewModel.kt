package com.example.asfinalwork2023.ui.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asfinalwork2023.R

//在其他地方,比如Activity中,可以调用ViewModel的方法改变LiveData值,触发Fragment的UI更新。
class PersonalViewModel : ViewModel() {
    //在ViewModel中定义LiveData用于监听数据变化，这里是_text
    private val _loginText = MutableLiveData<String>().apply {
        value = "点击登录"
    }
    val loginText: LiveData<String> = _loginText
    fun setLoginText(text: String) {
        _loginText.value = text
    }

    private val _loginState = MutableLiveData<String>().apply {
        value = "未登录"
    }
    val loginState: LiveData<String> = _loginState
    fun updateLoginState(state: String) {
        _loginState.value = state
    }

    private val _userImageRes = MutableLiveData<Int>().apply {
        value = R.drawable.personal
    }
    val userImageRes: LiveData<Int> = _userImageRes
    fun updateUserImageRes(resId: Int) {
        _userImageRes.value = resId
    }

    val boxCheckLiveData = MutableLiveData<Triple<Boolean, Boolean, Boolean>>()
}