package com.example.asfinalwork2023.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.asfinalwork2023.R

class PassageDetail : AppCompatActivity() {
    companion object {
        const val PASSAGE_TITLE = "passageTitle"
        const val PASSAGE_CONTENT = "passageContent"
        const val PASSAGE_IMAGE = "passageImage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passage_detail)
        val passageTitle = intent.getStringExtra(PASSAGE_TITLE) ?: "Default Title"
        val passageContent = intent.getStringExtra(PASSAGE_CONTENT) ?: "Default Content"
        val passageImage = intent.getIntExtra(PASSAGE_IMAGE, R.drawable.ic_delete)


    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun generateFruitContent(passageName: String) = passageName.repeat(100)
}