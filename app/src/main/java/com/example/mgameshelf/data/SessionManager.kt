package com.example.mgameshelf.data

import android.content.Context

/**
 * Guarda el ID del usuario logueado en SharedPreferences.
 * Sobrevive al cierre de la app.
 */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("gameshelf_session", Context.MODE_PRIVATE)

    fun guardarSesion(usuarioId: Int) {
        prefs.edit().putInt("usuario_id", usuarioId).apply()
    }

    fun obtenerUsuarioId(): Int = prefs.getInt("usuario_id", -1)

    fun cerrarSesion() = prefs.edit().clear().apply()

    fun haySesion(): Boolean = obtenerUsuarioId() != -1
}