package com.reforest.mobile.data.apiauth

import android.content.Context
import android.util.Log
import com.reforest.mobile.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthApiClient {

    private var retrofit: Retrofit? = null
    private var _authService: AuthService? = null

    fun init(context: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d("AuthApiClient", "Llamando a URL: ${request.url}")
                chain.proceed(request)
            }
            .build()

        val rf = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_AUTH)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        retrofit = rf
        _authService = rf.create(AuthService::class.java)
    }

    val authService: AuthService
        get() = _authService ?: throw IllegalStateException("AuthApiClient no ha sido inicializado. Llama a init() primero.")
}
