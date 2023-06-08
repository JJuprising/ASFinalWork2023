package com.example.asfinalwork2023.ui.dashboard

import android.content.ContentValues
import android.content.Intent
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.database.getBlobOrNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val passageList = ArrayList<PassageInfoInt>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)

//        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initPassage()//初始化文章流

        val layoutManager = GridLayoutManager(context, 2)
        val  recyclerView:RecyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager

        val adapter = PassageAdapter(context!!, passageList)//填充adapter
        recyclerView.adapter = adapter

        val swipeRefresh:SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.purple_200)
        swipeRefresh.setOnRefreshListener {
            refreshPassage(adapter)
        }
        val button = binding.fab
        button.setOnClickListener{
            val intent = Intent(context, PassagePost::class.java)
            context!!.startActivity(intent)

        }
        return root
    }

    fun CreatDB(){
        var passages = mutableListOf(
            PassageInfoInt("Title1","Content1", R.drawable.apple),
            PassageInfoInt("Title2","Content2", R.drawable.banana),
            PassageInfoInt("Title3","Content3", R.drawable.cherry),
            PassageInfoInt("Title4","Content4", R.drawable.grape),
            PassageInfoInt("Title5","Content5", R.drawable.mango),
            PassageInfoInt("Title6","Content6", R.drawable.watermelon),
            PassageInfoInt("Title7","Content7", R.drawable.strawberry),
            PassageInfoInt("Title8","Content8", R.drawable.pineapple),
            PassageInfoInt("Title9","Content9", R.drawable.pear),
            PassageInfoInt("Title10","Content10", R.drawable.orange)
        )
        val dbHelper = PassageDBHelper(requireContext(), "Passage.db",1)
        val db = dbHelper.writableDatabase
        for( passage in passages){
            val drawable = resources.getDrawable(passage.picture)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val values = ContentValues()

            values.put("title", passage.title);
            values.put("content", passage.content);
            values.put("picture", byteArray);

            db.insert("Passage",null,values)
        }
    }
    fun ReadData(){
        val dbHelper = PassageDBHelper(requireContext(), "Passage.db",1)
        val db = dbHelper.writableDatabase
        // 查询Book表中所有的数据
        val cursor = db.query("Passage", null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val picture = cursor.getBlobOrNull(cursor.getColumnIndex("picture"))
                Log.d("MainActivity", "ID: $id")
                Log.d("MainActivity", "Title: $title")
                Log.d("MainActivity", "Content: $content")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initPassage() {
        passageList.clear()
        val dbHelper = PassageDBHelper(requireContext(), "Passage.db",1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Passage", null, null, null, null, null, "id desc")
        val cw = CursorWindow("name", 5000000)
        val ac = cursor as AbstractWindowedCursor
        ac.window = cw
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
//                val picture = cursor.getBlob(cursor.getColumnIndex("picture"))

                passageList.add(PassageInfoInt(title,content,id))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }
    private fun refreshPassage(adapter: PassageAdapter) {
        thread {
            Thread.sleep(2000)
            requireActivity().runOnUiThread {
                initPassage()
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
        }
    }
}