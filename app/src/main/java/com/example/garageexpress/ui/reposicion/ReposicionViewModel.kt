package com.example.garageexpress.ui.reposicion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.garageexpress.data.local.ProductoEntity
import com.example.garageexpress.data.local.Proveedor
import com.example.garageexpress.data.repository.ProductoRepository
import com.example.garageexpress.data.repository.ProveedorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.max

data class ItemReposicion(
    val producto: ProductoEntity,
    val cantidadSugerida: Int,
    val proveedor: Proveedor
)

class ReposicionViewModel(
    private val productoRepo: ProductoRepository,
    private val proveedorRepo: ProveedorRepository
) : ViewModel() {

    val itemsReposicion: StateFlow<List<ItemReposicion>> = productoRepo.productos
        .map { lista ->
            lista.filter { it.cantidad <= it.stockMinimo }
                .map { prod ->
                    val sugerida = max(5, (prod.stockMinimo * 2) - prod.cantidad)
                    val prov = proveedorRepo.buscarProveedorPorCategoria(prod.categoria)
                    ItemReposicion(prod, sugerida, prov)
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    class Factory(
        private val productoRepo: ProductoRepository,
        private val proveedorRepo: ProveedorRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReposicionViewModel::class.java)) {
                return ReposicionViewModel(productoRepo, proveedorRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
