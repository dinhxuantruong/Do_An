package com.example.datn.view.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.datn.R
import com.example.datn.data.dataresult.User
import com.example.datn.data.model.Auth
import com.example.datn.data.model.google_input
import com.example.datn.data.model.loginWithGoogle
import com.example.datn.databinding.FragmentLoginBinding
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.utils.SharePreference.UserPreferences
import com.example.datn.utils.network.Constance.Companion.CLIENT_ID
import com.example.datn.utils.network.Constance.Companion.CLIENT_SECRET
import com.example.datn.utils.network.Constance.Companion.GRANT_TYPE
import com.example.datn.utils.network.Constance.Companion.GRANT_TYPE_GOOGLE
import com.example.datn.utils.network.RetrofitInstance
import com.example.datn.view.MainView.MainViewActivity
import com.example.datn.viewmodel.Auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001 //
    private var isLoggedInFirstTime = false
    private var gso: GoogleSignInOptions? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        init()
        checkLogin()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("732233247014-4q7jg7t9hvmh4cg695d1nrjg7n408r3u.apps.googleusercontent.com")
            .requestServerAuthCode(
                "732233247014-4q7jg7t9hvmh4cg695d1nrjg7n408r3u.apps.googleusercontent.com",
                true
            )
            .requestEmail()
            .build()

        // Initialize GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso!!)

        // Map views and handle Google sign-in button click
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
            observeView()
        }



//        binding.emailEdt.onFocusChangeListener = this
//        binding.passwordEdt.onFocusChangeListener = this
//        binding.emailEdt.onFocusChangeListener = this
//        binding.passwordEdt.onFocusChangeListener = this
//
//        binding.emailEdt.addTextChangedListener(emailWatcher)
//        binding.passwordEdt.addTextChangedListener(passwordWatcher)
        binding.btnForgotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            binding.passwordEdt.text = null
            binding.passwordTil.isErrorEnabled = false
        }
        // Khởi tạo ViewModel Factory

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }


