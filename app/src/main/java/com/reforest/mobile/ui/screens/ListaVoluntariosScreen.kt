package com.reforest.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reforest.mobile.data.model.Voluntario
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.theme.ForestGreen
import com.reforest.mobile.ui.viewmodels.VoluntarioViewModel

@Composable
fun ListaVoluntariosScreen(navController: NavController) {
    val viewModel: VoluntarioViewModel = viewModel()
    val voluntarios by viewModel.voluntarios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // RECARGAR AL ENTRAR
    LaunchedEffect(Unit) {
        viewModel.getVoluntarios()
    }

    CommonScaffold(
        navController = navController,
        title = "Gestión de Voluntarios",
        currentRoute = "voluntarios",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("registrar_voluntario") },
                containerColor = ForestGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Voluntario")
            }
        }
    ) { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                isLoading && voluntarios.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null && voluntarios.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.getVoluntarios() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
                voluntarios.isEmpty() -> {
                    Text(
                        "No hay voluntarios registrados en el sistema",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(voluntarios) { voluntario ->
                            VoluntarioItem(voluntario)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoluntarioItem(voluntario: Voluntario) {
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(text = "Detalle del Voluntario", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Nombre:", fontWeight = FontWeight.SemiBold)
                    Text(text = voluntario.nombre)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Correo:", fontWeight = FontWeight.SemiBold)
                    Text(text = voluntario.correo)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Teléfono:", fontWeight = FontWeight.SemiBold)
                    Text(text = voluntario.telefono)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Disponibilidad:", fontWeight = FontWeight.SemiBold)
                    Text(text = voluntario.disponibilidad)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailsDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(ForestGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = ForestGreen)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voluntario.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Disponibilidad: ${voluntario.disponibilidad}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                Icons.Default.Info, 
                contentDescription = "Ver detalles", 
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
