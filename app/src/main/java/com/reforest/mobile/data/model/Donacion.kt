package com.reforest.mobile.data.model

import com.google.gson.annotations.SerializedName

data class Donacion(
    val id: Int = 0,
    val entidad: String,
    val especie: String,
    val cantidad: Int,
    @SerializedName("fecha_registro")
    val fechaRegistro: String? = null
)
