package com.example.datn.view.Search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterSearch
import com.example.datn.data.model.HistoryItemSearch
import com.example.datn.databinding.ActivityListSearchBinding
import com.example.datn.viewmodel.Products.RoomViewModel
import com.example.datn.viewmodel.Products.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSearchActivity : AppCompatActivity() {
    private var _binding: ActivityListSearchBinding? = null
    private val binding get() = _binding!!
    private var adapter: adapterSearch? = null
    private lateinit var listSearch: MutableList<String>
    private var flag = true
    private lateinit var vSearch : RoomViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vSearch = ViewModelProvider(this@ListSearchActivity)[RoomViewModel::class.java]
        init()

        vSearch.readAllData.observe(this@ListSearchActivity){
            listSearch.clear()
            it.forEach { item ->
                listSearch.add(item.searchtext)
            }
            val initialList = listSearch.take(6).toMutableList()

            adapter = adapterSearch(this@ListSearchActivity, initialList)
            binding.rycSearch.adapter = adapter
        }


        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val intent =
                        Intent(this@ListSearchActivity, ListProductSearchActivity::class.java)
                    intent.putExtra("name", query)
                    startActivity(intent)
                    val searchItem =HistoryItemSearch.SearchHistory(0,query.toString())
                    vSearch.insertData(searchItem)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }


    private fun init() {
        binding.searchView.onActionViewExpanded()
        binding.rycSearch.setHasFixedSize(true)
        binding.rycSearch.isNestedScrollingEnabled = false
        binding.rycSearch.layoutManager = LinearLayoutManager(this)
        listSearch = mutableListOf()

        binding.txtShowAll.setOnClickListener {
            if (flag) {
                adapter!!.updateList(listSearch)
                binding.txtShowAll.text = "Xóa tất cả"
            } else {
                binding.txtShowAll.text = "Xem thêm"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}