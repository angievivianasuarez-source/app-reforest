package com.reforest.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.reforest.mobile.data.model.Incendios

/**
 * IncendioItem: Tarjeta administrativa mejorada para visualización y gestión de incendios.
 */
@Composable
fun IncendioItem(
    incendio: Incendios,
    onEstadoChange: (Int, String) -> Unit = { _, _ -> },
    onViewLocation: (Incendios) -> Unit = {} // Acción para ver en el mapa principal
) {
    // Colores dinámicos basados en el estado (Rojo/Amarillo/Verde)
    val statusColor = when (incendio.estado.lowercase()) {
        "activo" -> Color(0xFFE53935)      // Rojo
        "controlado" -> Color(0xFFFFB300)  // Amarillo/Ámbar
        "extinguido" -> Color(0xFF43A047)  // Verde
        else -> Color.Gray
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    // Diálogo de detalles completo
    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(text = incendio.ubicacion, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Estado: ${incendio.estado.uppercase()}", color = statusColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Descripción:", fontWeight = FontWeight.SemiBold)
                    Text(text = incendio.descripcion)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Coordenadas Técnicas:", fontWeight = FontWeight.SemiBold)
                    Text(text = "Latitud: ${incendio.latitud}\nLongitud: ${incendio.longitud}")
                    if (incendio.fechaRegistro != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Fecha de registro: ${incendio.fechaRegistro}", style = MaterialTheme.typography.labelSmall)
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
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila de encabezado: Icono Llama + Título + Menú
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ICONO DE LLAMA (Whatshot) con color dinámico
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(statusColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Estado del fuego",
                        tint = statusColor,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incendio.ubicacion,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = incendio.estado.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón de opciones para cambiar estado
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Estado")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        listOf("activo", "controlado", "extinguido").forEach { estado ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = when(estado) {
                                            "activo" -> Color(0xFFE53935)
                                            "controlado" -> Color(0xFFFFB300)
                                            else -> Color(0xFF43A047)
                                        }
                                    )
                                },
                                text = { Text(estado.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    onEstadoChange(incendio.id, estado)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Descripción breve
            Text(
                text = incendio.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BOTÓN DETALLES (Abre el diálogo)
                TextButton(
                    onClick = { showDetailsDialog = true }
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Detalles")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // BOTÓN VER UBICACIÓN (Navega al mapa)
                Button(
                    onClick = { onViewLocation(incendio) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Ubicación")
                }
            }
        }
    }
}
