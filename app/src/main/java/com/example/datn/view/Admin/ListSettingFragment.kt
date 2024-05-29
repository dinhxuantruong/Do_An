package com.example.datn.view.Admin

import FirebaseListenerObserver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.FragmentListSettingBinding
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Admin.chart.BarChartActivity
import com.example.datn.view.Admin.chart.PieChartActivity
import com.example.datn.view.Auth.AuthActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso

class ListSettingFragment : Fragment() {
    private var _binding : FragmentListSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private val firebaseListeners = mutableListOf<FirebaseListenerObserver>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListSettingBinding.inflate(inflater, container, false)

        _prefManager = PrefManager(requireContext())
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.pieChart.setOnClickListener {
            startActivity(Intent(requireActivity(),PieChartActivity::class.java))
        }

        binding.findBarChart.setOnClickListener {
            startActivity(Intent(requireActivity(),BarChartActivity::class.java))
        }
        observeView()

        return binding.root
    }

    private fun observeView() {
        viewModel.resultLogout.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    FirebaseAuth.getInstance().signOut()
                    prefManager.removeDate()
                    stopFirebaseNotification()
                    removeFirebaseListeners()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }
    }

    private fun stopFirebaseNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("your_topic_name")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Notification", "Unsubscribed successfully")
                } else {
                    Log.e("Notification", "Failed to unsubscribe")
                }
            }
    }

    private fun removeFirebaseListeners() {
        for (observer in firebaseListeners) {
            observer.onDestroy(viewLifecycleOwner)
        }
        firebaseListeners.clear()
    }

    private fun addFirebaseListener() {
        val reference = FirebaseDatabase.getInstance().reference.child("SomeChild")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle data change
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        reference.addValueEventListener(listener)
        firebaseListeners.add(FirebaseListenerObserver(reference, listener))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
