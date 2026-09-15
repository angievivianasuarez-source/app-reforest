package com.reforest.mobile.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.theme.ForestGreen
import com.reforest.mobile.ui.viewmodels.VoluntarioViewModel

/**
 * VoluntarioFormScreen: Formulario administrativo con validaciones de datos integradas.
 */
@Composable
fun VoluntarioFormScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: VoluntarioViewModel = viewModel()
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var disponibilidad by remember { mutableStateOf("") }
    
    // Observar estados del ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    // Efecto para manejar éxito
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Voluntario registrado con éxito", Toast.LENGTH_SHORT).show()
            viewModel.resetStates()
            navController.popBackStack()
        }
    }

    // Efecto para manejar errores del servidor
    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    CommonScaffold(
        navController = navController,
        title = "Alta de Voluntario",
        currentRoute = "registrar_voluntario",
        showBackButton = true
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ForestGreen
            )
            
            Text(
                text = "Registro Administrativo",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // VALIDACIÓN: Campo obligatorio y longitud
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { if (it.length <= 100) nombre = it }, // Límite de texto
                        label = { Text("Nombre Completo *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        enabled = !isLoading,
                        singleLine = true
                    )
                    
                    // VALIDACIÓN: Correo con formato correcto
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo Electrónico *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    
                    // VALIDACIÓN: Teléfono (solo números y longitud)
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() } && it.length <= 15) {
                                telefono = it 
                            }
                        },
                        label = { Text("Teléfono de contacto *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    // VALIDACIÓN: Disponibilidad obligatoria
                    OutlinedTextField(
                        value = disponibilidad,
                        onValueChange = { if (it.length <= 200) disponibilidad = it },
                        label = { Text("Disponibilidad (Horarios) *") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Fines de semana, Mañanas...") },
                        enabled = !isLoading
                    )
                    
                    Button(
                        onClick = {
                            // BLOQUE DE VALIDACIONES (Requisito de evidencia)
                            if (nombre.isBlank() || correo.isBlank() || telefono.isBlank() || disponibilidad.isBlank()) {
                                Toast.makeText(context, "Por favor complete todos los campos obligatorios (*)", Toast.LENGTH_SHORT).show()
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                                Toast.makeText(context, "El formato del correo electrónico es inválido", Toast.LENGTH_SHORT).show()
                            } else if (telefono.length < 7) {
                                Toast.makeText(context, "El teléfono debe tener al menos 7 dígitos", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.registrarVoluntario(nombre, correo, telefono, disponibilidad)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Guardar Registro")
                        }
                    }
                }
            }
        }
    }
}
