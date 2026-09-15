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
import androidx.compose.material.icons.filled.Favorite
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
import com.reforest.mobile.data.model.Donacion
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.theme.ForestGreen
import com.reforest.mobile.ui.viewmodels.DonacionViewModel

@Composable
fun ListaDonacionesScreen(navController: NavController) {
    val viewModel: DonacionViewModel = viewModel()
    val donaciones by viewModel.donaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // RECARGAR AL ENTRAR
    LaunchedEffect(Unit) {
        viewModel.getDonaciones()
    }

    CommonScaffold(
        navController = navController,
        title = "Donaciones",
        currentRoute = "lista_donaciones",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("registrar_donacion") },
                containerColor = ForestGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Donación")
            }
        }
    ) { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                isLoading && donaciones.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null && donaciones.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.getDonaciones() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
                donaciones.isEmpty() -> {
                    Text(
                        "No hay donaciones registradas",
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
                        items(donaciones) { donacion ->
                            DonacionItem(donacion)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DonacionItem(donacion: Donacion) {
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(text = "Detalle de Donación", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Donante:", fontWeight = FontWeight.SemiBold)
                    Text(text = donacion.entidad)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Especie / Insumo:", fontWeight = FontWeight.SemiBold)
                    Text(text = donacion.especie)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Cantidad:", fontWeight = FontWeight.SemiBold)
                    Text(text = donacion.cantidad.toString())
                    if (donacion.fechaRegistro != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Fecha de registro:", fontWeight = FontWeight.SemiBold)
                        Text(text = donacion.fechaRegistro)
                    }
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(ForestGreen.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = ForestGreen)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donacion.entidad,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${donacion.especie} x ${donacion.cantidad}",
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
            
            if (donacion.fechaRegistro != null) {
                Text(
                    text = "Recibido: ${donacion.fechaRegistro}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
