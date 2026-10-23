package com.example.garageexpress.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.garageexpress.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovimientoViewModel(
    private val repo: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovimientoUiState())
    val uiState: StateFlow<MovimientoUiState> = _uiState.asStateFlow()

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            val p = repo.obtenerPorId(id)
            _uiState.value = MovimientoUiState(producto = p)
        }
    }

    fun onTipoChange(tipo: TipoOperacion) =
        _uiState.update { it.copy(tipo = tipo, mensajeExito = null) }

    fun onCantidadChange(value: String) =
        _uiState.update { it.copy(cantidad = value, cantidadError = null, mensajeExito = null) }

    private fun calcularNuevoStock(stockActual: Int, tipo: TipoOperacion, cantidad: Int): Int {
        return when (tipo) {
            TipoOperacion.VENTA, TipoOperacion.CONSUMO -> stockActual - cantidad
            TipoOperacion.REPOSICION, TipoOperacion.DEVOLUCION -> stockActual + cantidad
            TipoOperacion.AJUSTE -> cantidad
        }
    }

    fun registrar() {
        val estado = _uiState.value
        val producto = estado.producto ?: return

        val cantidad = estado.cantidad.toIntOrNull()

        val error = when {
            estado.cantidad.isBlank() -> "La cantidad es obligatoria"
            cantidad == null -> "Debe ser un numero"
            cantidad <= 0 -> "Debe ser mayor a 0"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(cantidadError = error) }
            return
        }

        val nuevoStock = calcularNuevoStock(producto.cantidad, estado.tipo, cantidad!!)

        if (nuevoStock < 0) {
            _uiState.update {
                it.copy(cantidadError = "Stock insuficiente (disponible: ${producto.cantidad})")
            }
            return
        }

        _uiState.update { it.copy(registrando = true) }

        viewModelScope.launch {
            val actualizado = producto.copy(cantidad = nuevoStock)
            repo.actualizar(actualizado)
            _uiState.update {
                it.copy(
                    producto = actualizado,
                    registrando = false,
                    cantidad = "",
                    mensajeExito = "Operacion registrada con exito"
                )
            }
        }
    }

    class Factory(private val repo: ProductoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovimientoViewModel(repo) as T
        }
    }
}
