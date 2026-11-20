package com.example.garageexpress.data.local

data class Proveedor(
    val id: String,
    val nombreComercial: String,
    val correo: String,
    val telefono: String,
    val categoriaSuministro: String,
    val estado: String = "Activo"
)
