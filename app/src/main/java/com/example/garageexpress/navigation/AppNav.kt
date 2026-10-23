package com.example.garageexpress.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.garageexpress.ui.login.LoginScreen
import com.example.garageexpress.ui.login.LoginViewModel
import com.example.garageexpress.ui.login.RegisterScreen
import com.example.garageexpress.ui.login.RegisterViewModel
import com.example.garageexpress.ui.producto.ProductoFormScreen
import com.example.garageexpress.ui.producto.ListaProductosScreen
import com.example.garageexpress.ui.producto.ProductoViewModel
import com.example.garageexpress.ui.producto.MovimientoScreen
import com.example.garageexpress.ui.producto.MovimientoViewModel
import com.example.garageexpress.ui.producto.QrScannerScreen

@Composable
fun AppNav(
    productoVmFactory: ProductoViewModel.Factory,
    movimientoVmFactory: MovimientoViewModel.Factory,
    loginVmFactory: LoginViewModel.Factory,
    registerVmFactory: RegisterViewModel.Factory
) {
    val navController = rememberNavController()

    val productoVm: ProductoViewModel = viewModel(factory = productoVmFactory)

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController = navController, vmFactory = loginVmFactory)
        }

        composable("registro") {
            RegisterScreen(navController = navController, vmFactory = registerVmFactory)
        }

        composable(
            route = "productos/{username}/{rol}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val rol = backStackEntry.arguments?.getString("rol") ?: "asistente"
            ListaProductosScreen(
                navController = navController,
                vm = productoVm,
                username = username,
                rol = rol
            )
        }

        composable(
            route = "form/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productoId") ?: 0
            ProductoFormScreen(navController = navController, vm = productoVm, productoId = id)
        }

        composable("qr") {
            QrScannerScreen(navController = navController, vm = productoVm)
        }

        composable(
            route = "movimiento/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productoId") ?: 0
            val movimientoVm: MovimientoViewModel = viewModel(factory = movimientoVmFactory)
            MovimientoScreen(navController = navController, vm = movimientoVm, productoId = id)
        }
    }
}
