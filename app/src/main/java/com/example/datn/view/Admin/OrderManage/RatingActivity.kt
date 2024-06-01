package com.example.datn.view.Admin.OrderManage

import android.os.Bundle
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.ratingBody
import com.example.datn.databinding.ActivityRatingBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory
import com.example.datn.viewmodel.Orders.RatingViewModel

class RatingActivity : AppCompatActivity() {
    private var _binding : ActivityRatingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : RatingViewModel
    private var numberRate = 0.0
    private var id  = 0
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ratingBarSendData.setOnRatingBarChangeListener { _, rating, _ ->
            numberRate = if (rating <= 0.0){
                1.0
            }else if (rating > 5.0){
                5.0
            }else{
                rating.toDouble()
            }
        }

        init()
        setOnCLick()
    observeView()

    }

    private fun observeView() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultRating.observe(this){
            when(it){
                is ResponseResult.Success -> {
                    this.snackBar(it.data.message)
                    finish()
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }
    }

    private fun setOnCLick() {
        id = intent.getIntExtra("id", 1)
        binding.btnRate.setOnClickListener {
            viewModel.getRatingOrder(id, ratingBody(numberRate, binding.txtRate.text.toString()))
        }
        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun init(){
        val repositoryProduct = repositoryProduct()
        val vmFactory  = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this@RatingActivity,vmFactory)[RatingViewModel::class.java]


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}