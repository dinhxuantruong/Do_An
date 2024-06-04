package com.example.datn.view.Admin.chart

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.databinding.ActivityPieChartBinding
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultPieChartItem
import com.example.datn.repository.repositoryAdmin
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class PieChartActivity : AppCompatActivity() {
    private var _binding: ActivityPieChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChart: PieChart
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPieChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]

        //binding.pieChart.setDrawSliceText(false)
        viewModel.getPieChart()
        observeView()

        pieChart = findViewById(R.id.pie_chart)

        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }

        // Khởi tạo PieChart trống
        initPieChart()
    }

    private fun observeView() {
        viewModel.resultPieChart.observe(this@PieChartActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    // Cập nhật PieChart với dữ liệu từ API
                    updatePieChart(it.data)
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initPieChart() {
        pieChart.description.text = "Pie Chart"
        pieChart.centerText = "Thống kê .."
        pieChart.setUsePercentValues(true)
        pieChart.animateY(2000)

        // Tùy chỉnh chú thích (legend)
        val legend = pieChart.legend
        legend.isWordWrapEnabled = true  // Cho phép tự động chia thành nhiều hàng nếu cần
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
    }

    private fun updatePieChart(data: List<resultPieChartItem>) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // Tạo các PieEntry từ dữ liệu JSON
        for (item in data) {
            entries.add(PieEntry(item.sold_percentage.toFloat(), item.category))
        }

        val pieDataSet = PieDataSet(entries, "Tỷ lệ % bán hàng")
        pieDataSet.colors = getColors(entries.size)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 15f

        val pieData = PieData(pieDataSet)

        pieChart.data = pieData
        pieChart.invalidate() // Làm mới PieChart để hiển thị dữ liệu mới
    }

    private fun getColors(size: Int): List<Int> {
        val colors: ArrayList<Int> = ArrayList()
        val availableColors = ColorTemplate.VORDIPLOM_COLORS + ColorTemplate.JOYFUL_COLORS +
                ColorTemplate.COLORFUL_COLORS + ColorTemplate.LIBERTY_COLORS +
                ColorTemplate.PASTEL_COLORS
        for (i in 0 until size) {
            colors.add(availableColors[i % availableColors.size])
        }
        return colors
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
