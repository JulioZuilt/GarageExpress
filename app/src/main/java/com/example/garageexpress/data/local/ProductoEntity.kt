package com.example.garageexpress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val cantidad: Int,
    val stockMinimo: Int,
    val qrContenido: String? = null
)
