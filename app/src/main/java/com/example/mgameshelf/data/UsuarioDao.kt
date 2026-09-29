package com.example.mgameshelf.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombre AND passwordHash = :hash LIMIT 1")
    suspend fun login(nombre: String, hash: String): Usuario?
}