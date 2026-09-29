package com.example.mgameshelf.ui.gamelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.mgameshelf.data.Game
import com.example.mgameshelf.data.GameStatus
import com.example.mgameshelf.data.PlatformCatalog
import com.example.mgameshelf.ui.theme.Amarillo
import com.example.mgameshelf.ui.theme.FondoPrincipal
import com.example.mgameshelf.ui.theme.FondoSecundario
import com.example.mgameshelf.ui.theme.Gris
import com.example.mgameshelf.ui.theme.MoradoPrincipal
import kotlinx.coroutines.launch
import java.io.File

/**
 * Pantalla principal de la biblioteca (Read + Create/Update/Delete),
 * ahora envuelta en un ModalNavigationDrawer: la barra lateral lista
 * las plataformas agrupadas por fabricante (mismo PlatformCatalog que
 * usa el formulario de crear/editar), y tocar una filtra la lista.
 */
@Composable
fun GameListScreen(
    onCerrarSesion: () -> Unit,
    viewModel: GameListViewModel = viewModel()
) {
    val juegos by viewModel.filteredGames.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val plataformaSeleccionada by viewModel.selectedPlatform.collectAsState()

    var juegoParaEditar by remember { mutableStateOf<Game?>(null) }
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var juegoParaBorrar by remember { mutableStateOf<Game?>(null) }
    var juegoParaVerDetalle by remember { mutableStateOf<Game?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PlatformDrawer(
                plataformaSeleccionada = plataformaSeleccionada,
                onPlatformSelected = { plataforma ->
                    viewModel.onPlatformSelected(plataforma)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = FondoPrincipal,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true },
                    containerColor = MoradoPrincipal,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar juego")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Filtrar por plataforma", tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Mi Biblioteca",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (plataformaSeleccionada == null)
                                "${juegos.size} juegos en tu colección"
                            else
                                "${juegos.size} juegos · $plataformaSeleccionada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gris
                        )
                    }
                    IconButton(onClick = {
                        viewModel.cerrarSesion()
                        onCerrarSesion()
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión", tint = Gris)
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onSearchQueryChange,
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = Gris)
                    },
                    placeholder = { Text("Buscar juego...", color = Gris) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FondoSecundario,
                        unfocusedContainerColor = FondoSecundario,
                        focusedBorderColor = MoradoPrincipal,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                if (juegos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            when {
                                query.isNotBlank() -> "Sin resultados para \"$query\""
                                plataformaSeleccionada != null -> "No tienes juegos de $plataformaSeleccionada todavía"
                                else -> "Tu biblioteca está vacía. Toca + para agregar tu primer juego."
                            },
                            color = Gris
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(juegos, key = { it.id }) { juego ->
                            GameCard(
                                juego = juego,
                                onCardClick = { juegoParaVerDetalle = juego },
                                onEditClick = { juegoParaEditar = juego },
                                onDeleteClick = { juegoParaBorrar = juego }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de DETALLE (solo lectura) — se abre al tocar la tarjeta.
    juegoParaVerDetalle?.let { juego ->
        GameDetailDialog(
            juego = juego,
            onDismiss = { juegoParaVerDetalle = null },
            onEditarClick = {
                juegoParaVerDetalle = null
                juegoParaEditar = juego
            }
        )
    }

    if (mostrarDialogoCrear) {
        AddEditGameDialog(
            juegoExistente = null,
            onDismiss = { mostrarDialogoCrear = false },
            onConfirm = {
                viewModel.agregarJuego(it)
                mostrarDialogoCrear = false
            }
        )
    }

    juegoParaEditar?.let { juego ->
        AddEditGameDialog(
            juegoExistente = juego,
            onDismiss = { juegoParaEditar = null },
            onConfirm = {
                viewModel.actualizarJuego(it)
                juegoParaEditar = null
            }
        )
    }

    juegoParaBorrar?.let { juego ->
        DeleteConfirmDialog(
            juego = juego,
            onDismiss = { juegoParaBorrar = null },
            onConfirm = {
                viewModel.eliminarJuego(juego)
                juegoParaBorrar = null
            }
        )
    }
}

/**
 * Contenido de la barra lateral: "Todas las plataformas" arriba, y
 * debajo cada fabricante (Sony/Nintendo/Microsoft/Otras) como encabezado
 * seguido de sus plataformas. Tocar una plataforma la selecciona como
 * filtro; tocarla de nuevo (o "Todas") la limpia.
 */
@Composable
private fun PlatformDrawer(
    plataformaSeleccionada: String?,
    onPlatformSelected: (String?) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = FondoSecundario,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(20.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = MoradoPrincipal)
            Spacer(Modifier.width(8.dp))
            Text(
                "Plataformas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        NavigationDrawerItem(
            label = { Text("Todas las plataformas") },
            selected = plataformaSeleccionada == null,
            onClick = { onPlatformSelected(null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MoradoPrincipal.copy(alpha = 0.25f),
                selectedTextColor = MoradoPrincipal,
                unselectedTextColor = Color.White
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(8.dp))

        PlatformCatalog.AGRUPADO.forEach { (fabricante, plataformas) ->
            Text(
                text = fabricante,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Gris,
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 4.dp)
            )
            plataformas.forEach { plataforma ->
                NavigationDrawerItem(
                    label = { Text(plataforma) },
                    selected = plataformaSeleccionada == plataforma,
                    onClick = { onPlatformSelected(plataforma) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MoradoPrincipal.copy(alpha = 0.25f),
                        selectedTextColor = MoradoPrincipal,
                        unselectedTextColor = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Tarjeta de juego: portada grande, título bold, plataforma + chip
 * de estado, estrellas amarillas, botones circulares.
 */
@Composable
private fun GameCard(
    juego: Game,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoSecundario),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val path = juego.imagenUri
            if (path != null && File(path).exists()) {
                AsyncImage(
                    model = File(path),
                    contentDescription = juego.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 72.dp, height = 96.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MoradoPrincipal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = juego.titulo.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = juego.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = juego.plataforma,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gris
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    EstadoChip(estado = juego.estado)
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < juego.calificacion)
                                Icons.Filled.Star
                            else
                                Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (i < juego.calificacion) Amarillo else Gris,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            BotonCircular(
                icon = Icons.Filled.Edit,
                contentDesc = "Editar",
                tint = MoradoPrincipal,
                onClick = onEditClick
            )
            Spacer(modifier = Modifier.width(4.dp))
            BotonCircular(
                icon = Icons.Filled.Delete,
                contentDesc = "Eliminar",
                tint = Color(0xFFEF4444),
                onClick = onDeleteClick
            )
        }
    }
}

/** Chip de estado con color según el tipo. */
@Composable
private fun EstadoChip(estado: String) {
    val color = when (estado) {
        GameStatus.PLAYING   -> Color(0xFF22C55E)
        GameStatus.COMPLETED -> MoradoPrincipal
        GameStatus.ON_HOLD   -> Color(0xFFF59E0B)
        GameStatus.DROPPED   -> Color(0xFFEF4444)
        else                 -> Gris
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Botón circular con fondo translúcido. */
@Composable
private fun BotonCircular(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.15f))
    ) {
        Icon(
            icon,
            contentDescription = contentDesc,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}