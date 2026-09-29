package com.example.mgameshelf.ui.gamelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mgameshelf.data.Game
import com.example.mgameshelf.data.GameStatus
import com.example.mgameshelf.ui.theme.Amarillo
import com.example.mgameshelf.ui.theme.FondoPrincipal
import com.example.mgameshelf.ui.theme.Gris
import com.example.mgameshelf.ui.theme.MoradoPrincipal
import java.io.File

/**
 * Diálogo de SOLO LECTURA: se abre al tocar la tarjeta de un juego en la
 * lista. Muestra la ficha completa (portada grande, plataforma, estado,
 * calificación y reseña completa) sin permitir editar nada directamente
 * aquí — para eso está el botón "Editar", que cierra este diálogo y abre
 * AddEditGameDialog (una responsabilidad por diálogo, nada se mezcla).
 */
@Composable
fun GameDetailDialog(
    juego: Game,
    onDismiss: () -> Unit,
    onEditarClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = juego.titulo,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // --- Portada grande, centrada ---
                val path = juego.imagenUri
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FondoPrincipal),
                    contentAlignment = Alignment.Center
                ) {
                    if (path != null && File(path).exists()) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = juego.titulo,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        Text(
                            text = juego.titulo.take(1).uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            color = MoradoPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --- Plataforma + estado ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = juego.plataforma,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gris
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorDeEstado(juego.estado).copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = juego.estado,
                            style = MaterialTheme.typography.labelMedium,
                            color = colorDeEstado(juego.estado),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // --- Calificación (solo lectura) ---
                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < juego.calificacion)
                                Icons.Filled.Star
                            else
                                Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (i < juego.calificacion) Amarillo else Gris,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // --- Reseña completa ---
                if (juego.resena.isNotBlank()) {
                    Column {
                        Text(
                            text = "Reseña",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gris,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = juego.resena,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Sin reseña todavía.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gris
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onEditarClick) {
                Text("Editar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

private fun colorDeEstado(estado: String): Color = when (estado) {
    GameStatus.PLAYING   -> Color(0xFF22C55E)
    GameStatus.COMPLETED -> MoradoPrincipal
    GameStatus.ON_HOLD   -> Color(0xFFF59E0B)
    GameStatus.DROPPED   -> Color(0xFFEF4444)
    else                 -> Gris
}