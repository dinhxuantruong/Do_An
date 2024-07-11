package com.example.datn.view.Orders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.datn.R
import com.example.datn.databinding.ActivitySuccessBinding
import com.example.datn.view.MainActivity2
import com.example.datn.view.MainView.MainViewActivity

class SuccessActivity : AppCompatActivity() {
    private var _binding : ActivitySuccessBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("result")
        val orderId = intent.getStringExtra("order_id")
        val checkPay = intent.getIntExtra("idPay",3)
        if (checkPay == 0){
            binding.txtNotification.text = "Đặt hàng thành công!"
        }else if (checkPay == 1){
            if (orderId != null) {
                binding.txtNotification.text = "$result\nMã giao dịch: $orderId"
            } else {
                binding.txtNotification.text  = result
            }
        }else{
            binding.txtNotification.text = "Lỗi đơn hàng!!"
        }



        binding.btnContinue.setOnClickListener {
          finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}