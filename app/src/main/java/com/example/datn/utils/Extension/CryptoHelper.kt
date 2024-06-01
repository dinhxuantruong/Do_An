package com.example.datn.utils.Extension

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

object CryptoHelper {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val ITERATION_COUNT = 65536
    private const val KEY_LENGTH = 256

    // Key và IV cần có kích thước cố định
    private val key = "truongxuan8322dgmailcom".toByteArray(UTF_8).copyOf(KEY_LENGTH / 8)
    private val iv = "0123456789ABCDEF".toByteArray(UTF_8).copyOf(16)

    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(text.toByteArray(UTF_8))
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = android.util.Base64.decode(encryptedText, android.util.Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, UTF_8)
    }
}