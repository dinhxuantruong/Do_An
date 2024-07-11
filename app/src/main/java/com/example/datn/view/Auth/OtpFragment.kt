package com.example.datn.view.Auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.datn.R
import com.example.datn.data.model.AcceptOTP
import com.example.datn.data.model.ForgetPass
import com.example.datn.data.model.sendOTP
import com.example.datn.databinding.FragmentOtpBinding
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.Extension.EmailType
import com.example.datn.viewmodel.Auth.AuthViewModel

class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private var _editTextList: List<EditText>? = null
    private val editTextList get() = _editTextList!!
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimeout = 1 * 60 * 1000L
    private val viewModel: AuthViewModel by activityViewModels()
    var email: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        email = arguments?.getString("email")
        val pass = arguments?.getString("pass")
        val confPass = arguments?.getString("conf pass")

        when(email){
            EmailType.REGISTER -> {
                binding.toolRegisterOTP.title = "Đăng ký tài khoản"
            }
            EmailType.FORGOT -> {
                binding.toolRegisterOTP.title = "Quên mật khẩu"
            }
        }

        binding.toolRegisterOTP.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            val otp = getOtpFromEditText()
            if (otp.length == 6) {
                when(email){
                    EmailType.REGISTER -> {
                        binding.toolRegisterOTP.title = "Register Account"
                        viewModel.authAcceptRegister(AcceptOTP(EmailType.REGISTER, otp))
                        observeRegister()
                    }
                    EmailType.FORGOT -> {
                        binding.toolRegisterOTP.title = "Forgot Password"
                        viewModel.authForgetPassword(ForgetPass(EmailType.FORGOT, otp, pass!!, confPass!!))
                        observeForgot()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        val modifiedEmail = modifyString(email!!)
        binding.emailView.text = modifiedEmail

        // Khoi tao view model
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        // Initialize the list of EditTexts
        _editTextList = listOf(
            binding.editTextDigit1,
            binding.editTextDigit2,
            binding.editTextDigit3,
            binding.editTextDigit4,
            binding.editTextDigit5,
            binding.editTextDigit6
        )

        // Add TextWatcher and OnKeyListener to handle backspace/delete key press
        setupEditTextListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startOtpCountdown()

        binding.tvSendOTP.setOnClickListener {
            if (binding.tvSendOTP.isEnabled) {
                viewModel.authSendOTP(sendOTP(email!!))
            }
        }

        observeOTP()
    }

    private fun startOtpCountdown() {
        countDownTimer = object : CountDownTimer(otpTimeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                val time = String.format("%02d:%02d", minutes, seconds)
                binding.tvSendOTP.text = "Gửi lại OTP sau $time"
            }

            override fun onFinish() {
                binding.tvSendOTP.text = "Gửi lại"
                binding.tvSendOTP.isEnabled = true
                binding.tvSendOTP.isClickable = true
                Toast.makeText(context, "Thời gian OTP đã hết hạn, vui lòng gửi lại OTP", Toast.LENGTH_SHORT).show()
            }
        }
        countDownTimer.start()
        binding.tvSendOTP.isEnabled = false
        binding.tvSendOTP.isClickable = false
    }

    private fun observeOTP() {
        viewModel.resultOTP.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    startOtpCountdown()
                }

                is ResponseResult.Error -> {
                    //Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun observeRegister() {
        viewModel.acceptResult.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    clearEmail()
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    while (findNavController().navigateUp()) {
                        findNavController().popBackStack()
                    }
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Đăng ký tài khoản không thành công", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeForgot() {
        viewModel.resultForget?.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    clearEmail()
                    val message = it.data.message
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    while (findNavController().navigateUp()) {
                        findNavController().popBackStack()
                    }
                }

                is ResponseResult.Error -> {
                    val message = it.message
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun clearEmail() {
        EmailType.REGISTER = ""
        EmailType.FORGOT = ""
    }

    private fun modifyString(originalString: String): String {
        val length = originalString.length
        val atIndex = originalString.indexOf('@')
        if (atIndex != -1 && atIndex >= 5) { // Đảm bảo có ký tự '@' và có đủ ký tự trước và sau '@'
            val stars =
                "*".repeat((length - length / 1.40).toInt()) // Số dấu sao tương ứng với số ký tự giữ nguyên trước '@' (ở đây là 5)
            return originalString.take(5) + stars + originalString.substring(atIndex)
        }
        return originalString // Trả về chuỗi ban đầu nếu không có ký tự '@' hoặc không đủ ký tự trước '@'
    }

    private fun getOtpFromEditText(): String {
        val stringBuilder = StringBuilder()
        for (editText in editTextList) {
            stringBuilder.append(editText.text.toString())
        }
        return stringBuilder.toString()
    }

    private fun setupEditTextListeners() {
        for (i in editTextList.indices) {
            val currentEditText = editTextList[i]
            val nextEditText = if (i < editTextList.size - 1) editTextList[i + 1] else null
            val prevEditText = if (i > 0) editTextList[i - 1] else null

            currentEditText.addTextChangedListener(createTextWatcher(nextEditText))
            setOnBackspaceListener(currentEditText, prevEditText)
        }
    }

    private fun createTextWatcher(nextEditText: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1 && nextEditText != null) {
                    nextEditText.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun setOnBackspaceListener(editText: EditText, prevEditText: EditText?) {
        editText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editText.text.isEmpty() && prevEditText != null) {
                prevEditText.requestFocus()
                prevEditText.text.clear()  // Xóa nội dung của ô trước đó
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _editTextList = null
        _binding = null
    }
}
