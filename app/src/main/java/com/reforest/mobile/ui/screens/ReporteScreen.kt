package com.reforest.mobile.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.reforest.mobile.data.model.Incendios
import com.reforest.mobile.ui.components.CommonScaffold
import com.reforest.mobile.ui.components.MapViewContainer
import com.reforest.mobile.ui.theme.ForestGreen
import com.reforest.mobile.ui.viewmodels.IncendioViewModel
import org.osmdroid.util.GeoPoint

/**
 * ReporteScreen: Pantalla para registrar un incendio con validaciones de ubicación y datos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteScreen(
    navController: NavController,
    incendioViewModel: IncendioViewModel = viewModel()
) {
    val context = LocalContext.current
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("activo") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }

    // Observar estados del ViewModel compartido
    val isLoading by incendioViewModel.isLoading.collectAsState()
    val error by incendioViewModel.error.collectAsState()
    val success by incendioViewModel.success.collectAsState()

    // Manejar éxito del registro
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "¡Incendio reportado con éxito!", Toast.LENGTH_LONG).show()
            incendioViewModel.resetStates()
            navController.popBackStack()
        }
    }

    // Manejar errores
    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            incendioViewModel.resetStates()
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        latitud = it.latitude.toString()
                        longitud = it.longitude.toString()
                    }
                }
            } catch (e: SecurityException) { }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    latitud = it.latitude.toString()
                    longitud = it.longitude.toString()
                }
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    CommonScaffold(
        navController = navController,
        title = "Nuevo Reporte",
        currentRoute = "reporte",
        showBackButton = true
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Toca el mapa para ubicar el incendio *",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // MAPA SELECTOR (VALIDACIÓN: Coordenadas de ubicación)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                MapViewContainer(
                    isPickerMode = true,
                    selectedLocation = if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
                        try {
                            GeoPoint(latitud.toDouble(), longitud.toDouble())
                        } catch (e: Exception) {
                            null
                        }
                    } else null,
                    onLocationSelected = { point ->
                        latitud = point.latitude.toString()
                        longitud = point.longitude.toString()
                    },
                    showUserLocation = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // VALIDACIÓN: Campo obligatorio y Texto demasiado largo
                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = { if (it.length <= 150) ubicacion = it },
                        label = { Text("Nombre de la zona *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                        singleLine = true,
                        enabled = !isLoading
                    )

                    // VALIDACIÓN: Campo obligatorio
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { if (it.length <= 500) descripcion = it },
                        label = { Text("Descripción del suceso *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        minLines = 3,
                        enabled = !isLoading
                    )

                    // Selector de Estado
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = estado.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estado *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("activo", "controlado", "extinguido").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        estado = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            // BLOQUE DE VALIDACIONES (Requisito de evidencia)
                            if (ubicacion.isBlank() || descripcion.isBlank() || latitud.isBlank()) {
                                Toast.makeText(context, "Por favor complete la ubicación en el mapa y los campos obligatorios (*)", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            val incendio = Incendios(
                                ubicacion = ubicacion,
                                descripcion = descripcion,
                                estado = estado,
                                latitud = latitud.toDoubleOrNull(),
                                longitud = longitud.toDoubleOrNull()
                            )

                            incendioViewModel.registrarIncendio(incendio)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Enviar Reporte")
                        }
                    }
                }
            }
        }
    }
}
