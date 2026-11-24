package com.example

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object NoteCrypto {
    private fun loadKey(): ByteArray {
        val b64 = System.getenv("AES_KEY")
            ?: System.getenv("AES_KEY_FILE")?.let { java.io.File(it).readText().trim() }
            ?: error("Set AES_KEY or AES_KEY_FILE")
        return Base64.getDecoder().decode(b64).also { require(it.size == 32) }
    }
    private val secretKey = SecretKeySpec(loadKey(), "AES")

    fun encrypt(plainText: String): String {
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    fun decrypt(cipherText: String): String {
        val data = Base64.getDecoder().decode(cipherText)
        val iv = data.copyOfRange(0, 12)
        val encrypted = data.copyOfRange(12, data.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encrypted))
    }
}