//        binding.loginButton.setOnClickListener {
//            val email = binding.emailEdt.text.toString()
//            val password = binding.passwordEdt.text.toString()
//
//
//            // Kiểm tra dữ liệu đầu vào
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                // Gọi hàm đăng nhập từ ViewModel
//                viewModel.login(Auth(email, password))
//                if (isLoggedInFirstTime) {
//                    viewModel.loginResult.observeOnceAfterInit(viewLifecycleOwner) { result ->
//                        when (result) {
//                            is DataResult.Success -> {
//                                // Xử lý khi đăng nhập thành công
//                                prefManager.setLogin(true)
//                                prefManager.setFlag(0)
//                                saveAccount(email, password)
//                                if (result.data.user.role == 1) {
//                                } else {
//                                    startActivity(
//                                        Intent(requireActivity(), MainViewActivity::class.java)
//                                    )
//                                    requireActivity().finish()
//                                }
//                            }
//
//                            is DataResult.Error -> {
//                                // Xử lý khi đăng nhập thất bại
//                                val errorMessage = result.message
//                                prefManager.setLogin(false)
//                                Toast.makeText(requireContext(), "hehehe", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//
//                        }
//                    }
//                }else{
//                    viewModel.loginResult.observeOnce(viewLifecycleOwner) { result ->
//                        when (result) {
//                            is DataResult.Success -> {
//                                // Xử lý khi đăng nhập thành công
//                                prefManager.setLogin(true)
//                                prefManager.setFlag(0)
//                                saveAccount(email, password)
//                                if (result.data.user.role == 1) {
//                                } else {
//                                    startActivity(
//                                        Intent(requireActivity(), MainViewActivity::class.java)
//                                    )
//                                    requireActivity().finish()
//                                }
//                            }
//
//                            is DataResult.Error -> {
//                                // Xử lý khi đăng nhập thất bại
//                                val errorMessage = result.message
//                                prefManager.setLogin(false)
//                                Toast.makeText(requireContext(), "hehehe", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//
//                        }
//                    }
//                    isLoggedInFirstTime = true
//                }
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Vui lòng nhập đủ thông tin đăng nhập",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
     //   }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            // Kiểm tra dữ liệu đầu vào
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Gọi hàm đăng nhập từ ViewModel
                viewModel.login(Auth(email, password))

                if (isLoggedInFirstTime) {
                    viewModel.loginResult.observeOnceAfterInit(viewLifecycleOwner) { result ->
                        handleLoginResult(result, email, password)
                    }
                } else {
                    viewModel.loginResult.observeOnce(viewLifecycleOwner) { result ->
                        handleLoginResult(result, email, password)
                        isLoggedInFirstTime = true
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin đăng nhập", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun handleLoginResult(dataResult: ResponseResult<User>, email: String, password: String) {
        when (dataResult) {
            is ResponseResult.Success -> {
                // Xử lý khi đăng nhập thành công
                prefManager.setLogin(true)
                prefManager.setFlag(0)
                saveAccount(email, password)
                if (dataResult.data.user.role == 1) {
                } else {
                    val userPreferences = UserPreferences(requireContext())
                    userPreferences.authToken.asLiveData().observe(viewLifecycleOwner){
                        RetrofitInstance.Token = it.toString()
                    }
                    startActivity(
                        Intent(requireActivity(), MainViewActivity::class.java)
                    )
                    requireActivity().finish()
                }
            }

            is ResponseResult.Error -> {
                // Xử lý khi đăng nhập thất bại
                val errorMessage = dataResult.message
                prefManager.setLogin(false)
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }


    private fun checkLogin() {
        if (prefManager.isLogin()!!) {
            binding.loading.visibility = View.VISIBLE
            val flag = prefManager.getFlag()
            Log.d("MainActivity", flag.toString())
            when (flag) {
                1 -> loginWithGoogle()
                0 -> loginWithEmailPassword()
                else -> Toast.makeText(requireContext(), "HEHEHE", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithGoogle() {
        val idToken = prefManager.getIDToken()
        val refreshToken = prefManager.getGoogleRefreshToken()
        viewModel.loginWithGoogle(
            loginWithGoogle(
                idToken, refreshToken,
                CLIENT_ID, CLIENT_SECRET, GRANT_TYPE_GOOGLE
            )
        )
        viewModel.loginWithGoogle.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    val userPreferences = UserPreferences(requireContext())
                    userPreferences.authToken.asLiveData().observe(viewLifecycleOwner){data ->
                        RetrofitInstance.Token = data.toString()
                    }
                    startActivity(Intent(requireActivity(), MainViewActivity::class.java))
                    prefManager.saveEmail(it.data.user.email)
                    requireActivity().finish()
                }

                is ResponseResult.Error -> {
                    //prefManager.removeDate()
                }
            }
        }
    }

    private fun loginWithEmailPassword() {
        val email = prefManager.getEmail()
        val password = prefManager.getPassword()
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Email hoặc password không có giá trị!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        viewModel.login(Auth(email, password))
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    val userPreferences = UserPreferences(requireContext())
                    userPreferences.authToken.asLiveData().observe(viewLifecycleOwner){
                        RetrofitInstance.Token = it.toString()
                    }
                    saveAccount(email, password)
                    val intent = Intent(requireActivity(), MainViewActivity::class.java)
                    intent.putExtra("email", result.data.user.email)
                    startActivity(intent)
                    requireActivity().finish()
                }

                is ResponseResult.Error -> {
                    // Handle error
                }
            }
        }
    }


    private fun observeView() {
        viewModel.googleResult.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    RetrofitInstance.Token = it.data.access_token
                    prefManager.setLogin(true)
                    prefManager.setFlag(1)
                    prefManager.saveGoogleRefreshToken(it.data.refresh_token)
                    Log.d("AuthActivity", prefManager.getGoogleRefreshToken().toString())
                    loginWithGoogle()
                    //startActivity(Intent(requireActivity(), MainViewActivity::class.java))
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }
    }

