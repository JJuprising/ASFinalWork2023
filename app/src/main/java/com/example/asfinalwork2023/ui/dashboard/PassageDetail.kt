package com.example.asfinalwork2023.ui.dashboard

import android.R.attr.data
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_passage_detail.*
import kotlin.math.sign


class PassageDetail : AppCompatActivity() {
    companion object {
        const val PASSAGE_TITLE = "passageTitle"
        const val PASSAGE_CONTENT = "passageContent"
        const val PASSAGE_IMAGE = "passageImage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passage_detail)
        val passageTitle = intent.getStringExtra(PASSAGE_TITLE) ?: "Default Title"//标题，string类型
        val passageContent = intent.getStringExtra(PASSAGE_CONTENT) ?: "Default Content"//内容，string类型
        val passageImageID = intent.getIntExtra(PASSAGE_IMAGE, 1) ?: 1//数据库内文章行的id，int型
        //用id找图片
        val db = PassageDBHelper(this, "Passage.db", 1).writableDatabase
        val cursor =
            db.query("Passage", null, "id=?", arrayOf(passageImageID.toString()), null, null, null)
        val cw = CursorWindow("name", 5000000)
        val ac = cursor as AbstractWindowedCursor
        ac.window = cw
        if (cursor.moveToFirst()) {
            val array = cursor.getBlob(cursor.getColumnIndex("picture"))
            val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
            Glide.with(this).load(bitmap).into(PassageDetailImage);//设置图片
        }
        cursor.close()
        db.close()
        PassageDetailTitle.text = passageTitle//设置标题
        PassageDetailContent.text = passageContent//设置内容


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