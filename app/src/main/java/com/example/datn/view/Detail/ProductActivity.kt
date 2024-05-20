package com.example.datn.view.Detail

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.datn.R
import com.example.datn.adapter.productAdapter
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityProductBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.formatNumber
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.ViewModelDetailProduct


class ProductActivity : AppCompatActivity() {
    private var _binding: ActivityProductBinding? = null
    private lateinit var listImageProduct: MutableList<SlideModel>
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModelDetailProduct
    private lateinit var listProductSame: MutableList<ProductTypeX>
    private  var adapter: productAdapter? = null
    private var id = 0
    private var cartQuantity : Int = 0
    companion object {
        var isLoggedInFirstTime = false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        id = intent.getIntExtra("id", 0)
        binding.btnCart.setOnClickListener {
            NewTaskSheet().show(supportFragmentManager, "New Task Sheet")
        }
        viewModel.getCartCount()
        viewModel.resultGetCartCount.observe(this@ProductActivity) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    cartQuantity = result.data.item_count
                    this@ProductActivity.invalidateOptionsMenu()  // Yêu cầu cập nhật lại menu
                }
                is ResponseResult.Error -> {
                    // Xử lý lỗi
                }
            }
        }
        viewModel.getDetailProduct(id)
        viewModel.resultDetail.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    Log.e("Main", it.data.ProductType.products.size.toString())
                    val productType = it.data.ProductType
                    val listImages = it.data.Images
                    listImages.forEach { image ->
                        listImageProduct.add(SlideModel(image))
                    }
                    binding.imageSlider.setImageList(listImageProduct)

                    binding.txtName.text = productType.name
                    binding.txtThuonghieu.text = "Thương hiệu: ${productType.trademark}"
                    binding.txtPrice.text =
                        productType.lowest_price.toVietnameseCurrency() + "/${productType.quantity}"
                    binding.expandableTextView.text = productType.description
                    binding.txtCountSold.text = "Đã bán " + formatNumber(productType.sold_quantity)

                    val like = productType.liked_by_current_user
                    val countFav = productType.productlikes_count
                    setFavoriteView(like,countFav)
                }

                is ResponseResult.Error -> {

                }

                else -> {}
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.txtMore.setOnClickListener {
            if (binding.expandableTextView.isExpanded) {
                binding.expandableTextView.collapse()
                binding.txtMore.text = "Xem thêm"
            } else {
                binding.expandableTextView.expand()
                binding.txtMore.text = "Thu gọn"
            }
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.addFavorite(id)
        }

        viewModel.resultAddFavorite.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    val status = it.data.status
                    val countFav = it.data.productlikes_count
                    setFavoriteView(status, countFav)
                    this@ProductActivity.snackBar(it.data.message)
                }

                is ResponseResult.Error -> {
                   this@ProductActivity.snackBar(it.message)
                }
            }
        }

        viewModel.resultProductSame.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    listProductSame.clear()
                    val product = it.data.ProductTypes
                    product.forEach { item ->
                        listProductSame.add(item)
                    }
                    if (listProductSame.isNotEmpty()) {
                        listProductSame.removeAt(0)
                    }
                    adapter = productAdapter(this, object : productAdapter.ClickListener2 {
                        override fun onClickedItem(itemBlog: ProductTypeX) {
                            Toast.makeText(this@ProductActivity, itemBlog.id.toString(), Toast.LENGTH_SHORT).show()
                        }
                    },listProductSame)

                    binding.reSame.adapter = adapter
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setFavoriteView(check: Boolean, countFav : Int) {
        binding.txtCountFav.text = " $countFav"
        val colorBlue = ContextCompat.getColor(this, R.color.colorPrimary)
        val colorWhite = ContextCompat.getColor(this, android.R.color.black)
        if (check) {
            val tintList = ColorStateList.valueOf(colorBlue)
            ImageViewCompat.setImageTintList(binding.btnFavorite, tintList)
            val backgroundTintList = ColorStateList.valueOf(colorWhite)
            binding.btnFavorite.backgroundTintList = backgroundTintList
        } else {
            val tintList = ColorStateList.valueOf(colorWhite)
            ImageViewCompat.setImageTintList(binding.btnFavorite, tintList)
            val backgroundTintList = ColorStateList.valueOf(colorBlue)
            binding.btnFavorite.backgroundTintList = backgroundTintList
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cart, menu)
        val menuItem  = menu.findItem(R.id.cart_id)
        val actionView = menuItem.actionView
        val cartIcon = actionView!!.findViewById<TextView>(R.id.txtCartView2)
        cartIcon.text = cartQuantity.toString()
        cartIcon.visibility = if (cartQuantity == 0) View.GONE else View.VISIBLE
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cart_id -> {
                startActivity(Intent(this@ProductActivity,CartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun init() {
        setSupportActionBar(binding.toolbar)
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[ViewModelDetailProduct::class.java]

        listProductSame = mutableListOf()
        binding.reSame.setHasFixedSize(true)
        binding.reSame.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.reSame.isNestedScrollingEnabled = false

        binding.expandableTextView.setAnimationDuration(100L)
        binding.expandableTextView.setInterpolator(OvershootInterpolator())

        listImageProduct = mutableListOf()
        (this as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (this as? AppCompatActivity)?.supportActionBar?.apply {
            title = ""
//            setDisplayHomeAsUpEnabled(true)
        }
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.qqq)
        Palette.from(bitmap).generate {
            if (it != null) {
                binding.collapsingToolbar.setContentScrimColor(it.getMutedColor(androidx.appcompat.R.attr.colorPrimary))
            }
        }
        binding.appBarLayout.setExpanded(true, true)

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = verticalOffset <= -(appBarLayout.totalScrollRange / 100)
            // Màu mặc định của icon
            val defaultColor = Color.WHITE


            // Màu mà icon sẽ chuyển đổi thành
            val targetColor = if (isCollapsed) Color.WHITE else defaultColor
            // val targetColor2 = if (isCollapsed) Color.parseColor("#1EA0DB") else defaultColor


            // Tìm icon trong menu và thay đổi màu sắc
            val cartItem = binding.toolbar.menu.findItem(R.id.icCart)
            val notifiItem = binding.toolbar.menu.findItem(R.id.icNotifi)

            // Thay đổi màu sắc của collapsing toolbar tùy thuộc vào trạng thái của toolbar
            binding.collapsingToolbar.setContentScrimColor(targetColor)

            // Thay đổi màu sắc của icon
            cartItem?.icon?.let { icon ->
                icon.mutate()
                icon.setTint(targetColor)
            }

            notifiItem?.icon?.let { icon ->
                icon.mutate()
                icon.setTint(targetColor)
            }
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                onBackPressed()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartCount()
    }
    override fun onDestroy() {
        super.onDestroy()
        isLoggedInFirstTime = false
        adapter = null
        _binding = null
    }
}


