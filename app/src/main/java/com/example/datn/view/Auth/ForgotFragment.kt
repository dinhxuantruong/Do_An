package com.example.datn.view.Auth

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.datn.data.model.sendOTP
import com.example.datn.databinding.FragmentForgotBinding
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.Extension.EmailType
import com.example.datn.viewmodel.Auth.AuthViewModel


class ForgotFragment : Fragment(), View.OnClickListener, View.OnKeyListener,
    View.OnFocusChangeListener {
    private var _binding: FragmentForgotBinding? = null
    private val binding get() = _binding!!
    private val viewModel : AuthViewModel by activityViewModels()
    private var isConfirmPasswordValid = false
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotBinding.inflate(inflater, container, false)


        binding.emailEdt.onFocusChangeListener = this
        binding.passwordEdt.onFocusChangeListener = this
        binding.confirmPasswordEdt.onFocusChangeListener = this

        binding.confirmPasswordEdt.addTextChangedListener(confirmPasswordWatcher)
        binding.emailEdt.addTextChangedListener(emailWatcher)
        binding.passwordEdt.addTextChangedListener(passwordWatcher)


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        binding.toolForget.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSendOTP.setOnClickListener {
            email = binding.emailEdt.text.toString()
            password = binding.passwordEdt.text.toString()
            confirmPassword = binding.confirmPasswordEdt.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                viewModel.authCheckAccount(sendOTP(email))
                observeView()
            } else {
                Toast.makeText(requireContext(), "Information is not accurate", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root

    }



   private fun observeView()
   {
       viewModel.resultCheckAccount?.observe(viewLifecycleOwner) {
           when (it) {
               is ResponseResult.Success -> {
                   val bundle = Bundle()
                   Log.d("MainActivity","aaa")
                   EmailType.FORGOT = email
                   bundle.putString("email", EmailType.FORGOT)
                   bundle.putString("pass", password)
                   bundle.putString("conf pass", confirmPassword)
                   findNavController().navigate(R.id.action_forgotFragment_to_otpFragment2,bundle)
               }

               is ResponseResult.Error -> {
                   Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
               }

               else -> {}
           }
       }
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
                binding.emailTil.apply {
                    isErrorEnabled = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.emailEdt.removeTextChangedListener(emailWatcher)
        binding.passwordEdt.removeTextChangedListener(passwordWatcher)
        binding.confirmPasswordEdt.removeTextChangedListener(confirmPasswordWatcher)
      //  viewModel.clear()
        _binding= null
    }
}