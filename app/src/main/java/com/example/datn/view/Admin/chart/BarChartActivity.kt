package com.example.datn.view.Admin.chart

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultBarrChartItem
import com.example.datn.databinding.ActivityBarChartBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.random.Random

class BarChartActivity : AppCompatActivity() {
    private var _binding: ActivityBarChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var barChart: BarChart
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBarChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarCart)

        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]

        barChart = findViewById(R.id.bar_chart)

        viewModel.getBarChart()

        viewModel.resultBarChart.observe(this@BarChartActivity) { response ->
            when (response) {
                is ResponseResult.Success -> {
                    val revenueData = response.data
                    updateBarChart(revenueData)
                }
                is ResponseResult.Error -> {
                    // Toast.makeText(this, "Error: ${response.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateBarChart(revenueData: List<resultBarrChartItem>) {
        val revenueMap = mutableMapOf<Int, Float>()
        for (data in revenueData) {
            revenueMap[data.month] = data.total_revenue.toFloat() / 1_000_000  // Chia để đơn giản hóa đơn vị triệu
        }

        val list: ArrayList<BarEntry> = ArrayList()
        for (i in 1..12) {
            list.add(BarEntry(i.toFloat() - 1, revenueMap[i] ?: 0f)) // months start from 1 to 12, entries start from 0
        }

        val barDataSet = BarDataSet(list, "Doanh thu (triệu VNĐ)")
        barDataSet.colors = generateRandomColors(list.size)
        barDataSet.valueTextColor = Color.BLACK

        val barData = BarData(barDataSet)
        barChart.data = barData

        val months = arrayOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.labelCount = months.size

        // Sử dụng formatter để hiển thị giá trị theo triệu
        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString()
            }
        }

        barChart.axisRight.valueFormatter = barChart.axisLeft.valueFormatter

        barChart.setFitBars(true)
        barChart.description.text = "Thống kê doanh thu theo tháng"
        barChart.animateY(1000)
        barChart.invalidate() // Refresh the chart
    }

    private fun generateRandomColors(number: Int): ArrayList<Int> {
        val colors = ArrayList<Int>()
        for (i in 0 until number) {
            val red = Random.nextInt(256)
            val green = Random.nextInt(256)
            val blue = Random.nextInt(256)
            colors.add(Color.rgb(red, green, blue))
        }
        return colors
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu2,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.usrSetting -> {
                startActivity(Intent(this@BarChartActivity,TotalActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}