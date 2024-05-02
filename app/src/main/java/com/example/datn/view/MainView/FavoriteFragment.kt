package com.example.datn.view.MainView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.R
import com.example.datn.databinding.FragmentFavoriteBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.FavoriteViewModel
import com.example.datn.viewmodel.Products.HomeViewModel
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

            override fun onClickedItem(itemBlog: ProductType) {
                Toast.makeText(requireContext(), itemBlog.id.toString(), Toast.LENGTH_SHORT).show()
            }
        })
        // Sử dụng RetrofitClient để lấy instance của RetrofitService
        //  val retrofitService = RetrofitClient.retrofitService
        binding.recyclerviewFavo.adapter = adapter



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