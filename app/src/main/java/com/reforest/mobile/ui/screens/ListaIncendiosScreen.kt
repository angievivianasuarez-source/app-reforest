package com.reforest.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.components.IncendioItem
import com.reforest.mobile.ui.viewmodels.IncendioViewModel

/**
 * ListaIncendiosScreen: Pantalla administrativa que lista todos los reportes de incendios.
 */
@Composable
fun ListaIncendiosScreen(
    navController: NavController,
    incendioViewModel: IncendioViewModel = viewModel()
) {
    val incendios by incendioViewModel.incendios.collectAsState()
    val isLoading by incendioViewModel.isLoading.collectAsState()
    val error by incendioViewModel.error.collectAsState()

    // RECARGAR SIEMPRE AL ENTRAR A LA PANTALLA
    LaunchedEffect(Unit) {
        incendioViewModel.cargarIncendios()
    }

    CommonScaffold(
        navController = navController,
        title = "Reportes de Incendios",
        currentRoute = "lista_incendios",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("reporte") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Reportar")
            }
        }
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && incendios.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { incendioViewModel.cargarIncendios() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
                incendios.isEmpty() -> {
                    Text(
                        "No hay incendios reportados en el sistema",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(incendios) { incendio ->
                            IncendioItem(
                                incendio = incendio,
                                onEstadoChange = { id, nuevoEstado ->
                                    incendioViewModel.actualizarEstado(id, nuevoEstado)
                                },
                                onViewLocation = { focused ->
                                    incendioViewModel.focusIncendio(focused)
                                    navController.navigate("main") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
            
            if (isLoading && incendios.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                )
            }
        }
    }
}
