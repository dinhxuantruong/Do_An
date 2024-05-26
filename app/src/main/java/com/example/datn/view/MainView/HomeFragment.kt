package com.example.datn.view.MainView


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.datn.R
import com.example.datn.adapter.ImageOutAdapter
import com.example.datn.adapter.categoryAdapter
import com.example.datn.adapter.productAdapter
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.data.model.categoryfilter
import com.example.datn.databinding.FragmentHomeBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.view.Detail.CartActivity
import com.example.datn.view.Detail.ListActivity
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.view.Search.ListSearchActivity
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils


@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    //private val viewModel get() = _viewModel!!
    private lateinit var listImageSlide: MutableList<SlideModel>

    private lateinit var listGrid: MutableList<categoryfilter>
    private lateinit var listImageCate: MutableList<Int>
    private lateinit var listProductMax: MutableList<ProductTypeX>
    private lateinit var listProductTime: MutableList<ProductTypeX>
    private lateinit var listImageOut: MutableList<com.example.datn.data.dataresult.DataResult>
    private lateinit var listProductAll: MutableList<ProductTypeX>

    private var categoryAdapter: categoryAdapter? = null
    private var listProductAdapter: productAdapter? = null
    private var listProductTimeAdapter: productAdapter? = null
    private var listImageOutAdapter: ImageOutAdapter? = null

    private var cartQuantity : Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


       init()
        viewModel.getCartCount()
        viewModel.resultGetCartCount.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    cartQuantity = result.data.item_count
                    requireActivity().invalidateOptionsMenu()  // Yêu cầu cập nhật lại menu
                }
                is ResponseResult.Error -> {
                    // Xử lý lỗi
                }
            }
        }

        observeView()
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(requireActivity(),ListSearchActivity::class.java))
        }

        return binding.root
    }



    private fun observeView() {

        viewModel.resultProductTypeAll.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listProductAll.clear()
                    val response = it.data.ProductTypes
                    response.forEach { item ->
                        listProductAll.add(item)
                    }
                    listProductAdapter =
                        productAdapter(requireActivity(), object : productAdapter.ClickListener2 {
                            override fun onClickedItem(itemProduct: ProductTypeX) {
                                startActivity(itemProduct)
                            }
                        }, listProductAll)
                    binding.recyclerview.adapter = listProductAdapter
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultAllPtypeMax.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listProductMax.clear()
                    val dataResult = it.data.ProductTypes
                    dataResult.forEach { item ->
                        listProductMax.add(item)
                    }
                    listProductAdapter =
                        productAdapter(requireActivity(), object : productAdapter.ClickListener2 {
                            override fun onClickedItem(itemProduct: ProductTypeX) {
                                startActivity(itemProduct)
                            }

                        }, listProductMax)
                    binding.recySpbc.adapter = listProductAdapter
                }

                is ResponseResult.Error -> {
                    listProductMax.isEmpty()
                }

                else -> {}
            }
        }

        //  viewModel.getImageSlide()
        viewModel.resultImage.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    val dataResult = it.data.data_result
                    listImageSlide.clear()
                    dataResult.forEach { item ->
                        listImageSlide.add(SlideModel(item.image_url))
                    }
                    binding.imageSlider.setImageList(listImageSlide)
                }

                is ResponseResult.Error -> {

                }

                else -> {}
            }
        }

        viewModel.resultAllPrTime.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listProductTime.clear()
                    val dataResult = it.data.ProductTypes
                    dataResult.forEach { item ->
                        listProductTime.add(item)
                    }
                    listProductTimeAdapter =
                        productAdapter(requireActivity(), object : productAdapter.ClickListener2 {
                            override fun onClickedItem(itemProduct: ProductTypeX) {
                                startActivity(itemProduct)
                            }

                        }, listProductTime)
                    binding.recyPrTime.adapter = listProductTimeAdapter
                }

                is ResponseResult.Error -> {
                    listProductTime.isEmpty()
                }

                else -> {}
            }
        }

        viewModel.resultImageOut.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listImageOut.clear()
                    val dataResult = it.data.data_result
                    dataResult.forEach { item ->
                        listImageOut.add(item)
                    }
                    listImageOutAdapter = ImageOutAdapter(requireActivity(), dataResult)
                    binding.reImage.adapter = listImageOutAdapter
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun addListGrid() {
        binding.recyViewcategory.setHasFixedSize(true)
        binding.recyViewcategory.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        listGrid.add(categoryfilter(R.drawable.wig, "Hair"))
        listGrid.add(categoryfilter(R.drawable.nounlipstick, "Lipstick"))
        listGrid.add(categoryfilter(R.drawable.eyelash, "Eyelash"))
        listGrid.add(categoryfilter(R.drawable.nightcream, "Cream"))
        listGrid.add(categoryfilter(R.drawable.nounlotion, "Cleanser"))
        listGrid.add(categoryfilter(R.drawable.nounserum, "Serum"))

        categoryAdapter = categoryAdapter(object : categoryAdapter.ClickListener {
            override fun onClickedItem(itemBlog: categoryfilter) {
                startActivity(Intent(requireActivity(), ListActivity::class.java))
            }
        }, requireActivity(), listGrid)
        binding.recyViewcategory.adapter = categoryAdapter

    }

    private fun init() {
        listImageSlide = mutableListOf()
        listImageCate = mutableListOf()
        listGrid = mutableListOf()
        listProductMax = mutableListOf()
        listProductTime = mutableListOf()
        listImageOut = mutableListOf()
        listProductAll = mutableListOf()

        addListGrid()

        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)



        binding.recySpbc.setHasFixedSize(true)
        binding.recySpbc.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recySpbc.isNestedScrollingEnabled = false


        binding.recyPrTime.setHasFixedSize(true)
        binding.recyPrTime.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyPrTime.isNestedScrollingEnabled = false

        binding.reImage.setHasFixedSize(true)
        binding.reImage.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.reImage.isNestedScrollingEnabled = false

        binding.recyViewcategory.isNestedScrollingEnabled = false


        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = ""
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
            val targetColor2 = if (isCollapsed) Color.parseColor("#1EA0DB") else defaultColor


            // Tìm icon trong menu và thay đổi màu sắc
            val cartItem = binding.toolbar.menu.findItem(R.id.icCart)
            val notifiItem = binding.toolbar.menu.findItem(R.id.icNotifi)

            // Thay đổi màu sắc của collapsing toolbar tùy thuộc vào trạng thái của toolbar
            binding.collapsingToolbar.setContentScrimColor(targetColor2)

            // Thay đổi màu sắc của icon
            cartItem?.icon?.let { icon ->
                icon.mutate()
                icon.setTint(targetColor)
            }

            notifiItem?.icon?.let { icon ->
                icon.mutate()
                icon.setTint(targetColor)
            }

            // setStatusBarTranslucent(isCollapsed)
        }

