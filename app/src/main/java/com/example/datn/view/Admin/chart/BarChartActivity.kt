package com.example.datn.view.Admin.chart

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datn.R
import com.example.datn.databinding.ActivityBarChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.random.Random

class BarChartActivity : AppCompatActivity() {
    private var _binding : ActivityBarChartBinding?= null
    private val binding get() = _binding!!
    private lateinit var barChart: BarChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBarChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barChart = findViewById(R.id.bar_chart)

        val listColor: ArrayList<BarEntry> = ArrayList()
        // Tạo dữ liệu cho 12 tháng
        for (i in 0 until 12) {
            listColor.add(BarEntry(i.toFloat(), Random.nextFloat() * 100))  // Random doanh thu
        }


        // Danh sách các mục BarEntry cho 12 tháng
        val list: ArrayList<BarEntry> = ArrayList()
        list.add(BarEntry(0f, 10f))  // Tháng 1
        list.add(BarEntry(1f, 20.4f))  // Tháng 2
        list.add(BarEntry(2f, 15f))  // Tháng 3
        list.add(BarEntry(3f, 30f))  // Tháng 4
        list.add(BarEntry(4f, 25f))  // Tháng 5
        list.add(BarEntry(5f, 66f))  // Tháng 6
        list.add(BarEntry(6f, 0f))  // Tháng 7
        list.add(BarEntry(7f, 0f))  // Tháng 8
        list.add(BarEntry(8f, 0f))  // Tháng 9
        list.add(BarEntry(9f, 0f))  // Tháng 10
        list.add(BarEntry(10f, 0f)) // Tháng 11
        list.add(BarEntry(11f, 0f)) // Tháng 12

        val barDataSet = BarDataSet(list, "Doanh thu (triệu VNĐ)")
        barDataSet.colors = generateRandomColors(list.size)
        barDataSet.valueTextColor = Color.BLACK

        val barData = BarData(barDataSet)
        barChart.data = barData

        // Đặt tên các tháng cho trục X
        val months = arrayOf("T1", "T2", "T3", "T4", "T5", "T6",
            "T7", "T8", "T9", "T10", "T11", "T12")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.granularity = 1f  // Đảm bảo mỗi tháng được hiển thị
        barChart.xAxis.labelCount = months.size

        barChart.setFitBars(true)
        barChart.description.text = "Thống kê doanh thu theo tháng"
        barChart.animateY(1000)

        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }
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
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}