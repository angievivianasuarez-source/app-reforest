package com.reforest.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reforest.mobile.data.apiauth.AuthApiClient
import com.reforest.mobile.data.apiauth.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("civil") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; errorMensaje = null },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; errorMensaje = null },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMensaje = null },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMensaje = null },
                    label = { Text("Contraseña (mínimo 6 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMensaje = null },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text("Rol (civil, bombero, entidad)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Opciones: civil, bombero, entidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (errorMensaje != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMensaje!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nombre.isEmpty() || correo.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            errorMensaje = "Todos los campos son obligatorios"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMensaje = "Las contraseñas no coinciden"
                            return@Button
                        }
                        if (password.length < 6) {
                            errorMensaje = "La contraseña debe tener al menos 6 caracteres"
                            return@Button
                        }

                        isLoading = true
                        errorMensaje = null

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = AuthApiClient.authService.register(
                                    RegisterRequest(
                                        nombre = nombre,
                                        correo = correo,
                                        username = username,
                                        contrasena = password,
                                        rol = rol
                                    )
                                )

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "¡Registro exitoso! Ahora inicia sesión",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("login") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    } else {
                                        try {
                                            val errorBody = response.errorBody()?.string()
                                            if (errorBody != null) {
                                                val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                                                val msg = json.get("mensaje")?.asString ?: "Error en el registro"
                                                errorMensaje = msg
                                            } else {
                                                errorMensaje = "Error en el registro"
                                            }
                                        } catch (e: Exception) {
                                            errorMensaje = "Error en el registro"
                                        }
                                    }
                                }
                            } catch (e: HttpException) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "Error de conexión: ${e.message()}"
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    errorMensaje = "Error de conexión: ${e.message}"
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Registrando..." else "Registrarse")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.navigate("login") },
                    enabled = !isLoading
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}