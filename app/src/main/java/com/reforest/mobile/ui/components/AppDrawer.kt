package com.reforest.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reforest.mobile.ui.theme.TranslucentGreen
import com.reforest.mobile.ui.theme.ForestGreen

@Composable
fun AppDrawerContent(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    currentRoute: String
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp),
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        windowInsets = WindowInsets(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TranslucentGreen,
                            Color(0xBB1B5E20)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "ReForest 🌳",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cuidando el planeta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                DrawerItem("Mapa de Incendios", Icons.Default.Map, "main", currentRoute, onNavigate)
                DrawerItem("Reportes de Incendios", Icons.AutoMirrored.Filled.List, "lista_incendios", currentRoute, onNavigate)
                DrawerItem("Donaciones", Icons.Default.Favorite, "lista_donaciones", currentRoute, onNavigate)
                DrawerItem("Gestión de Voluntarios", Icons.Default.Group, "voluntarios", currentRoute, onNavigate)
                DrawerItem("Notificaciones", Icons.Default.Notifications, "notificaciones", currentRoute, onNavigate)

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    route: String,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val isSelected = currentRoute == route
    NavigationDrawerItem(
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        selected = isSelected,
        onClick = { onNavigate(route) },
        icon = { Icon(icon, contentDescription = null) },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color.White.copy(alpha = 0.2f),
            unselectedIconColor = Color.White.copy(alpha = 0.8f),
            selectedIconColor = Color.White,
            unselectedTextColor = Color.White.copy(alpha = 0.8f),
            selectedTextColor = Color.White
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
