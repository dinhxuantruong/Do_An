package com.example.datn.view.Detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.adapter.sizeAdapter
import com.example.datn.data.dataresult.Product
import com.example.datn.data.dataresult.ProductTypeDetail
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.model.addCart
import com.example.datn.databinding.FragmentNewTaskSheetBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.ViewModelDetailProduct
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso

class NewTaskSheet : BottomSheetDialogFragment() {
    private lateinit var viewModel: ViewModelDetailProduct
    private var _binding: FragmentNewTaskSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var listSize: MutableList<Product>
    private var adapter: sizeAdapter? = null

    //private val adapter get() = _adapter!!
    private var id = 0
    private var quantity = ""
    private var productId = 0
    private var stock = 0
    private var updatingText = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskSheetBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        setViewModel()
        binding.btnIncrease.isEnabled = false
        binding.btnIncrease.isEnabled = false
        binding.txtQuantity.addTextChangedListener(countWatcher)

        viewModel.resultDetail.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listSize.clear()
                    val productType = it.data.ProductType
                    binding.txtThuonghieu.text = "Thương hiệu: ${productType.trademark}"
                    quantity = productType.quantity
                    setBigView(productType.image_url, productType.lowest_price.toString())
                    val data = it.data.ProductType.products
                    data.forEach { item ->
                        listSize.add(item)
                    }
                    adapter = sizeAdapter(object : sizeAdapter.ClickListenerSize {
                        override fun onClickedItem(itemProduct: Product) {
                            binding.txtThuonghieu.text = "Tồn kho: ${itemProduct.stock}"
                            stock = itemProduct.stock
                            productId = itemProduct.id
                            setViewIsCheck(itemProduct, productType)
                            binding.txtQuantity.setText("1")
                        }
                    }, requireActivity(), listSize)
                    binding.recyclerview.adapter = adapter
                }

                is ResponseResult.Error -> {

                    //
                }
            }
        }



        binding.btnAddCart.setOnClickListener {
            val currentValue = binding.txtQuantity.text.toString().toIntOrNull()
            if ((currentValue == null) || (currentValue < 0)) {
                binding.txtQuantity.setText("1")
            }
            val quantity = binding.txtQuantity.text.toString().toInt()
            viewModel.addToCart(addCart(productId, quantity))
//            viewModel.resultAddCart.observeOnceAfterInit(viewLifecycleOwner){
//                when(it){
//                    is ResponseResult.Success -> {
//                        Toast.makeText(requireContext(), "haha", Toast.LENGTH_SHORT).show()
//                    }
//                    is ResponseResult.Error -> {
//                        Toast.makeText(requireContext(),"huhu",Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
            if (ProductActivity.isLoggedInFirstTime) {
                viewModel.resultAddCart.observeOnceAfterInit(viewLifecycleOwner) { result ->
                    handleResult(result)
                    //HomeFragment.isLoggedInFirstTime = false
                }
            } else {
                viewModel.resultAddCart.observeOnce(viewLifecycleOwner) { result ->
                    handleResult(result)
                }
                ProductActivity.isLoggedInFirstTime = true
            }
            Log.e("CHECK", "${ProductActivity.isLoggedInFirstTime}")
        }



        binding.btnDecrease.setOnClickListener {
            binding.txtQuantity.clearFocus()
            val currentValue =
                binding.txtQuantity.text.toString().toIntOrNull() ?: return@setOnClickListener
            if (currentValue > 1) {
                val newValue = currentValue - 1
                binding.txtQuantity.setText(newValue.toString())
            }
        }

        binding.btnIncrease.setOnClickListener {
            binding.txtQuantity.clearFocus()
            val currentValue =
                binding.txtQuantity.text.toString().toIntOrNull() ?: return@setOnClickListener
            if (currentValue < stock) {
                val newValue = currentValue + 1
                binding.txtQuantity.setText(newValue.toString())
            } else {
                binding.txtMax.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }


        return binding.root
    }

    private fun handleResult(result: ResponseResult<ResultMessage>) {
        when (result) {
            is ResponseResult.Success -> {
               viewModel.getCartCount()
                dismiss()
           requireActivity().snackBar(result.data.message)
            }

            is ResponseResult.Error -> {
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setViewIsCheck(itemProduct: Product, productType: ProductTypeDetail) {
        val isEnabled = !itemProduct.isClicked
        val buttonBackground =
            if (isEnabled) R.drawable.rounded_button_bg else R.drawable.rounded_button_bg2
        val buttonTextColor = ContextCompat.getColor(
            requireContext(),
            if (isEnabled) R.color.white else R.color.isCheckFailed
        )
        val imageUrl = if (isEnabled) itemProduct.image_url else productType.image_url
        val priceText =
            if (isEnabled) itemProduct.price.toString() else productType.lowest_price.toString()
        id = if (isEnabled) itemProduct.id else 0

        binding.btnAddCart.setBackgroundResource(buttonBackground)
        binding.btnAddCart.setTextColor(buttonTextColor)
        setBigView(imageUrl, priceText)

        binding.btnAddCart.isEnabled = isEnabled
        binding.btnIncrease.isEnabled = isEnabled
        binding.btnIncrease.isEnabled = isEnabled
        binding.txtQuantity.isEnabled = isEnabled
    }


    private fun setBigView(url: String, price: String) {
        Picasso.get().load(url).into(binding.images)
        binding.priceAll.text = "${price.toInt().toVietnameseCurrency()}/$quantity"
    }

    private fun setViewModel() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel =
            ViewModelProvider(requireActivity(), vmFactory)[ViewModelDetailProduct::class.java]

        listSize = mutableListOf()
        binding.recyclerview.setHasFixedSize(true)

    }

    private val countWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Không cần thực hiện gì trước khi thay đổi
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (updatingText) return

            val enteredValue = s?.toString() ?: ""
            if (enteredValue.isNotEmpty()) {
                val newValue = enteredValue.toIntOrNull()
                if (newValue == null || newValue !in 1..stock) {
                    updatingText = true
                    binding.txtQuantity.setText(stock.toString())
                    binding.txtMax.visibility = View.VISIBLE
                } else if (enteredValue.length > 2) {
                    updatingText = true
                    binding.txtQuantity.setText(enteredValue.substring(0, 2))
                } else {
                    binding.txtMax.visibility = View.GONE
                }
            } else {
                binding.txtMax.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable?) {
            updatingText = false
            binding.txtQuantity.text?.let {
                if (it.isNotEmpty()) binding.txtQuantity.setSelection(it.length)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.txtQuantity.removeTextChangedListener(countWatcher)
        listSize.forEach { it.isClicked = false }
        adapter?.notifyDataSetChanged()
        adapter = null
        _binding = null
    }

}