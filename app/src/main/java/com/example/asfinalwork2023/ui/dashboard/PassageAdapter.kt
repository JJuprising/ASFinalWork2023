package com.example.asfinalwork2023.ui.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.asfinalwork2023.R

class PassageAdapter (val context: Context, val passageList: List<PassageInfo>): RecyclerView.Adapter<PassageAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val passageImage: ImageView = view.findViewById(R.id.passageImage)
        val passageTitle: TextView = view.findViewById(R.id.passageTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.passage_card, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val passage = passageList[position]
            val intent = Intent(context, PassageDetail::class.java).apply {
                putExtra(PassageDetail.PASSAGE_TITLE, passage.title)
                putExtra(PassageDetail.PASSAGE_CONTENT, passage.content)
                putExtra(PassageDetail.PASSAGE_IMAGE, passage.picture)
            }
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passage = passageList[position]
        holder.passageTitle.text = passage.title
        Glide.with(context).load(passage.picture).into(holder.passageImage);
    }

    override fun getItemCount() = passageList.size
}