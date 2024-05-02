package com.example.datn.view.MainView


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.datn.R
import com.example.datn.adapter.ImageOutAdapter
import com.example.datn.adapter.categoryAdapter
import com.example.datn.adapter.productAdapter
import com.example.datn.data.ProductTypeX
import com.example.datn.data.model.categoryfilter
import com.example.datn.databinding.FragmentHomeBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.DataResult
import com.example.datn.view.Detail.ListActivity
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory


@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: HomeViewModel? = null

    private val viewModel get() = _viewModel!!
    private lateinit var listImageSlide: MutableList<SlideModel>

    private lateinit var listGrid: MutableList<categoryfilter>
    private lateinit var listImageCate: MutableList<Int>
    private lateinit var listProductMax: MutableList<ProductTypeX>
    private lateinit var listProductTime: MutableList<ProductTypeX>
    private lateinit var listImageOut: MutableList<com.example.datn.data.DataResult>
    private lateinit var listProductAll : MutableList<ProductTypeX>

    private var categoryAdapter: categoryAdapter? = null
    private var listProductAdapter: productAdapter? = null
    private var listProductTimeAdapter: productAdapter? = null
    private var listImageOutAdapter: ImageOutAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        _viewModel = ViewModelProvider(requireActivity(), vmFactory)[HomeViewModel::class.java]



        init()


        viewModel.getImageSlideAndAllProductsType()
        observeView()






        return binding.root
    }

    private fun observeView() {

        viewModel.resultProductTypeAll.observe(viewLifecycleOwner){
            when(it){
                is DataResult.Success -> {
                    listProductAll.clear()
                    val response = it.data.ProductTypes
                    response.forEach { item ->
                        listProductAll.add(item)
                    }
                    listProductAdapter = productAdapter(requireActivity(),object : productAdapter.ClickListener2{
                        override fun onClickedItem(itemProduct: ProductTypeX) {
                            startActivity(itemProduct)
                        }
                    },listProductAll)
                    binding.recyclerview.adapter = listProductAdapter
                }
                is DataResult.Error -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.resultAllPtypeMax.observe(viewLifecycleOwner) {
            when (it) {
                is DataResult.Success -> {
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

                is DataResult.Error -> {
                    listProductMax.isEmpty()
                }

                else -> {}
            }
        }

        //  viewModel.getImageSlide()
        viewModel.resultImage.observe(viewLifecycleOwner) {
            when (it) {
                is DataResult.Success -> {
                    val dataResult = it.data.data_result
                    listImageSlide.clear()
                    dataResult.forEach { item ->
                        listImageSlide.add(SlideModel(item.image_url))
                    }
                    binding.imageSlider.setImageList(listImageSlide)
                }

                is DataResult.Error -> {

                }

                else -> {}
            }
        }

        viewModel.resultAllPrTime.observe(viewLifecycleOwner) {
            when (it) {
                is DataResult.Success -> {
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

                is DataResult.Error -> {
                    listProductTime.isEmpty()
                }

                else -> {}
            }
        }

        viewModel.resultImageOut.observe(viewLifecycleOwner) {
            when (it) {
                is DataResult.Success -> {
                    listImageOut.clear()
                    val dataResult = it.data.data_result
                    dataResult.forEach { item ->
                        listImageOut.add(item)
                    }
                    listImageOutAdapter = ImageOutAdapter(requireActivity(), dataResult)
                    binding.reImage.adapter = listImageOutAdapter
                }

                is DataResult.Error -> {
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
                startActivity(Intent(requireActivity(),ListActivity::class.java))
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
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerview.isNestedScrollingEnabled = false


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

    private fun startActivity(item : ProductTypeX){
        val intent = Intent(requireActivity(),ProductActivity::class.java)
        intent.putExtra("id",item.id)
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
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryAdapter = null
        listProductAdapter = null
        listProductTimeAdapter = null
        listImageOutAdapter = null
        listProductAdapter = null
        _viewModel?.cancelJobs()
        _viewModel = null
        _binding?.appBarLayout?.removeAllViews()
        binding.imageSlider.stopSliding()
        _binding = null
    }

}


