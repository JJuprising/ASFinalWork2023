package com.example.asfinalwork2023.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_passage_post.*

val images:ArrayList<ImageView> = ArrayList<ImageView>()
class PassagePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passage_post)
        images.add(PassagePostImage1)
        images.add(PassagePostImage2)
        images.add(PassagePostImage3)
        images.add(PassagePostImage4)
    }
}