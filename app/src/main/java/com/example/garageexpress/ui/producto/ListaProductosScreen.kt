package com.example.garageexpress.ui.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.garageexpress.data.local.ProductoEntity
import com.example.garageexpress.ui.theme.AmarilloAcento
import com.example.garageexpress.ui.theme.NaranjaStockBajo
import com.example.garageexpress.ui.theme.RojoEliminar

@Composable
fun ListaProductosScreen(
    navController: NavController,
    vm: ProductoViewModel,
    username: String,
    rol: String
) {
    val productos by vm.productos.collectAsState()
    val categorias = vm.categorias
    val busqueda by vm.busqueda.collectAsState()
    val categoriaSel by vm.categoriaFiltro.collectAsState()

    val esAdmin = rol == "admin"
    var productoAEliminar by remember { mutableStateOf<ProductoEntity?>(null) }

    val rolBonito = rol.replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    "Garage Express",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Usuario: $username ($rolBonito)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesion", tint = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = vm::onBusquedaChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Buscar por codigo o nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Categorias:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias) { cat ->
                val seleccionada = cat.equals(categoriaSel, ignoreCase = true)
                FilterChip(
                    selected = seleccionada,
                    onClick = { vm.onCategoriaSeleccionada(cat) },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AmarilloAcento,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (productos.isEmpty()) {
            Text(
                "No hay productos para mostrar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productos, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        esAdmin = esAdmin,
                        onMovimiento = {
                            navController.navigate("movimiento/${producto.id}")
                        },
                        onEditar = {
                            vm.cargarProducto(producto.id)
                            navController.navigate("form/${producto.id}")
                        },
                        onEliminar = { productoAEliminar = producto }
                    )
                }
            }
        }
    }

    if (esAdmin) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = {
                    vm.nuevoProducto()
                    navController.navigate("form/0")
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    }

    if (productoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("Seguro que deseas eliminar \"${productoAEliminar!!.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(productoAEliminar!!)
                    productoAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { productoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ProductoCard(
    producto: ProductoEntity,
    esAdmin: Boolean,
    onMovimiento: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val stockBajo = producto.cantidad <= producto.stockMinimo

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.Top) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    producto.codigo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "Categoria: ${producto.categoria}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Precio: $${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AmarilloAcento,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (stockBajo) {
                    Text(
                        "\u26A0 Stock: ${producto.cantidad} (Min: ${producto.stockMinimo})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NaranjaStockBajo,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Stock: ${producto.cantidad} (Min: ${producto.stockMinimo})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onMovimiento) {
                    Icon(
                        Icons.Default.Science,
                        contentDescription = "Registrar operacion",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (esAdmin) {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RojoEliminar)
                    }
                }
            }
        }
    }
}
