package com.reforest.mobile.data.apiservicios

import com.reforest.mobile.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Cookie", "JWT=$token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
