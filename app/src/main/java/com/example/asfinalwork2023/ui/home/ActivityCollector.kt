package com.example.asfinalwork2023.ui.home

import android.app.Activity

object ActivityCollector {
    private val activies = ArrayList<Activity>()

    fun addActivity(activity: Activity){
        activies.add(activity)
    }

    fun removeActivity(activity: Activity){
        activies.remove(activity)
    }

    fun finishAll(){
        for (activity in activies){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
    }
}