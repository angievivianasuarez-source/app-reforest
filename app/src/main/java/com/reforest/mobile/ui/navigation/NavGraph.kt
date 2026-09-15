package com.reforest.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reforest.mobile.ui.screens.SplashScreen
import com.reforest.mobile.ui.screens.LoginScreen
import com.reforest.mobile.ui.screens.RegisterScreen
import com.reforest.mobile.ui.screens.MainScreen
import com.reforest.mobile.ui.screens.ReporteScreen
import com.reforest.mobile.ui.screens.ListaIncendiosScreen
import com.reforest.mobile.ui.screens.ListaDonacionesScreen
import com.reforest.mobile.ui.screens.DonacionFormScreen
import com.reforest.mobile.ui.screens.ListaVoluntariosScreen
import com.reforest.mobile.ui.screens.VoluntarioFormScreen
import com.reforest.mobile.ui.screens.NotificacionesScreen
import com.reforest.mobile.ui.viewmodels.IncendioViewModel

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    // ViewModel compartido para la gestión de incendios entre Mapa, Lista y Reporte
    val incendioViewModel: IncendioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("main") {
            MainScreen(navController, incendioViewModel)
        }
        composable("reporte") {
            ReporteScreen(navController, incendioViewModel)
        }
        composable("lista_incendios") {
            ListaIncendiosScreen(navController, incendioViewModel)
        }
        composable("lista_donaciones") {
            ListaDonacionesScreen(navController)
        }
        composable("registrar_donacion") {
            DonacionFormScreen(navController)
        }
        composable("voluntarios") {
            ListaVoluntariosScreen(navController)
        }
        composable("registrar_voluntario") {
            VoluntarioFormScreen(navController)
        }
        composable("notificaciones") {
            NotificacionesScreen(navController)
        }
    }
}
