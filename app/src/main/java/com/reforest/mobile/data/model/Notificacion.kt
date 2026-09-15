package com.reforest.mobile.data.model

import com.google.gson.annotations.SerializedName

data class Notificacion(
    val id: Int = 0,
    @SerializedName("usuarioId")
    val usuarioId: Int,
    val mensaje: String,
    val canal: String,
    val fecha: String? = null
)