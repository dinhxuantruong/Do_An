package com.example.datn.view.Search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterSearch
import com.example.datn.data.model.HistoryItemSearch
import com.example.datn.databinding.ActivityListSearchBinding
import com.example.datn.viewmodel.Products.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSearchActivity : AppCompatActivity() {
    private var _binding: ActivityListSearchBinding? = null
    private val binding get() = _binding!!
    private var adapter: adapterSearch? = null
    private lateinit var listSearch: MutableList<HistoryItemSearch.SearchHistory>
    private var flag = true
    private lateinit var vSearch: RoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vSearch = ViewModelProvider(this)[RoomViewModel::class.java]
        init()
        setupListeners()
    }

    private fun init() {
        binding.searchView.onActionViewExpanded()
        binding.rycSearch.setHasFixedSize(true)
        binding.rycSearch.isNestedScrollingEnabled = false
        binding.rycSearch.layoutManager = LinearLayoutManager(this)
        listSearch = mutableListOf()
    }


    private fun setupListeners() {
        binding.backIcon.setOnClickListener { finish() }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    startActivity(
                        Intent(
                            this@ListSearchActivity,
                            ListProductSearchActivity::class.java
                        ).apply {
                            putExtra("name", it)
                        })
                    val searchItem = HistoryItemSearch.SearchHistory(0, it)
                    vSearch.insertData(searchItem)
                    updateListView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        binding.txtShowAll.setOnClickListener {
            if (flag) {
                binding.rycSearch.visibility = View.VISIBLE
                adapter!!.updateList(listSearch)
                binding.txtShowAll.text = "Xóa tất cả"
                flag = false
            } else {
                binding.txtShowAll.visibility = View.GONE
                vSearch.deleteAllData()
                flag = true
                binding.rycSearch.visibility = View.GONE
            }
        }
    }

    private fun updateListView() {
        vSearch.readAllData.observe(this) { data ->
            listSearch.clear()
            listSearch.addAll(data.reversed()) // Đảo ngược danh sách để mới nhất xuất hiện đầu tiên
            val initialList = listSearch.take(6).toMutableList()
            adapter =
                adapterSearch(this, initialList, object : adapterSearch.OnItemDeleteListener {
                    override fun onItemDelete(item: HistoryItemSearch.SearchHistory) {
                        vSearch.deleteData(item)
                        updateListView()
                    }
                })
            binding.rycSearch.adapter = adapter
            showText()
        }
    }

    private fun showText() {
        binding.txtShowAll.visibility = if (listSearch.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        updateListView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
