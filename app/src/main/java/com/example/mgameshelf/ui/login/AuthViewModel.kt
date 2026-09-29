package com.example.mgameshelf.ui.login

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mgameshelf.R
import com.example.mgameshelf.data.AppDatabase
import com.example.mgameshelf.data.Game
import com.example.mgameshelf.data.GameStatus
import com.example.mgameshelf.data.ImagenUtils
import com.example.mgameshelf.data.PasswordUtils
import com.example.mgameshelf.data.SessionManager
import com.example.mgameshelf.data.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para registro e inicio de sesión.
 * Al registrar un usuario, precarga 10 juegos demo con sus portadas.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val session = SessionManager(app)

    fun registrar(
        nombre: String,
        email: String,
        password: String,
        onResultado: (exito: Boolean, mensaje: String) -> Unit
    ) {
        viewModelScope.launch {
            if (nombre.isBlank() || email.isBlank() || password.length < 4) {
                onResultado(false, "Llena todos los campos (password mín. 4 caracteres)")
                return@launch
            }
            if (db.usuarioDao().buscarPorNombre(nombre) != null) {
                onResultado(false, "Ese nombre de usuario ya existe")
                return@launch
            }
            if (db.usuarioDao().buscarPorEmail(email) != null) {
                onResultado(false, "Ese email ya está registrado")
                return@launch
            }

            val id = db.usuarioDao().insertar(
                Usuario(
                    nombreUsuario = nombre.trim(),
                    email = email.trim(),
                    passwordHash = PasswordUtils.hash(password)
                )
            )
            session.guardarSesion(id.toInt())
            precargarJuegosDemo(id.toInt())
            onResultado(true, "¡Registro exitoso!")
        }
    }

    fun login(
        nombre: String,
        password: String,
        onResultado: (exito: Boolean, mensaje: String) -> Unit
    ) {
        viewModelScope.launch {
            val user = db.usuarioDao().login(nombre.trim(), PasswordUtils.hash(password))
            if (user == null) {
                onResultado(false, "Usuario o contraseña incorrectos")
            } else {
                session.guardarSesion(user.id)
                onResultado(true, "¡Bienvenido!")
            }
        }
    }

    /**
     * Copia los drawables demo a filesDir y los inserta como juegos
     * del usuario recién registrado.
     */
    private suspend fun precargarJuegosDemo(usuarioId: Int) = withContext(Dispatchers.IO) {
        val demos = listOf(
            Triple("Phoenix Wright: Ace Attorney", "Nintendo DS", R.drawable.aceatt),
            Triple("BioShock 2", "PS3", R.drawable.bb2),
            Triple("Fortnite", "PC", R.drawable.angellore),
            Triple("Kingdom Hearts HD 1.5 + 2.5 ReMIX", "PS4", R.drawable.kh),
            Triple("Gran Turismo 4", "PS2", R.drawable.thegoat),
            Triple("Ratchet & Clank Future: Tools of Destruction", "PS3", R.drawable.ratchetclank),
            Triple("MotorStorm: Pacific Rift", "PS3", R.drawable.motorstorm),
            Triple("Sekiro: Shadows Die Twice", "PC", R.drawable.marcogay),
            Triple("Killer7", "PS2", R.drawable.k7),
            Triple("Persona 5 Royal", "PS4", R.drawable.perusona)
        )

        val context = getApplication<Application>()
        demos.forEach { (titulo, plataforma, drawable) ->
            val uri = Uri.parse("android.resource://${context.packageName}/$drawable")
            val path = ImagenUtils.copiarAAlmacenamientoInterno(context, uri)

            db.gameDao().insertGame(
                Game(
                    titulo = titulo,
                    plataforma = plataforma,
                    estado = GameStatus.BACKLOG,
                    calificacion = 0,
                    resena = "",
                    imagenUri = path,
                    usuarioId = usuarioId
                )
            )
        }
    }
}
