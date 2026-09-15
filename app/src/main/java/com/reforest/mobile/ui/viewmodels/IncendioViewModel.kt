package com.reforest.mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reforest.mobile.data.apiservicios.ApiClient
import com.reforest.mobile.data.model.Incendios
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * IncendioViewModel: Gestiona el estado y la lógica de los reportes de incendios.
 * Centraliza el listado, creación y actualización de estados.
 */
class IncendioViewModel : ViewModel() {

    private val _incendios = MutableStateFlow<List<Incendios>>(emptyList())
    val incendios: StateFlow<List<Incendios>> = _incendios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _focusedIncendio = MutableStateFlow<Incendios?>(null)
    val focusedIncendio: StateFlow<Incendios?> = _focusedIncendio

    init {
        cargarIncendios()
    }

    /**
     * Carga todos los incendios y actualiza el flujo de datos.
     */
    fun cargarIncendios() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ApiClient.instance.getIncendios()
                if (response.isSuccessful) {
                    _incendios.value = response.body() ?: emptyList()
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

    /**
     * Registra un nuevo incendio y refresca la lista automáticamente.
     */
    fun registrarIncendio(incendio: Incendios) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = false
            try {
                val response = ApiClient.instance.registrarIncendio(incendio)
                if (response.isSuccessful) {
                    val bodyString = response.body()?.string() ?: ""
                    val isTextTrue = bodyString.trim().equals("true", ignoreCase = true)
                    var isJsonSuccess = false
                    try {
                        if (bodyString.startsWith("{")) {
                            val json = JsonParser.parseString(bodyString).asJsonObject
                            isJsonSuccess = (json.get("success")?.asBoolean ?: false) || 
                                            (json.get("ok")?.asBoolean ?: false)
                        }
                    } catch (e: Exception) { }

                    if (isTextTrue || isJsonSuccess || (bodyString.isBlank() && response.code() == 200)) {
                        _success.value = true
                        cargarIncendios() // RECARGA EN TIEMPO REAL
                    } else {
                        _error.value = "El servidor rechazó el reporte"
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de un incendio y refresca la vista.
     */
    fun actualizarEstado(id: Int, nuevoEstado: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.instance.actualizarEstadoIncendio(id, nuevoEstado)
                if (response.isSuccessful) {
                    val body = response.body()?.string() ?: ""
                    if (body.contains("true") || body.startsWith("{")) {
                        cargarIncendios() // RECARGA EN TIEMPO REAL
                    } else {
                        _error.value = "El servidor rechazó el cambio de estado"
                    }
                } else {
                    _error.value = "Error al actualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun focusIncendio(incendio: Incendios?) {
        _focusedIncendio.value = incendio
    }

    fun resetStates() {
        _success.value = false
        _error.value = null
    }
}
