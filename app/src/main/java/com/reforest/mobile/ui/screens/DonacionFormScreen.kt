package com.reforest.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import com.reforest.mobile.ui.viewmodels.DonacionViewModel

@Composable
fun DonacionFormScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: DonacionViewModel = viewModel()
    
    var entidad by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var cantidadText by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Donación registrada con éxito", Toast.LENGTH_SHORT).show()
            viewModel.resetStates()
            navController.popBackStack()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    CommonScaffold(
        navController = navController,
        title = "Nueva Donación",
        currentRoute = "registrar_donacion",
        showBackButton = true
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ForestGreen
            )
            
            Text(
                text = "Registrar Donación",
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
                    OutlinedTextField(
                        value = entidad,
                        onValueChange = { entidad = it },
                        label = { Text("Donante (Persona o Entidad)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        enabled = !isLoading,
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = especie,
                        onValueChange = { especie = it },
                        label = { Text("Especie / Insumo") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        enabled = !isLoading,
                        singleLine = true,
                        placeholder = { Text("Ej: Árboles, herramientas, etc.") }
                    )
                    
                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() }) {
                                cantidadText = it 
                            }
                        },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = {
                            val cantidad = cantidadText.toIntOrNull()
                            if (entidad.isBlank() || especie.isBlank() || cantidadText.isBlank()) {
                                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                            } else if (cantidad == null || cantidad <= 0) {
                                Toast.makeText(context, "La cantidad debe ser válida", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.registrarDonacion(entidad, especie, cantidad)
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
                            Text("Guardar Donación")
                        }
                    }
                }
            }
        }
    }
}
