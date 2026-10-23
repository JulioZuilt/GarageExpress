package com.example.garageexpress.data.repository

import com.example.garageexpress.data.local.UsuarioDao
import com.example.garageexpress.data.local.UsuarioEntity

// Resultado del registro de un usuario
sealed class RegistroResultado {
    object Exito : RegistroResultado()
    data class Error(val mensaje: String) : RegistroResultado()
}
class UsuarioRepository(private val dao: UsuarioDao) {

    // se crea las cuentas por defecto la primera vez que se abre la app
    suspend fun sembrarUsuariosPorDefecto() {
        if (dao.contar() == 0) {
            dao.insertar(UsuarioEntity("admin", "Administrador", "123", "admin"))
            dao.insertar(UsuarioEntity("asistente", "Asistente", "123", "asistente"))
        }
    }

    suspend fun login(username: String, password: String): UsuarioEntity? {
        return dao.login(username.trim(), password)
    }

    // aqui se egistra un nuevo usuario validando que no exista
    suspend fun registrar(
        username: String,
        nombreCompleto: String,
        password: String,
        rol: String
    ): RegistroResultado {
        val existe = dao.obtenerPorUsername(username.trim())
        if (existe != null) {
            return RegistroResultado.Error("El usuario ya existe")
        }
        dao.insertar(
            UsuarioEntity(
                username = username.trim(),
                nombreCompleto = nombreCompleto.trim(),
                password = password,
                rol = rol
            )
        )
        return RegistroResultado.Exito
    }
}
