package com.example.datn.view.Detail


import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.utils.MyButtonClickListener
import com.example.blogapp.utils.MySwipeHelper
import com.example.datn.R
import com.example.datn.adapter.cartAdapter2
import com.example.datn.data.dataresult.ItemCartsWithTotal
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityCartBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extention.MyButton
import com.example.datn.utils.Extention.NumberExtensions.snackBar
import com.example.datn.utils.Extention.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Products.CartViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory

class CartActivity : AppCompatActivity() {
    private var _binding: ActivityCartBinding? = null
    private var adapter: cartAdapter2? = null
    private lateinit var listCart: MutableList<ItemCartsWithTotal>
    private val binding get() = _binding!!
    private lateinit var viewModel: CartViewModel
    private var swipe: MySwipeHelper? = null
    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()
        deleteAllCart()
        swipeCart()
        viewModel.getAllCart()

        binding.toolBarCart.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.checkBoxAll.setOnClickListener {
            if (binding.checkBoxAll.isChecked) {
                viewModel.allBox()
            } else {
                viewModel.deleteCheckBoxAll()
            }
        }
    }

    private fun swipeCart() {
        swipe = object : MySwipeHelper(this, binding.recyclerViewCart, 200) {
            override fun instantiateButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(this@CartActivity, "Delete", 40, 0, Color.parseColor("#FFFF0000"),
                        object : MyButtonClickListener {
                            override fun onClickedItem(pos: Int) {
                                position = pos
                                viewModel.deleteItemCart(listCart[pos].id)
                            }
                        })
                )
            }
        }
    }

    private fun deleteAllCart() {
        binding.toolBarCart.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deleteAllCart -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Xác nhận xóa")
                    builder.setMessage("Bạn có chắc chắn muốn xóa toàn bộ sản sản phẩm khỏi giỏ hàng không?")
                    builder.setPositiveButton("Xóa") { dialog, _ ->
                        // Gọi hàm xóa sản phẩm yêu thích
                        viewModel.deleteCart()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Hủy") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
            true
        }
    }

    private fun observeView() {
        viewModel.resultPlusItemCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    adapter?.updateCartCount(position)
                    setTotal(it.data.total.toVietnameseCurrency())
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultDeleteItemCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    listCart.removeAt(position)
                    adapter?.notifyDataSetChanged()
                    setTotal(it.data.total.toVietnameseCurrency())
                    if (listCart.isEmpty()) {
                        setCheckBoxALl(false)
                        visibilityGone()
                    }
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultDeleteCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    adapter?.clearAllCart()
                    visibilityGone()
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultDeleteCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    Toast.makeText(this@CartActivity, it.data.message, Toast.LENGTH_SHORT).show()
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultDeleteCheckBox.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    adapter?.checkAllItems(false)
                    setTotal(it.data.total.toVietnameseCurrency())
                    setTextButton("Mua hàng")
                }

                is ResponseResult.Error -> {
                    adapter?.checkAllItems(true)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.resultAllBox.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    adapter?.checkAllItems(true)
                    setTotal(it.data.total.toVietnameseCurrency())
                    setTextButton("Mua hàng (${listCart.size})")
                }

                is ResponseResult.Error -> {
                    adapter?.checkAllItems(false)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.resultMinusItemCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    adapter?.updateCartCountMinus(position)
                    if (it.data.status){
                        visibilityGone()
                    }
                    setTotal(it.data.total.toVietnameseCurrency())
                }

                is ResponseResult.Error -> {
                    this@CartActivity.snackBar(it.message)
                }
            }
        }
        viewModel.resultCart.observe(this) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    val data = result.data.itemCartsWithTotal
                    if (data.isNotEmpty()) {
                        visibility()
                        listCart.clear()
                        data.forEach { item ->
                            listCart.add(item)
                        }
                        adapter =
                            cartAdapter2(this@CartActivity, object : cartAdapter2.onClickCart {
                                override fun checkBoxClick(itemCart: ItemCartsWithTotal) {
                                    viewModel.changeCart(itemCart.id)
                                }

                                override fun updateCheckBoxAll(isChecked: Boolean) {
                                    binding.checkBoxAll.isChecked = isChecked
                                }

                                override fun plusCart(itemCart: ItemCartsWithTotal, pos: Int) {
                                    viewModel.plusItemCart(itemCart.id)
                                    position = pos
                                }

                                override fun minusCart(itemCart: ItemCartsWithTotal, pos: Int, minusCheck : Boolean) {
                                    position = pos
                                    if (!minusCheck){
                                        viewModel.minusItemCart(itemCart.id)
                                    }else{
                                        val builder = AlertDialog.Builder(this@CartActivity)
                                        builder.setTitle("Xác nhận xóa")
                                        builder.setMessage("Bạn có chắc chắn muốn xóa sản sản phẩm khỏi giỏ hàng không?")
                                        builder.setPositiveButton("Xóa") { dialog, _ ->
                                            // Gọi hàm xóa sản phẩm yêu thích
                                            viewModel.minusItemCart(itemCart.id)
                                            dialog.dismiss()
                                        }
                                        builder.setNegativeButton("Hủy") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        val dialog = builder.create()
                                        dialog.show()
                                    }
                                }

                                override fun updateQuantity(
                                    itemCart: ItemCartsWithTotal,
                                    newQuantity: Int
                                ) {

                                }

                            }, listCart)
                        binding.recyclerViewCart.adapter = adapter // Set adapter cho RecyclerView
                    }
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultChangeCart.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    setTotal(it.data.total.toVietnameseCurrency())
                    setCheckBoxALl(it.data.status)
                    setTextButton("Mua hàng (${it.data.count})")
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun visibilityGone() {
        binding.layoutCart.visibility = View.GONE
        binding.toolBarCart.menu.clear()
    }

    private fun visibility() {
        val menu = binding.toolBarCart.menu
        menuInflater.inflate(R.menu.cart_menu, menu)
        binding.layoutCart.visibility = View.VISIBLE
    }

    private fun setTotal(total: String) {
        binding.total.text = total
    }

    private fun setCheckBoxALl(isCheck: Boolean) {
        binding.checkBoxAll.isChecked = isCheck
    }

    private fun setTextButton(text: String) {
        binding.btnMua.text = text
    }

    private fun init() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this@CartActivity, vmFactory)[CartViewModel::class.java]

        listCart = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        swipe = null
        _binding = null
    }
}