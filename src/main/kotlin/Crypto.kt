package com.example

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object NoteCrypto {
    private val key = System.getenv("AES_KEY")?.let { Base64.getDecoder().decode(it) }
        ?: ByteArray(32).also { SecureRandom().nextBytes(it) } // demo fallback, для прода только env var

    private val secretKey = SecretKeySpec(key, "AES")

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