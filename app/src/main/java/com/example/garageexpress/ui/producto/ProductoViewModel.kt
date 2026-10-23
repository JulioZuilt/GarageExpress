package com.example.garageexpress.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.garageexpress.data.local.ProductoEntity
import com.example.garageexpress.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class ProductoViewModel(
    private val repo: ProductoRepository
) : ViewModel() {
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()
    private val _categoriaFiltro = MutableStateFlow("Todos")
    val categoriaFiltro: StateFlow<String> = _categoriaFiltro.asStateFlow()
    private val todos: StateFlow<List<ProductoEntity>> =
        repo.productos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val categorias: List<String> = listOf("Todos") + CATEGORIAS_PRODUCTO
    val productos: StateFlow<List<ProductoEntity>> =
        combine(todos, _busqueda, _categoriaFiltro) { lista, texto, categoria ->
            lista.filter { p ->
                val coincideTexto = texto.isBlank() ||
                        p.nombre.contains(texto, ignoreCase = true) ||
                        p.codigo.contains(texto, ignoreCase = true)
                val coincideCategoria = categoria == "Todos" ||
                        p.categoria.equals(categoria, ignoreCase = true)
                coincideTexto && coincideCategoria
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onBusquedaChange(value: String) { _busqueda.value = value }
    fun onCategoriaSeleccionada(cat: String) { _categoriaFiltro.value = cat }

    private val _formState = MutableStateFlow(ProductoUiState())
    val formState: StateFlow<ProductoUiState> = _formState.asStateFlow()
    fun onCodigoChange(value: String) = _formState.update { it.copy(codigo = value, codigoError = null) }
    fun onNombreChange(value: String) = _formState.update { it.copy(nombre = value, nombreError = null) }
    fun onCategoriaChange(value: String) = _formState.update { it.copy(categoria = value, categoriaError = null) }
    fun onPrecioChange(value: String) = _formState.update { it.copy(precio = value, precioError = null) }
    fun onCantidadChange(value: String) = _formState.update { it.copy(cantidad = value, cantidadError = null) }
    fun onStockMinimoChange(value: String) = _formState.update { it.copy(stockMinimo = value, stockMinimoError = null) }
    fun onQrLeido(contenido: String) = _formState.update { it.copy(qrContenido = contenido) }

    fun limpiarQr() = _formState.update { it.copy(qrContenido = null) }

    fun nuevoProducto() { _formState.value = ProductoUiState() }

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            val p = repo.obtenerPorId(id) ?: return@launch
            _formState.value = ProductoUiState(
                id = p.id,
                codigo = p.codigo,
                nombre = p.nombre,
                categoria = p.categoria,
                precio = p.precio.toString(),
                cantidad = p.cantidad.toString(),
                stockMinimo = p.stockMinimo.toString(),
                qrContenido = p.qrContenido
            )
        }
    }
    private fun validar(estado: ProductoUiState): ProductoUiState {
        val precioNum = estado.precio.toIntOrNull()
        val cantidadNum = estado.cantidad.toIntOrNull()
        val stockNum = estado.stockMinimo.toIntOrNull()

        return estado.copy(
            codigoError = if (estado.codigo.isBlank()) "El codigo es obligatorio" else null,
            nombreError = if (estado.nombre.isBlank()) "El nombre es obligatorio" else null,
            categoriaError = if (estado.categoria.isBlank()) "La categoria es obligatoria" else null,
            precioError = when {
                estado.precio.isBlank() -> "El precio es obligatorio"
                precioNum == null -> "Debe ser un numero"
                precioNum < 0 -> "Debe ser mayor o igual a 0"
                else -> null
            },
            cantidadError = when {
                estado.cantidad.isBlank() -> "La cantidad es obligatoria"
                cantidadNum == null -> "Debe ser un numero"
                cantidadNum < 0 -> "Debe ser mayor o igual a 0"
                else -> null
            },
            stockMinimoError = when {
                estado.stockMinimo.isBlank() -> "El stock minimo es obligatorio"
                stockNum == null -> "Debe ser un numero"
                stockNum < 0 -> "Debe ser mayor o igual a 0"
                else -> null
            }
        )
    }

    fun guardar(onGuardado: () -> Unit) {
        val validado = validar(_formState.value)
        _formState.value = validado

        val hayErrores = listOf(
            validado.codigoError,
            validado.nombreError,
            validado.categoriaError,
            validado.precioError,
            validado.cantidadError,
            validado.stockMinimoError
        ).any { it != null }
        if (hayErrores) return

        _formState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val entidad = ProductoEntity(
                id = validado.id,
                codigo = validado.codigo.trim(),
                nombre = validado.nombre.trim(),
                categoria = validado.categoria.trim(),
                precio = validado.precio.toInt(),
                cantidad = validado.cantidad.toInt(),
                stockMinimo = validado.stockMinimo.toInt(),
                qrContenido = validado.qrContenido
            )
            if (validado.id == 0) repo.insertar(entidad) else repo.actualizar(entidad)
            _formState.update { it.copy(guardando = false, guardadoExitosamente = true) }
            onGuardado()
        }
    }

    fun eliminar(producto: ProductoEntity) {
        viewModelScope.launch { repo.eliminar(producto) }
    }

    class Factory(private val repo: ProductoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductoViewModel(repo) as T
        }
    }
}
