package com.reforest.mobile.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reforest.mobile.ui.theme.ForestGreen
import com.reforest.mobile.utils.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffold(
    navController: NavController,
    title: String,
    currentRoute: String,
    showBackButton: Boolean = false,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val isLoggedIn = tokenManager.isLoggedIn()
    var authenticated by remember { mutableStateOf(isLoggedIn) }

    LaunchedEffect(Unit) {
        if (!tokenManager.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            authenticated = true
        }
    }

    if (!authenticated) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ForestGreen)
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !showBackButton,
            drawerContent = {
                AppDrawerContent(
                    onNavigate = { route: String ->
                        scope.launch { drawerState.close() }
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo("main") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onLogout = {
                        tokenManager.clear()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    currentRoute = currentRoute
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title, color = Color.White) },
                        navigationIcon = {
                            if (showBackButton) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { navController.navigate("notificaciones") },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = ForestGreen
                        )
                    )
                },
                floatingActionButton = floatingActionButton
            ) { paddingValues ->
                content(paddingValues)
            }
        }
    }
}
