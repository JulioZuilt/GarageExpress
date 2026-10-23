package com.example.garageexpress.data.repository

import com.example.garageexpress.data.local.ProductoDao
import com.example.garageexpress.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val dao: ProductoDao) {
    val productos: Flow<List<ProductoEntity>> = dao.obtenerProductos()
    suspend fun obtenerPorId(id: Int): ProductoEntity? = dao.obtenerPorId(id)
    suspend fun obtenerPorCodigo(codigo: String): ProductoEntity? = dao.obtenerPorCodigo(codigo)
    suspend fun insertar(producto: ProductoEntity) = dao.insertar(producto)
    suspend fun actualizar(producto: ProductoEntity) = dao.actualizar(producto)
    suspend fun eliminar(producto: ProductoEntity) = dao.eliminar(producto)
}
