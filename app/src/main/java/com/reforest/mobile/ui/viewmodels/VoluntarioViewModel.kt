package com.reforest.mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.reforest.mobile.data.apiservicios.ApiClient
import com.reforest.mobile.data.model.Voluntario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoluntarioViewModel : ViewModel() {

    private val _voluntarios = MutableStateFlow<List<Voluntario>>(emptyList())
    val voluntarios: StateFlow<List<Voluntario>> = _voluntarios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    init {
        getVoluntarios()
    }

    fun getVoluntarios() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ApiClient.instance.getVoluntarios()
                if (response.isSuccessful) {
                    _voluntarios.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarVoluntario(nombre: String, correo: String, telefono: String, disponibilidad: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = false
            try {
                val nuevoVoluntario = Voluntario(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    disponibilidad = disponibilidad
                )
                val response = ApiClient.instance.registrarVoluntario(nuevoVoluntario)
                if (response.isSuccessful) {
                    val bodyString = response.body()?.string() ?: ""
                    
                    var isJsonSuccess = false
                    try {
                        if (bodyString.startsWith("{")) {
                            val json = JsonParser.parseString(bodyString).asJsonObject
                            isJsonSuccess = (json.get("success")?.asBoolean ?: false) || 
                                            (json.get("ok")?.asBoolean ?: false)
                        }
                    } catch (e: Exception) {}

                    if (bodyString.trim().equals("true", ignoreCase = true) || isJsonSuccess || (bodyString.isBlank() && response.code() == 200)) {
                        _success.value = true
                        getVoluntarios()
                    } else {
                        _error.value = "El servidor rechazó el registro"
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Sin detalle"
                    _error.value = "Error del servidor (${response.code()}): $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.javaClass.simpleName} - ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetStates() {
        _success.value = false
        _error.value = null
    }
}
