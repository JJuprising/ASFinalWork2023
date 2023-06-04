package com.example.asfinalwork2023.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//在其他地方,比如Activity中,可以调用ViewModel的方法改变LiveData值,触发Fragment的UI更新。
class HomeViewModel : ViewModel() {
    private var _locationID = MutableLiveData<String>().apply{
        value = "101280803"
    }  //南海区
    fun setLocationId(ID: String){
        _locationID = MutableLiveData<String>().apply{
            value = ID
        }
    }
    val locationID: LiveData<String> = _locationID
}