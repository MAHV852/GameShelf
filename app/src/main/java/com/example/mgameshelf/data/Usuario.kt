package com.example.mgameshelf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario registrado en GameShelf.
 * La contraseña NUNCA se guarda en texto plano, solo su hash SHA-256.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreUsuario: String,
    val email: String,
    val passwordHash: String
)