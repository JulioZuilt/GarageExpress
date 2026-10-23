package com.example.garageexpress.ui.producto

import com.example.garageexpress.data.local.ProductoEntity
enum class TipoOperacion(val etiqueta: String) {
    VENTA("Venta"),
    REPOSICION("Reposicion"),
    CONSUMO("Consumo en servicio"),
    AJUSTE("Ajuste"),
    DEVOLUCION("Devolucion")
}
data class MovimientoUiState(
    val producto: ProductoEntity? = null,
    val tipo: TipoOperacion = TipoOperacion.VENTA,
    val cantidad: String = "",
    val cantidadError: String? = null,
    val registrando: Boolean = false,
    val mensajeExito: String? = null
)
