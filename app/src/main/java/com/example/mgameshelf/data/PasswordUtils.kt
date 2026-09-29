package com.example.mgameshelf.data

import java.security.MessageDigest

/**
 * Hash simple con SHA-256. En producción usar bcrypt/argon2.
 */
object PasswordUtils {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}