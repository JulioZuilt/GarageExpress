package com.example.garageexpress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// el username es la clave primara (no se repite)
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val username: String,
    val nombreCompleto: String,
    val password: String,
    val rol: String
)
