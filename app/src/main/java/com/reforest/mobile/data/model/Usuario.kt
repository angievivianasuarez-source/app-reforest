package com.reforest.mobile.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val username: String,
    val contrasena: String = "",
    val rol: String  // "civil", "bombero", "entidad"
)