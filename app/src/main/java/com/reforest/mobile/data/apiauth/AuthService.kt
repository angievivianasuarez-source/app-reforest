package com.reforest.mobile.data.apiauth

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    /**
     * Login de usuario
     * POST /api/auth/login
     *
     * @param request identificador (correo o username) + contrasena
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registro de usuario
     * POST /api/auth/register
     *
     * @param request nombre, correo, username, contrasena, rol
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

data class LoginRequest(
    val identificador: String,
    val contrasena: String
)

data class LoginResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("token")
    val token: String?,
    @SerializedName("mensaje")
    val mensaje: String?
)

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val username: String,
    val contrasena: String,
    val rol: String
)

data class RegisterResponse(
    val mensaje: String
)
