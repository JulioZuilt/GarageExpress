package com.example.garageexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.garageexpress.data.local.AppDatabase
import com.example.garageexpress.data.repository.ProductoRepository
import com.example.garageexpress.data.repository.UsuarioRepository
import com.example.garageexpress.navigation.AppNav
import com.example.garageexpress.ui.login.LoginViewModel
import com.example.garageexpress.ui.login.RegisterViewModel
import com.example.garageexpress.ui.producto.ProductoViewModel
import com.example.garageexpress.ui.producto.MovimientoViewModel
import com.example.garageexpress.ui.theme.GarageExpressTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.obtenerBase(applicationContext)
        val productoRepo = ProductoRepository(db.productoDao())
        val usuarioRepo = UsuarioRepository(db.usuarioDao())
        val productoVmFactory = ProductoViewModel.Factory(productoRepo)
        val movimientoVmFactory = MovimientoViewModel.Factory(productoRepo)
        val loginVmFactory = LoginViewModel.Factory(usuarioRepo)
        val registerVmFactory = RegisterViewModel.Factory(usuarioRepo)

        setContent {
            GarageExpressTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav(
                        productoVmFactory = productoVmFactory,
                        movimientoVmFactory = movimientoVmFactory,
                        loginVmFactory = loginVmFactory,
                        registerVmFactory = registerVmFactory
                    )
                }
            }
        }
    }
}
