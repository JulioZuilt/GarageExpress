package com.example.garageexpress.ui.login
data class RegisterUiState(
    val username: String = "",
    val nombreCompleto: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rol: String = "asistente",
    val usernameError: String? = null,
    val nombreError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val guardando: Boolean = false,
    val errorGeneral: String? = null,
    val registrado: Boolean = false
)