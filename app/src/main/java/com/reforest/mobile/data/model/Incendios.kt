package com.reforest.mobile.data.model

import com.google.gson.annotations.SerializedName

data class Incendios(
    val id: Int = 0,
    val ubicacion: String,
    val descripcion: String,
    val estado: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    @SerializedName("fecha_registro")
    val fechaRegistro: String? = null
)
