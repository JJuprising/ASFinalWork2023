package com.example.asfinalwork2023.ui.dashboard

import android.content.Context
import android.content.Intent
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R
import kotlinx.android.synthetic.main.activity_passage_detail.*

class PassageAdapter(val context: Context, val passageList: List<PassageInfoInt>) :
    RecyclerView.Adapter<PassageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val passageImage: ImageView = view.findViewById(R.id.passageImage)//卡片图片
        val passageTitle: TextView = view.findViewById(R.id.passageTitle)//卡片标题
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.passage_card, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {//点击进详情页
            val position = holder.adapterPosition
            val passage = passageList[position]
            val intent = Intent(context, PassageDetail::class.java).apply {//设置intent
                putExtra(PassageDetail.PASSAGE_TITLE, passage.title)//标题
                putExtra(PassageDetail.PASSAGE_CONTENT, passage.content)//内容
                putExtra(PassageDetail.PASSAGE_IMAGE, passage.picture)//封面图，Int格式
            }
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passage = passageList[position]
        holder.passageTitle.text = passage.title//设置标题
        //用id查图片
        val db = PassageDBHelper(context, "Passage.db", 1).writableDatabase
        val cursor =
            db.query("Passage", null, "id=?", arrayOf(passage.picture.toString()), null, null, null)
        val cw = CursorWindow("name", 5000000)
        val ac = cursor as AbstractWindowedCursor
        ac.window = cw
        if (cursor.moveToFirst()) {//查到了
            val array = cursor.getBlob(cursor.getColumnIndex("picture"))//找图片列，得到字节数组
            val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)//转成bitmap
            Glide.with(context).load(bitmap).into(holder.passageImage);//设置图片
        }

    }

    override fun getItemCount() = passageList.size
}