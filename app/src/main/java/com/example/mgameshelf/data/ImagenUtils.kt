package com.example.mgameshelf.data

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Copia la imagen seleccionada por el usuario a almacenamiento interno,
 * para que persista aunque borre la foto original de su galería.
 */
object ImagenUtils {
    fun copiarAAlmacenamientoInterno(context: Context, uri: Uri): String? {
        return try {
            val archivo = File(context.filesDir, "portada_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                archivo.outputStream().use { output -> input.copyTo(output) }
            }
            archivo.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}