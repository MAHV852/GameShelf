package com.example.mgameshelf.ui.gamelist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.mgameshelf.data.Game

/**
 * Diálogo de confirmación antes de borrar (Delete del requisito B).
 * Existe como pantalla separada del click del botón de borrar para
 * evitar eliminaciones accidentales: el usuario debe confirmar
 * explícitamente antes de que se dispare eliminarJuego() en el ViewModel.
 */
@Composable
fun DeleteConfirmDialog(
    juego: Game,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar juego?") },
        text = { Text("Se eliminará \"${juego.titulo}\" de tu biblioteca. Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