//    private val emailWatcher = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//        override fun afterTextChanged(s: Editable?) {
//            val email = s.toString()
//
//            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                // Email đúng định dạng, thực hiện các xử lý tương ứng
//                binding.emailTil.apply {
//                    isErrorEnabled = false
//                }
//            } else {
//                // Email không đúng định dạng, hiển thị lỗi và xóa icon check
//                binding.emailTil.apply {
//                    isErrorEnabled = true
//                    error = "Invalid email format"
//                    startIconDrawable = null
//                }
//            }
//        }
//    }
//
//    private val passwordWatcher = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//        override fun afterTextChanged(s: Editable?) {
//            val password = s.toString()
//            if (password.length in 6..15) {
//                // Độ dài mật khẩu hợp lệ, thực hiện các xử lý tương ứng
//                binding.passwordTil.apply {
//                    isErrorEnabled = false
//                    setStartIconDrawable(R.drawable.baseline_check_24)
//                    setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
//                }
//            } else {
//                // Độ dài mật khẩu không hợp lệ, hiển thị lỗi và xóa icon check
//                binding.passwordTil.apply {
//                    isErrorEnabled = true
//                    error = "Password must be between 6 and 15 characters long"
//                    startIconDrawable = null
//                }
//            }
//        }
//    }


    private fun saveAccount(email: String, password: String) {
        prefManager.saveEmail(email)
        prefManager.savePassword(password)
    }

    private fun init() {
        _prefManager = PrefManager(requireContext())
    }

    override fun onDestroyView() {
        gso = null
        mGoogleSignInClient = null
//        binding.emailEdt.removeTextChangedListener(emailWatcher)
//        binding.passwordEdt.removeTextChangedListener(passwordWatcher)
//        binding.passwordEdt.setText("")
//        binding.emailEdt.setText("")
//        binding.emailTil.isErrorEnabled = false
//        binding.passwordTil.isErrorEnabled = false
        _binding = null
        super.onDestroyView()
    }

//    override fun onFocusChange(v: View?, hasFocus: Boolean) {
//
//    }
//
//    override fun onClick(v: View?) {
//
//    }
//
//    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
//        return false
//    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode
            if (account != null) {
                val idToken = account.idToken
                prefManager.saveUrl(account.photoUrl.toString())
                if (idToken != null) {
                    Log.d("MainActivity", "Google ID Token: $idToken")
                    Log.d("MainActivity", "Google Refresh Token: $authCode")
                    prefManager.setIdtoken(idToken.toString())
                    viewModel.googleRequest(
                        google_input(
                            CLIENT_ID,
                            CLIENT_SECRET,
                            GRANT_TYPE,
                            authCode.toString()
                        )
                    )
                } else {
                    Log.e("MainActivity", "ID Token is null")
                    Toast.makeText(requireContext(), "ID Token is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle when no signed-in account is received
                Log.e("MainActivity", "Google sign in failed")
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            // Handle errors during sign-in process
            Log.e("MainActivity", "Error signing in: ${e.statusCode}")
            Toast.makeText(requireContext(), "Error signing in: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Example function to refresh access token using serverAuthCode
//    private fun refreshAccessToken(authCode: String?) {
//        // Your logic to use authCode to refresh access token from server
//        // For example, make a network request to your server endpoint with authCode
//        // and get the new access token and refresh token
//        // Update your UI or handle the new tokens accordingly
//    }

//    private fun logout() {
//        mGoogleSignInClient.signOut()
//            .addOnCompleteListener(requireActivity()) {
//                // Handle logout success
//                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
//                // Update UI or navigate to another login screen if needed
//            }
//    }

}