package com.example.mgameshelf.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Representa el estado completo de la pantalla de Login en un solo objeto
 * inmutable. La UI observa este estado y se redibuja cuando cambia
 * (patrón de "estado unidireccional": la UI nunca modifica el estado
 * directamente, solo llama funciones del ViewModel que lo actualizan).
 */
data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val error: String? = null,
    val loginExitoso: Boolean = false
)

/**
 * ViewModel de la pantalla de Login.
 *
 * NOTA IMPORTANTE sobre la validación: por ahora es una validación
 * SIMULADA (no consulta Room ni un backend real). Se compara contra un
 * usuario/contraseña de demostración. Esto se hace así porque GameShelf
 * todavía no tiene una tabla de usuarios/cuentas en el documento de
 * diseño; el punto de extensión para conectar autenticación real está
 * marcado abajo con un comentario "TODO", y bastaría con reemplazar el
 * cuerpo de validarCredenciales() por una consulta a un GameDao/UserDao.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Credenciales de demostración. En una versión real, esto se
    // reemplazaría por una consulta contra una tabla "users" en Room
    // (comparando un hash de la contraseña, nunca texto plano).
    private companion object {
        const val DEMO_USUARIO = "admin"
        const val DEMO_CONTRASENA = "1234"
    }

    fun onUsuarioChange(valor: String) {
        _uiState.value = _uiState.value.copy(usuario = valor, error = null)
    }

    fun onContrasenaChange(valor: String) {
        _uiState.value = _uiState.value.copy(contrasena = valor, error = null)
    }

    /**
     * Valida las credenciales ingresadas. Actualiza el estado con un
     * mensaje de error si algo falla, o marca loginExitoso = true si
     * todo es correcto (la pantalla observa ese flag para navegar).
     */
    fun intentarLogin() {
        val estado = _uiState.value

        if (estado.usuario.isBlank() || estado.contrasena.isBlank()) {
            _uiState.value = estado.copy(error = "Usuario y contraseña son obligatorios")
            return
        }

        // TODO: reemplazar por validación real (Room / API) cuando exista
        // una tabla de usuarios en el diseño de datos.
        val credencialesValidas = estado.usuario == DEMO_USUARIO &&
            estado.contrasena == DEMO_CONTRASENA

        if (credencialesValidas) {
            _uiState.value = estado.copy(error = null, loginExitoso = true)
        } else {
            _uiState.value = estado.copy(error = "Usuario o contraseña incorrectos")
        }
    }
}
