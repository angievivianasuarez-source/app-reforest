package com.reforest.mobile.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reforest.mobile.data.apiauth.AuthApiClient
import com.reforest.mobile.data.apiauth.LoginRequest
import com.reforest.mobile.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var identificador by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // El chequeo de sesión se hace ahora en el SplashScreen y CommonScaffold

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🌳",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ReForest",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Gestión Ambiental",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = identificador,
                    onValueChange = {
                        identificador = it
                        errorMensaje = null
                    },
                    label = { Text("Correo o nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMensaje = null
                    },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val imagen = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = imagen, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    enabled = !isLoading
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
                        if (identificador.isEmpty() || password.isEmpty()) {
                            errorMensaje = "Todos los campos son obligatorios"
                            return@Button
                        }
                        isLoading = true
                        errorMensaje = null

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = AuthApiClient.authService.login(
                                    LoginRequest(identificador, password)
                                )

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        val loginResponse = response.body()
                                        Log.d("LoginScreen", "Respuesta recibida: $loginResponse")

                                        if (loginResponse?.ok == true) {
                                            val token = loginResponse.token ?: ""
                                            if (token.isNotEmpty()) {
                                                tokenManager.saveToken(token)
                                                tokenManager.saveUser(identificador)
                                                Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("main") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                errorMensaje = "No se recibió token de autenticación"
                                            }
                                        } else {
                                            errorMensaje = loginResponse?.mensaje ?: "Credenciales incorrectas"
                                        }
                                    } else {
                                        try {
                                            val errorBody = response.errorBody()?.string()
                                            if (errorBody != null) {
                                                val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                                                val msg = json.get("mensaje")?.asString ?: "Credenciales incorrectas"
                                                errorMensaje = msg
                                            } else {
                                                errorMensaje = "Credenciales incorrectas"
                                            }
                                        } catch (e: Exception) {
                                            errorMensaje = "Credenciales incorrectas"
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
                    Text(if (isLoading) "Iniciando..." else "Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.navigate("register") },
                    enabled = !isLoading
                ) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}