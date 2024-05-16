package com.example.datn.view.Orders

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.addAddress
import com.example.datn.databinding.FragmentCreateAddressBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extention.NumberExtensions.snackBar
import com.example.datn.viewmodel.Orders.AddressesViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.io.IOException
import java.util.Locale


class CreateAddressFragment : Fragment() {


private var _binding : FragmentCreateAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddressesViewModel
    private lateinit var listTinhName: MutableList<String>
    private lateinit var listHuyenName: MutableList<String>
    private lateinit var listXaName: MutableList<String>
    private var idTinh: String? = null
    private var idHuyen: String? = null

    private val REQUEST_CONTACT = 101
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var address: String
    private lateinit var shortAddress: String
    private var isCheckAddress = 0
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedOrientationProviderClient: FusedOrientationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateAddressBinding.inflate(inflater,container,false)


        init()
        viewModel.getAllTinh()
        onclickButton()
        observeView()


        return binding.root
    }
    private fun onclickButton() {
        binding.nameLayout.setEndIconOnClickListener {
            requestContactsPermission()
        }
        binding.layoutMap.setEndIconOnClickListener {
            dialogView("Truy cập vị trí", "Bạn có chắc muốn lấy vị trí hiện tại không?", true)
        }
        binding.layoutMap.setEndIconOnLongClickListener {
            dialogView("Xác nhận", "Mở Google map đi đến vị trí", false)
            true
        }
        binding.switchDefault.setOnCheckedChangeListener { _, isChecked ->
            isCheckAddress = if (isChecked) {
                1
            } else {
                0
            }
        }
        binding.btnLocation.setOnClickListener {
            addAddress2()
        }
        binding.toolBarCart.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addAddress2() {
        val name = binding.txtName.text.toString()
        val phoneNumber = binding.txtSdt.text.toString()
        val tinh = binding.autoComple.text.toString()
        val huyen = binding.autoCompleHuyen.text.toString()
        val xa = binding.autoCompleXa.text.toString()
        val diachi = binding.txtDiachi.text.toString()
        if (name.isEmpty() || tinh.isEmpty() || huyen.isEmpty() || xa.isEmpty() || diachi.isEmpty()) {
            Toast.makeText(requireContext(), "Chưa đủ thông tin!", Toast.LENGTH_SHORT).show()
        } else if (phoneNumber.length !in 10..12) {
            Toast.makeText(requireActivity(), "Số điện thoại không đúng!", Toast.LENGTH_SHORT)
                .show()
        } else {
            viewModel.addAddressUser(
                addAddress(
                    shortAddress,
                    tinh,
                    huyen,
                    xa,
                    null,
                    null,
                    isCheckAddress.toString(),
                    name,
                    phoneNumber
                )
            )
        }
    }


    private fun dialogView(title: String, message: String, flag: Boolean) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, _ ->
            if (flag) {
                checkLocationPermission()
            } else {
                openGoogleMaps(address)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Quyền đã được cấp, thực hiện hành động liên quan đến vị trí
            checkGPS()
        } else {
            // Yêu cầu quyền truy cập vị trí
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkGPS() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val result = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                getUserLocation()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = e as ResolvableApiException
                            resolvable.startResolutionForResult(
                                requireActivity(),
                                LOCATION_PERMISSION_REQUEST_CODE
                            )
                        } catch (ex: IntentSender.SendIntentException) {
                            // Ignore the error
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // GPS is not available
                    }
                }
            }
        }
    }

    private fun getUserLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    try {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            address = addresses[0].getAddressLine(0)
                            // Tách địa chỉ thành các phần nhỏ bằng dấu phẩy
                            val addressParts = address.split(",")
                            shortAddress = if (addressParts.size >= 3) {
                                "${addressParts[0]}, ${addressParts[1]}, ${addressParts[2]}"
                            } else {
                                address
                            }
                            binding.txtDiachi.setText(shortAddress)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Không thể lấy vị trí: ${e.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun openGoogleMaps(location: String = "") {
        val uri = Uri.parse("geo:0,0?q=$location")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        // if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        // } else {
        //      Toast.makeText(this, "Google Maps chưa được cài đặt", Toast.LENGTH_SHORT).show()
        //  }
    }


    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACT
            )
        } else {
            openContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContacts()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã từ chối quyền truy cập danh bạ.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, kiểm tra lại GPS và lấy vị trí
                checkGPS()
            } else {
                // Quyền bị từ chối, thông báo cho người dùng
                Toast.makeText(
                    requireContext(),
                    "Quyền truy cập vị trí bị từ chối",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun openContacts() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, REQUEST_CONTACT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK) {
            val contactData: Uri? = data?.data
            val cursor: Cursor? = requireActivity().contentResolver.query(contactData!!, null, null, null, null)
            cursor?.moveToFirst()
            val contactId: String? =
                cursor?.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val contactName: String? =
                cursor?.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            val phoneNumber: String? = getContactPhoneNumber(contactId)
            cursor?.close()

            binding.txtName.setText(contactName)
            binding.txtSdt.setText(phoneNumber)
        }


    }

    private fun getContactPhoneNumber(contactId: String?): String? {
        var phoneNumber: String? = null
        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        return phoneNumber
    }


    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultAdd.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    requireActivity().snackBar(result.data.message)
                }

                is ResponseResult.Error -> {
                    requireActivity().snackBar(result.message)
                }
            }
        }
        viewModel.resultHuyen.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listHuyenName.clear()
                    listXaName.clear()
                    result.data.data.forEach { item ->
                        listHuyenName.add(item.name)
                    }
                    setAutoCompleteAdapter(binding.autoCompleHuyen, listHuyenName)
                    binding.autoCompleHuyen.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, i, _ ->
                            idHuyen = result.data.data[i].id
                            viewModel.getWards(idHuyen!!)
                            Toast.makeText(requireContext(), idHuyen, Toast.LENGTH_SHORT)
                                .show()
                            binding.autoCompleXa.setText("")
                            //binding.autoCompleXa.hint = "Phường xã"
                        }
                }

                is ResponseResult.Error -> {
                    // Handle error
                }
            }
        }
        viewModel.resultXa.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listXaName.clear()
                    result.data.data.forEach { item ->
                        listXaName.add(item.name)
                    }
                    setAutoCompleteAdapter(binding.autoCompleXa, listXaName)
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.resultTinh.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listHuyenName.clear()
                    listTinhName.clear()
                    result.data.data.forEach { item ->
                        listTinhName.add(item.name)
                    }
                    setAutoCompleteAdapter(binding.autoComple, listTinhName)
                    binding.autoComple.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, i, _ ->
                            idTinh = result.data.data[i].id
                            viewModel.getAllHuyen(idTinh!!)
                            binding.autoCompleHuyen.setText("")
                            //binding.autoCompleHuyen.hint = "Quận huyện"
                            binding.autoCompleXa.setText("")
                            // binding.autoCompleXa.hint = "Phường xã"
                        }
                }

                is ResponseResult.Error -> {
                    // Handle error
                }
            }
        }
    }

    private fun setAutoCompleteAdapter(
        autoCompleteTextView: AutoCompleteTextView,
        list: List<String>
    ) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_items, list)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun init() {
        listTinhName = mutableListOf()
        listHuyenName = mutableListOf()
        listXaName = mutableListOf()

        val repositoryProduct = repositoryProduct()
        val vmFactory = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(requireActivity(),vmFactory)[AddressesViewModel::class.java]
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}