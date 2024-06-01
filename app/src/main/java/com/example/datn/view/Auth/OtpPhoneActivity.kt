package com.example.datn.view.Auth

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.datn.R
import com.example.datn.databinding.ActivityOtpPhoneBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OtpPhoneActivity : AppCompatActivity() {
    private var _binding : ActivityOtpPhoneBinding? = null
    private val binding get() = _binding!!
    private var storedVerificationId  : String?= ""
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOtpPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth
        storedVerificationId= intent.getStringExtra("storedVerificationId")
        binding.btnCheck.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId,binding.editText.text.toString())
        }

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("success", "signInWithCredential:success")

                    val user = task.result?.user
                    Toast.makeText(this@OtpPhoneActivity, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("Failed", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

}