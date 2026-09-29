package com.example.mgameshelf.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mgameshelf.ui.theme.FondoSecundario
import com.example.mgameshelf.ui.theme.Gris
import com.example.mgameshelf.ui.theme.MoradoPrincipal

/**
 * Pantalla de login y registro. Cambia entre modos con un TextButton.
 */
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var modoRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val camposColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoSecundario,
        unfocusedContainerColor = FondoSecundario,
        focusedBorderColor = MoradoPrincipal,
        unfocusedBorderColor = Color.Transparent,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = MoradoPrincipal
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícono dentro de un círculo morado, a modo de logo simple.
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MoradoPrincipal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.VideogameAsset,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "GameShelf",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MoradoPrincipal
        )
        Spacer(Modifier.height(4.dp))
        Text(
            if (modoRegistro) "Crea tu cuenta para empezar" else "Bienvenido de vuelta",
            style = MaterialTheme.typography.bodyMedium,
            color = Gris
        )
        Spacer(Modifier.height(28.dp))

        // Los campos van dentro de una tarjeta con el color de superficie
        // del design system, en vez de flotar sueltos sobre el fondo.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FondoSecundario)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; mensaje = null },
                    label = { Text("Usuario") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = camposColors,
                    modifier = Modifier.fillMaxWidth()
                )

                if (modoRegistro) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; mensaje = null },
                        label = { Text("Email") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = camposColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; mensaje = null },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = camposColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (modoRegistro) {
                            viewModel.registrar(nombre, email, password) { ok, msg ->
                                mensaje = msg
                                if (ok) onLoginExitoso()
                            }
                        } else {
                            viewModel.login(nombre, password) { ok, msg ->
                                mensaje = msg
                                if (ok) onLoginExitoso()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        if (modoRegistro) "Registrarme" else "Entrar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = {
            modoRegistro = !modoRegistro
            mensaje = null
        }) {
            Text(
                if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión"
                else "¿No tienes cuenta? Regístrate",
                color = MoradoPrincipal
            )
        }

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color(0xFFFACC15))
        }
    }
}