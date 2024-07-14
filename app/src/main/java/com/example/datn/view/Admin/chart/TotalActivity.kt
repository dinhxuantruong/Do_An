package com.example.datn.view.Admin.chart

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultBarrChartItem
import com.example.datn.databinding.ActivityTotalBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Calendar

class TotalActivity : AppCompatActivity() {
    private var _binding: ActivityTotalBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineChart: LineChart
    private lateinit var viewModel: AdminViewModel
    private var currentMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTotalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()
    }

    private fun init() {
        setSupportActionBar(binding.toolBarCart)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        binding.toolBarCart.title = "Doanh thu tháng $currentMonth/${BarChartActivity.yearNow}"
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]
        viewModel.getAllTotal(BarChartActivity.yearNow, currentMonth)
        viewModel.getBarChart(BarChartActivity.yearNow)
        lineChart = findViewById(R.id.line_chart)
    }

    private fun observeView() {
        viewModel.resultTotalAll.observe(this@TotalActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    binding.txtSl.text = "${it.data.total_received_orders}/${it.data.total_orders}"
                    binding.txtCount.text = "${it.data.total_revenue.toInt().toVietnameseCurrency()}"
                }
                is ResponseResult.Error -> {
                    Log.d("Log", it.message)
                }
            }
        }

        viewModel.resultBarChart.observe(this@TotalActivity) { response ->
            when (response) {
                is ResponseResult.Success -> {
                    val revenueData = response.data
                    updateLineChart(revenueData)
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

    private fun updateLineChart(revenueData: List<resultBarrChartItem>) {
        val revenueMap = mutableMapOf<Int, Float>()
        for (data in revenueData) {
            revenueMap[data.month] = data.total_revenue.toFloat() / 1_000_000  // Chia để đơn giản hóa đơn vị triệu
        }

        val entries = ArrayList<Entry>()
        for (i in 1..12) {
            val value = revenueMap[i] ?: 0f
            entries.add(Entry(i.toFloat() - 1, value))
        }

        val lineDataSet = LineDataSet(entries, "Doanh thu (triệu VNĐ)")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.BLACK

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val months = arrayOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f

        // Sử dụng formatter để hiển thị giá trị theo triệu
        lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString()
            }
        }
        lineChart.axisRight.valueFormatter = lineChart.axisLeft.valueFormatter

        lineChart.description.text = "Biểu đồ tăng trưởng doanh thu"
        lineChart.animateY(1000)
        lineChart.invalidate() // Refresh the chart
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chart_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.idMonth -> {
                showNumberInputDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNumberInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_input_number, null)
        val inputNumberEditText = dialogView.findViewById<EditText>(R.id.inputNumber)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nhập số")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val inputNumber = inputNumberEditText.text.toString().toIntOrNull()
                if (inputNumber != null) {
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    viewModel.getAllTotal(currentYear, inputNumber)
                    binding.toolBarCart.title = "Doanh thu tháng $inputNumber/${BarChartActivity.yearNow}"
                } else {
                    Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
