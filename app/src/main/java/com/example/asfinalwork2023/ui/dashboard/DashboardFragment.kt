package com.example.asfinalwork2023.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.asfinalwork2023.R
import com.example.asfinalwork2023.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlin.concurrent.thread

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var passages = mutableListOf(
        PassageInfo("Title1","Content1", R.drawable.apple),
        PassageInfo("Title2","Content2", R.drawable.banana),
        PassageInfo("Title3","Content3", R.drawable.cherry),
        PassageInfo("Title4","Content4", R.drawable.grape),
        PassageInfo("Title5","Content5", R.drawable.mango),
        PassageInfo("Title6","Content6", R.drawable.watermelon),
        PassageInfo("Title7","Content7", R.drawable.strawberry),
        PassageInfo("Title8","Content8", R.drawable.pineapple),
        PassageInfo("Title9","Content9", R.drawable.pear),
        PassageInfo("Title10","Content10", R.drawable.orange)
    )
    private val passageList = ArrayList<PassageInfo>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        initPassage()
        val root: View = binding.root
        val layoutManager = GridLayoutManager(context, 2)
        val  recyclerView:RecyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager
        val adapter = PassageAdapter(context!!, passageList)
        recyclerView.adapter = adapter
        val swipeRefresh:SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.purple_200)
        swipeRefresh.setOnRefreshListener {
            refreshPassage(adapter)
        }
        val button = binding.fab
        button.setOnClickListener{
            val intent = Intent(context, MessagePost::class.java)
            context!!.startActivity(intent)

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initPassage() {
        passageList.clear()
        repeat(50) {
            val index = (0 until passages.size).random()
            passageList.add(passages[index])


        }
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