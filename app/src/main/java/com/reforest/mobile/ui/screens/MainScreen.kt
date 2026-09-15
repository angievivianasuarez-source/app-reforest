package com.reforest.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.components.MapViewContainer
import com.reforest.mobile.ui.viewmodels.IncendioViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun MainScreen(
    navController: NavController,
    incendioViewModel: IncendioViewModel = viewModel()
) {
    val context = LocalContext.current
    val incendios by incendioViewModel.incendios.collectAsState()
    val focusedIncendio by incendioViewModel.focusedIncendio.collectAsState()
    
    var zoomToAllTrigger by remember { mutableStateOf(0) }
    var centerOnUserTrigger by remember { mutableStateOf(0) }

    // RECARGAR SIEMPRE AL ENTRAR AL MAPA
    LaunchedEffect(Unit) {
        incendioViewModel.cargarIncendios()
    }

    CommonScaffold(
        navController = navController,
        title = "Mapa de Incendios",
        currentRoute = "main",
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                // BOTÓN 1: CENTRAR EN MI UBICACIÓN
                SmallFloatingActionButton(
                    onClick = { 
                        incendioViewModel.focusIncendio(null) 
                        centerOnUserTrigger++ 
                        Toast.makeText(context, "Centrando en tu posición...", Toast.LENGTH_SHORT).show()
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }

                // BOTÓN 2: VER TODOS LOS REPORTES
                SmallFloatingActionButton(
                    onClick = { 
                        if (incendios.isNotEmpty()) {
                            incendioViewModel.focusIncendio(null)
                            zoomToAllTrigger++
                            Toast.makeText(context, "Mostrando todos los reportes", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No hay incendios reportados", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.ZoomOutMap, contentDescription = "Ver todos")
                }

                // BOTÓN 3: REPORTAR NUEVO INCENDIO
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("reporte") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Reportar") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            MapViewContainer(
                incendios = incendios,
                zoomToAllTrigger = zoomToAllTrigger,
                centerOnUserTrigger = centerOnUserTrigger,
                forceCenter = focusedIncendio?.let { 
                    if (it.latitud != null && it.longitud != null) GeoPoint(it.latitud, it.longitud) else null 
                },
                onMarkerClick = { _ -> }
            )
        }
    }
}
