package com.example.garageexpress.ui.producto

data class ProductoUiState(
    val id: Int = 0,

    val codigo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val precio: String = "",
    val cantidad: String = "",
    val stockMinimo: String = "",
    val qrContenido: String? = null,
    val codigoError: String? = null,
    val nombreError: String? = null,
    val categoriaError: String? = null,
    val precioError: String? = null,
    val cantidadError: String? = null,
    val stockMinimoError: String? = null,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)
