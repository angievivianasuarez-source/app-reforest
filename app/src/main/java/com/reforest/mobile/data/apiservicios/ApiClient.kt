package com.reforest.mobile.data.apiservicios

import com.reforest.mobile.utils.Constants
import com.reforest.mobile.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    @Volatile
    private var _instance: ApiService? = null

    fun init(tokenManager: TokenManager) {
        if (_instance != null) return // Evitar re-inicialización innecesaria
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor(tokenManager))
            .build()

        val rf = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_SERVICIOS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        _instance = rf.create(ApiService::class.java)
    }

    val instance: ApiService
        get() = _instance ?: throw IllegalStateException("ApiClient no ha sido inicializado. Llama a init() primero en MainActivity.")
}
