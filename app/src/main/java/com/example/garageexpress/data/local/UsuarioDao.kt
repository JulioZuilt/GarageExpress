package com.example.garageexpress.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO de usuarios login, registro y verificacion de duplicados
@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun obtenerPorUsername(username: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity)
}
