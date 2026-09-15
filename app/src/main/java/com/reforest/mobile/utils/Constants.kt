package com.reforest.mobile.utils

object Constants {
    // ============================================================
    // 1. URLS DE LOS SERVICIOS
    // ============================================================

    // 🔐 Servicio de autenticación (login, registro)
    // ⚠️ CAMBIAR SEGÚN DONDE ESTÉ DESPLEGADO:
    // - Emulador Android: "http://10.0.2.2:8087/"
    // - Dispositivo físico (misma red): "http://192.168.1.X:8087/"
    // - Producción: "https://tudominio.com/"
    const val BASE_URL_AUTH = "https://auth.codeconheiner.com/"

    // 🌿 Servicio principal (incendios, donaciones, voluntarios, notificaciones)
    const val BASE_URL_SERVICIOS = "https://reforestapi.codeconheiner.com/api/"

    // ============================================================
    // 2. NOMBRES DE PREFERENCIAS (para guardar sesión)
    // ============================================================
    const val PREF_NAME = "ReForestPrefs"
    const val KEY_TOKEN = "token"
    const val KEY_USER = "user"

    // ============================================================
    // 3. RUTAS DE NAVEGACIÓN (para el NavController)
    // ============================================================
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_MAIN = "main"
    const val ROUTE_REPORTE = "reporte"
    const val ROUTE_LISTA_INCENDIOS = "lista_incendios"
}