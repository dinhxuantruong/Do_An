package com.example.datn.view.MainView

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.data.dataresult.ProductTypeDetail
import com.example.datn.databinding.FragmentFavoriteBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.FavoriteViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private var _adapter: ProductPagerAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var viewModel: FavoriteViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[FavoriteViewModel::class.java]


        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener {

            override fun onClickedItem(itemProduct: ProductType) {
                val intent = Intent(requireActivity(), ProductActivity::class.java)
                intent.putExtra("id", itemProduct.id)
                startActivity(intent)
            }

            override fun onLongItemClick(itemProduct: ProductType) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Xác nhận xóa")
                builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi danh sách yêu thích không?")
                builder.setPositiveButton("Xóa") { dialog, _ ->
                    // Gọi hàm xóa sản phẩm yêu thích
                    viewModel.addFavorite(itemProduct.id)
                    dialog.dismiss()
                }
                builder.setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
        // Sử dụng RetrofitClient để lấy instance của RetrofitService
        //  val retrofitService = RetrofitClient.retrofitService
        binding.recyclerviewFavo.adapter = adapter


        viewModel.resultAddFavorite.observe(viewLifecycleOwner){
            when(it){
                is ResponseResult.Success -> {
                    lifecycleScope.launch {
                        viewModel.getProductFavorite().observe(viewLifecycleOwner) {data ->
                            data?.let {
                                adapter.submitData(lifecycle, data)
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        adapter.addLoadStateListener { loadState ->
            // show empty list
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading
            )
                binding.progressDialog.isVisible = true
            else {
                binding.progressDialog.isVisible = false
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(requireContext(), it.error.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }

        // Sử dụng lifecycleScope để chạy coroutine và thu thập dữ liệu
        lifecycleScope.launch {
            viewModel.getProductFavorite().observe(viewLifecycleOwner) {
                Log.e("MAIN",it.toString())
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }

}