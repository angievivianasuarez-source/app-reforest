package com.reforest.mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.reforest.mobile.data.apiservicios.ApiClient
import com.reforest.mobile.data.model.Donacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonacionViewModel : ViewModel() {

    private val _donaciones = MutableStateFlow<List<Donacion>>(emptyList())
    val donaciones: StateFlow<List<Donacion>> = _donaciones

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    init {
        getDonaciones()
    }

    fun getDonaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ApiClient.instance.getDonaciones()
                if (response.isSuccessful) {
                    _donaciones.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarDonacion(entidad: String, especie: String, cantidad: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = false
            try {
                val nuevaDonacion = Donacion(entidad = entidad, especie = especie, cantidad = cantidad)
                val response = ApiClient.instance.registrarDonacion(nuevaDonacion)
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
                        getDonaciones()
                    } else {
                        _error.value = "El servidor rechazó la donación"
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
