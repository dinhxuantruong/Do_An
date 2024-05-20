package com.example.datn.view.Auth


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.datn.R
import com.example.datn.data.model.Register
import com.example.datn.databinding.FragmentRegisterBinding
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.Extension.EmailType
import com.example.datn.viewmodel.Auth.AuthViewModel


class RegisterFragment : Fragment(), View.OnClickListener, View.OnKeyListener,
    View.OnFocusChangeListener {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private var isConfirmPasswordValid = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.emailEdt.onFocusChangeListener = this
        binding.passwordEdt.onFocusChangeListener = this
        binding.confirmPasswordEdt.onFocusChangeListener = this

        binding.confirmPasswordEdt.addTextChangedListener(confirmPasswordWatcher)
        binding.emailEdt.addTextChangedListener(emailWatcher)
        binding.passwordEdt.addTextChangedListener(passwordWatcher)

        binding.registerButton.isEnabled = false
        binding.checkkk.setOnCheckedChangeListener { _, isChecked ->
            // isChecked là true nếu checkbox được tích và false nếu không được tích
            binding.registerButton.isEnabled =
                isChecked && validateEmail() && validateConFirmPassWord()
        }


        binding.toolRegister.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()
            val conf_password = binding.confirmPasswordEdt.text.toString()

            // Kiểm tra dữ liệu đầu vào
            if (email.isNotEmpty() && password.isNotEmpty() && conf_password.isNotEmpty()) {
                // Gọi hàm đăng nhập từ ViewModel
                viewModel.authRegister(Register(email, password, conf_password))
                observeView()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập đủ thông tin đăng nhập",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }


        return binding.root

    }



    private fun observeView() {
        viewModel.registerResult?.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    val email = result.data.email
                    EmailType.REGISTER = email
                    val bundle = Bundle()
                    bundle.putString("email",  EmailType.REGISTER)
                    findNavController().navigate(R.id.action_registerFragment_to_otpFragment, bundle)
                }

                is ResponseResult.Error -> {
                    // Xử lý khi đăng nhập thất bại
                    val errorMessage = result.message
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.emailEdt.removeTextChangedListener(emailWatcher)
        binding.passwordEdt.removeTextChangedListener(passwordWatcher)
        binding.confirmPasswordEdt.removeTextChangedListener(confirmPasswordWatcher)
        _binding = null
    }


    private val confirmPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val password = binding.passwordEdt.text.toString()
            val confirmPassword = s.toString()

            if (confirmPassword == password) {
                binding.confirmPassTil.apply {
                    isErrorEnabled = false
                    setStartIconDrawable(R.drawable.baseline_check_24)
                    setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                }
                isConfirmPasswordValid = true
            } else {
                binding.confirmPassTil.apply {
                    isErrorEnabled = true
                    error = "Confirm Password doesn't match with Password"
                    startIconDrawable = null
                }
                isConfirmPasswordValid = false
            }
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val email = s.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Email đúng định dạng, thực hiện các xử lý tương ứng
                binding.emailTil.apply {
                    isErrorEnabled = false
                }
            } else {
                // Email không đúng định dạng, hiển thị lỗi và xóa icon check
                binding.emailTil.apply {
                    isErrorEnabled = true
                    binding.checkkk.isChecked = false
                    error = "Invalid email format"
                    startIconDrawable = null
                }
            }
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val password = s.toString()
            if (password.length in 6..15) {
                // Độ dài mật khẩu hợp lệ, thực hiện các xử lý tương ứng
                binding.passwordTil.apply {
                    isErrorEnabled = false
                    setStartIconDrawable(R.drawable.baseline_check_24)
                    setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                }
            } else {
                // Độ dài mật khẩu không hợp lệ, hiển thị lỗi và xóa icon check
                binding.passwordTil.apply {
                    isErrorEnabled = true
                    error = "Password must be between 6 and 15 characters long"
                    startIconDrawable = null
                }
            }
        }
    }


    private fun validateEmail(): Boolean {
        var errorMessage: String? = null
        val value = binding.emailEdt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }

        if (errorMessage != null) {
            binding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        } else {
            // Nếu không có lỗi, đặt isErrorEnabled về false để ẩn lỗi khi người dùng đã nhập đúng
            binding.emailTil.isErrorEnabled = false
        }
        return errorMessage == null
    }


    private fun validatePassWord(): Boolean {
        var errorMessage: String? = null
        val value = binding.passwordEdt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) {
            errorMessage = "Password must be 6 character long"
        }

        if (errorMessage != null) {
            binding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }


    private fun validateConFirmPassWord(): Boolean {
        var errorMessage: String? = null
        val value = binding.confirmPasswordEdt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Confirm Password is required"
        } else if (value.length < 6) {
            errorMessage = "Confirm Password must be 6 character long"
        }
        if (errorMessage != null) {
            binding.confirmPassTil.apply {
                isErrorEnabled = true
                error = errorMessage
                binding.checkkk.isChecked = false
            }
        }
        return errorMessage == null
    }


    override fun onClick(view: View?) {

    }

    override fun onKey(view: View?, event: Int, keyEven: KeyEvent?): Boolean {
        return false
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {

                R.id.passwordEdt -> {
                    if (hasFocus) {
                        if (binding.passwordTil.isErrorEnabled) {
                            binding.passwordTil.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassWord()) {
                            if (binding.confirmPassTil.isErrorEnabled) {
                                binding.confirmPassTil.isErrorEnabled = false
                            }

                            binding.passwordTil.apply {
                                setStartIconDrawable(R.drawable.baseline_check_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        } else {
                            binding.passwordTil.startIconDrawable = null
                        }
                    }
                }

            }
        }
    }
}
