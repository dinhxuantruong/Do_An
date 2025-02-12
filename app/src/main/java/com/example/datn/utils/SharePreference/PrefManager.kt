package com.example.datn.utils.SharePreference


import android.content.Context
import android.content.SharedPreferences
import com.example.datn.utils.CryptoHelper
import java.security.MessageDigest

class PrefManager(var context: Context) {
    val PRIVATE_MODE = 0

    //SharedPreference file name
    private val PREF_NAME = "SharedPreference"

    private val IS_LOGIN = "is_login"

    var preferen: SharedPreferences? = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: SharedPreferences.Editor? = preferen?.edit()

    fun setLogin(isLogin: Boolean) {
        editor?.putBoolean(IS_LOGIN, isLogin)
        editor?.commit()
    }

    fun setFlag(flag : Int){
        editor?.putInt("flag",flag)
        editor?.commit()
    }

    fun setIdtoken(id : String){
        editor?.putString("idToken",id)
        editor?.commit()
    }

    fun getIDToken() : String?{
        return preferen?.getString("idToken","123")
    }

    fun getFlag() : Int?{
        return preferen?.getInt("flag",0)
    }
    fun saveGoogleRefreshToken(token : String){
        editor?.putString("token",token)
        editor?.commit()
    }

    fun getGoogleRefreshToken(): String? {
        return preferen?.getString("token", null)
    }


    fun saveEmail(email: String) {
        editor?.putString("email", email)
        editor?.commit()
    }

    fun savePassword(password : String){
        val encryptedPassword = CryptoHelper.encrypt(password)
        editor?.putString("password", encryptedPassword)
        editor?.commit()
    }



    fun isLogin(): Boolean? {
        return preferen?.getBoolean(IS_LOGIN, false)
    }


    fun getPassword(): String? {
        val password =  preferen?.getString("password", "")
        return  CryptoHelper.decrypt(password.toString())
    }

    fun getEmail() : String? {
        return preferen?.getString("email",null)
    }

    fun removeDate() {
        editor?.clear()
        editor?.commit()
    }

}