package com.example.garageexpress.data.repository

import com.example.garageexpress.data.local.Proveedor

class ProveedorRepository {
    fun obtenerProveedores(): List<Proveedor> {
        return listOf(
            Proveedor("P001", "Repuestos Bosch Chile", "contacto@bosch-repuestos.cl", "+56912345678", "Filtros y Bujías"),
            Proveedor("P002", "Lubricantes y Aditivos S.A.", "ventas@lubricantes-sa.cl", "+56987654321", "Aceites y Fluidos"),
            Proveedor("P003", "Frenos y Suspensión Brembo", "pedidos@brembo-cl.com", "+56955554433", "Frenos y Pastillas"),
            Proveedor("P004", "Distribuidora General Automotriz", "contacto@distribuidora-auto.cl", "+56999887766", "General / Varios")
        )
    }

    fun buscarProveedorPorCategoria(categoria: String): Proveedor {
        val proveedores = obtenerProveedores()
        return proveedores.find { it.categoriaSuministro.contains(categoria, ignoreCase = true) }
            ?: proveedores.last()
    }
}
