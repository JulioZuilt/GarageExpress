package com.example.garageexpress.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.garageexpress.data.repository.RegistroResultado
import com.example.garageexpress.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class RegisterViewModel(
    private val repo: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(v: String) = _uiState.update { it.copy(username = v, usernameError = null, errorGeneral = null) }
    fun onNombreChange(v: String) = _uiState.update { it.copy(nombreCompleto = v, nombreError = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, passwordError = null) }
    fun onConfirmChange(v: String) = _uiState.update { it.copy(confirmPassword = v, confirmError = null) }
    fun onRolChange(v: String) = _uiState.update { it.copy(rol = v) }

    fun registrar(onOk: () -> Unit) {
        val estado = _uiState.value

        // aqui estan las vaalidaciones
        val validado = estado.copy(
            usernameError = if (estado.username.isBlank()) "El usuario es obligatorio" else null,
            nombreError = if (estado.nombreCompleto.isBlank()) "El nombre es obligatorio" else null,
            passwordError = when {
                estado.password.isBlank() -> "La contrasena es obligatoria"
                estado.password.length < 3 -> "Minimo 3 caracteres"
                else -> null
            },
            confirmError = if (estado.password != estado.confirmPassword) "Las contrasenas no coinciden" else null
        )
        _uiState.value = validado

        val hayErrores = listOf(
            validado.usernameError,
            validado.nombreError,
            validado.passwordError,
            validado.confirmError
        ).any { it != null }
        if (hayErrores) return

        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repo.registrar(
                username = validado.username,
                nombreCompleto = validado.nombreCompleto,
                password = validado.password,
                rol = validado.rol
            )
            when (resultado) {
                is RegistroResultado.Exito -> {
                    _uiState.update { it.copy(guardando = false, registrado = true) }
                    onOk()
                }
                is RegistroResultado.Error -> {
                    _uiState.update { it.copy(guardando = false, errorGeneral = resultado.mensaje) }
                }
            }
        }
    }

    class Factory(private val repo: UsuarioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(repo) as T
        }
    }
}