//        binding.Like.setOnClickListener {
//            Toast.makeText(requireContext(), "Like Products", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun startActivity(item: ProductTypeX) {
        val intent = Intent(requireActivity(), ProductActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }

    private fun setStatusBarTranslucent(translucent: Boolean) {
        if (translucent) {
            // Apply translucent status bar
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        } else {
            // Clear translucent status bar
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[HomeViewModel::class.java]
        viewModel.getImageSlideAndAllProductsType()
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartCount()
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val menuItem  = menu.findItem(R.id.icCart)
        val actionView = menuItem.actionView
        val cartIcon = actionView!!.findViewById<TextView>(R.id.txtCartView)
        if (cartQuantity > 99){
            cartIcon.text = "99"
        }else {
            cartIcon.text = cartQuantity.toString()
        }
        cartIcon.visibility = if (cartQuantity == 0) View.GONE else View.VISIBLE
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
        super.onCreateOptionsMenu(menu, inflater)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.icCart -> {
                startActivity(Intent(requireActivity(),CartActivity::class.java))
                true
            }

            R.id.icNotifi -> {
                // Xử lý khi người dùng click vào menu item 2
                showToast("Menu item 2 clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}
    override fun onDestroyView() {
        super.onDestroyView()
        categoryAdapter = null
        listProductAdapter = null
        listProductTimeAdapter = null
        listImageOutAdapter = null
        listProductAdapter = null
//        _viewModel?.cancelJobs()
//        _viewModel = null
        _binding?.appBarLayout?.removeAllViews()
        binding.imageSlider.stopSliding()
        _binding = null
    }

}


