package com.reforest.mobile.data.apiservicios

import com.reforest.mobile.data.model.Donacion
import com.reforest.mobile.data.model.Incendios
import com.reforest.mobile.data.model.Notificacion
import com.reforest.mobile.data.model.Voluntario
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * ApiService: Interfaz que define todos los puntos de conexión (endpoints) con el Backend.
 * Utiliza Retrofit para convertir peticiones HTTP en funciones de Kotlin.
 */
interface ApiService {

    // --- MÓDULO DE INCENDIOS ---

    // Obtiene la lista completa de incendios para el mapa y reportes
    @GET("incendios")
    suspend fun getIncendios(): Response<List<Incendios>>

    // Registra un nuevo incendio enviado desde el formulario de la App
    @POST("incendios")
    suspend fun registrarIncendio(@Body incendio: Incendios): Response<ResponseBody>

    // Actualiza el estado de un incendio (activo, controlado, extinguido)
    // Se envían como parámetros de consulta (?id=X&estado=Y)
    @POST("incendios/estado")
    suspend fun actualizarEstadoIncendio(
        @Query("id") id: Int,
        @Query("estado") estado: String
    ): Response<ResponseBody>


    // --- MÓDULO DE DONACIONES ---

    // Lista todas las donaciones recibidas por la entidad
    @GET("donaciones")
    suspend fun getDonaciones(): Response<List<Donacion>>

    // Registra una nueva donación de insumos o especies
    @POST("donaciones")
    suspend fun registrarDonacion(@Body donacion: Donacion): Response<ResponseBody>


    // --- MÓDULO DE VOLUNTARIOS ---

    // Lista todos los voluntarios registrados en el sistema administrativo
    @GET("voluntarios")
    suspend fun getVoluntarios(): Response<List<Voluntario>>

    // Da de alta un nuevo voluntario capturado desde el panel
    @POST("voluntarios")
    suspend fun registrarVoluntario(@Body voluntario: Voluntario): Response<ResponseBody>


    // --- MÓDULO DE NOTIFICACIONES ---

    // Obtiene las notificaciones del sistema para el usuario actual
    @GET("notificaciones")
    suspend fun getNotificaciones(): Response<List<Notificacion>>

    // Registra una nueva notificación (ej. alerta de sistema)
    @POST("notificaciones")
    suspend fun registrarNotificacion(@Body notificacion: Notificacion): Response<ResponseBody>
}
