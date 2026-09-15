package com.reforest.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.reforest.mobile.data.apiauth.AuthApiClient
import com.reforest.mobile.data.apiservicios.ApiClient
import com.reforest.mobile.ui.navigation.NavGraph   // 👈 IMPORTAR NavGraph
import com.reforest.mobile.ui.theme.ReForestMobileTheme
import com.reforest.mobile.utils.TokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar TokenManager
        val tokenManager = TokenManager(this)

        // Inicializar clientes
        AuthApiClient.init(applicationContext)
        ApiClient.init(tokenManager)

        setContent {
            ReForestMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()   // ✅ Sin parámetros (usa rememberNavController por defecto)
                }
            }
        }
    }
}