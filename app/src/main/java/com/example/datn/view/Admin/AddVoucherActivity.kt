package com.example.datn.view.Admin

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.addVoucher
import com.example.datn.databinding.ActivityAddVoucherBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddVoucherActivity : AppCompatActivity() {
    private var _binding: ActivityAddVoucherBinding? = null
    private val binding get() = _binding!!
    private lateinit var listDiscount: MutableList<String>
    private var selectedDate: String = ""
    private var formattedDate: String = ""
    private lateinit var viewModel : AdminViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this,vmFactory)[AdminViewModel::class.java]
        listDiscount = mutableListOf("fixed", "percent")
        val adapter = ArrayAdapter(this, R.layout.list_items, listDiscount)
        binding.autoComple.setAdapter(adapter)

        // Lấy ngày hiện tại
        val currentDate = LocalDate.now()
         formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        binding.dateLayout.setEndIconOnClickListener {
            // Get current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Show DatePickerDialog
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date and set it to the EditText
                 selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                // Optionally show a toast
                binding.txtdate.setText(selectedDate)
            }, year, month, day).show()
        }

        binding.btnAdd.setOnClickListener {
            viewModel.addVoucher(addVoucher(binding.txtCode.text.toString(),
                binding.autoComple.text.toString(),
                binding.txtDiscountValue.text.toString().toDouble(),
                binding.txtdate.text.toString(),
                binding.txtUseLimit.text.toString().toInt(),
                formattedDate))
        }

        viewModel.resultAddVoucher.observe(this@AddVoucherActivity){
            when(it){
                is ResponseResult.Success -> {
                    this.snackBar(it.data.message)
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
