package com.example.mgameshelf.ui.gamelist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mgameshelf.data.Game
import com.example.mgameshelf.data.GameStatus
import com.example.mgameshelf.data.ImagenUtils
import com.example.mgameshelf.data.PlatformCatalog
import com.example.mgameshelf.ui.theme.FondoPrincipal
import com.example.mgameshelf.ui.theme.Gris
import com.example.mgameshelf.ui.theme.MoradoPrincipal
import java.io.File

/**
 * Diálogo para Create y Update.
 * - juegoExistente = null  -> modo crear (campos vacíos).
 * - juegoExistente != null -> modo editar (campos precargados).
 */
@Composable
fun AddEditGameDialog(
    juegoExistente: Game?,
    onDismiss: () -> Unit,
    onConfirm: (Game) -> Unit
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf(juegoExistente?.titulo ?: "") }

    // Plataforma ahora es una selección cerrada (dropdown), no texto libre.
    var plataforma by remember { mutableStateOf(juegoExistente?.plataforma ?: "") }
    var menuPlataformaAbierto by remember { mutableStateOf(false) }
    var errorPlataforma by remember { mutableStateOf<String?>(null) }

    var calificacionTexto by remember {
        mutableStateOf(juegoExistente?.calificacion?.toString() ?: "0")
    }
    var resena by remember { mutableStateOf(juegoExistente?.resena ?: "") }

    // Ruta local de la portada. Empieza con la que ya tenía el juego
    // (si se está editando) y se reemplaza cuando el usuario elige una
    // foto nueva desde su almacenamiento.
    var imagenUri by remember { mutableStateOf(juegoExistente?.imagenUri) }

    // Selector de fotos nativo de Android (Photo Picker): no requiere
    // pedir permisos de almacenamiento. Al elegir una imagen, la
    // copiamos a almacenamiento interno con ImagenUtils para que
    // sobreviva aunque el usuario borre la foto original de su galería.
    val selectorDeImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val rutaGuardada = ImagenUtils.copiarAAlmacenamientoInterno(context, uri)
            if (rutaGuardada != null) {
                imagenUri = rutaGuardada
            }
        }
    }

    var estado by remember {
        mutableStateOf(juegoExistente?.estado ?: GameStatus.BACKLOG)
    }
    var menuEstadoAbierto by remember { mutableStateOf(false) }

    var errorTitulo by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (juegoExistente == null) "Agregar juego" else "Editar juego") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // --- Selector de portada ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FondoPrincipal)
                        .clickable {
                            selectorDeImagen.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val rutaActual = imagenUri
                    if (rutaActual != null && File(rutaActual).exists()) {
                        AsyncImage(
                            model = File(rutaActual),
                            contentDescription = "Portada elegida",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.AddAPhoto,
                                contentDescription = "Elegir portada",
                                tint = MoradoPrincipal,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Toca para elegir una portada",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gris
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                        errorTitulo = null
                    },
                    label = { Text("Título") },
                    isError = errorTitulo != null,
                    supportingText = { errorTitulo?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Selector de PLATAFORMA: TextField de solo lectura + ---
                // --- DropdownMenu agrupado por fabricante (Sony/Nintendo/  ---
                // --- Microsoft/Otras), tomado de PlatformCatalog.AGRUPADO. ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = plataforma,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Plataforma") },
                        isError = errorPlataforma != null,
                        supportingText = { errorPlataforma?.let { Text(it) } },
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Abrir menú")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { menuPlataformaAbierto = true }
                    )
                    DropdownMenu(
                        expanded = menuPlataformaAbierto,
                        onDismissRequest = { menuPlataformaAbierto = false },
                        modifier = Modifier.heightIn(max = 320.dp)
                    ) {
                        PlatformCatalog.AGRUPADO.forEach { (fabricante, plataformas) ->
                            // Encabezado de grupo: no seleccionable, solo etiqueta.
                            DropdownMenuItem(
                                enabled = false,
                                text = {
                                    Text(
                                        fabricante,
                                        color = MoradoPrincipal,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                onClick = {}
                            )
                            plataformas.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        plataforma = opcion
                                        errorPlataforma = null
                                        menuPlataformaAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = calificacionTexto,
                    onValueChange = { texto ->
                        // Solo dígitos, máximo 1 carácter (0-5).
                        if (texto.all { it.isDigit() } && texto.length <= 1) {
                            calificacionTexto = texto
                        }
                    },
                    label = { Text("Calificación (0-5)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de estado: TextField de solo lectura + DropdownMenu.
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Abrir menú")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { menuEstadoAbierto = true }
                    )
                    DropdownMenu(
                        expanded = menuEstadoAbierto,
                        onDismissRequest = { menuEstadoAbierto = false }
                    ) {
                        GameStatus.ALL.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    estado = opcion
                                    menuEstadoAbierto = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = resena,
                    onValueChange = { resena = it },
                    label = { Text("Reseña") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                var valido = true
                if (titulo.isBlank()) {
                    errorTitulo = "El título es obligatorio"
                    valido = false
                }
                if (plataforma.isBlank()) {
                    errorPlataforma = "Selecciona una plataforma"
                    valido = false
                }
                if (!valido) return@TextButton

                val calificacion = calificacionTexto.toIntOrNull()?.coerceIn(0, 5) ?: 0

                // .copy() preserva id y usuarioId automáticamente al editar.
                // Si es un juego nuevo, se parte de un Game vacío.
                val base = juegoExistente ?: Game(titulo = "", plataforma = "")
                onConfirm(
                    base.copy(
                        titulo = titulo.trim(),
                        plataforma = plataforma,
                        estado = estado,
                        calificacion = calificacion,
                        resena = resena.trim(),
                        imagenUri = imagenUri
                    )
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}