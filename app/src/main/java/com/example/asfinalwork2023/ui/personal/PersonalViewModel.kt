package com.example.asfinalwork2023.ui.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//在其他地方,比如Activity中,可以调用ViewModel的方法改变LiveData值,触发Fragment的UI更新。
class PersonalViewModel : ViewModel() {
    //在ViewModel中定义LiveData用于监听数据变化，这里是_text
    private val _text = MutableLiveData<String>().apply {
        value = "This is personal Fragment"
    }
    val text: LiveData<String> = _text
}