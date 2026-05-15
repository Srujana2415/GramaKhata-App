package com.example.gramakhata.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.gramakhata.ui.screens.*
import com.example.gramakhata.viewmodel.LedgerViewModel

@Composable
fun MainScreen(vm: LedgerViewModel) {

    val navController = rememberNavController()

    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    label = { Text("Home") },
                    icon = { Text("🏠") }
                )

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "customers" } == true,
                    onClick = {
                        navController.navigate("customers") {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    label = { Text("Customers") },
                    icon = { Text("👤") }
                )

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "repayments" } == true,
                    onClick = {
                        navController.navigate("repayments") {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    label = { Text("Reports") },
                    icon = { Text("📊") }
                )

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "analytics" } == true,
                    onClick = {
                        navController.navigate("analytics") {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    label = { Text("Analytics") },
                    icon = { Text("📈") }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(vm, navController)
            }

            // ✅ FIXED HERE (IMPORTANT)
            composable("customers") {
                CustomerScreen(vm, navController)
            }

            composable("repayments") {
                RepaymentScreen(vm)
            }

            composable("analytics") {
                AnalyticsScreen(vm)
            }

            // ✅ DETAIL SCREEN NAVIGATION
            composable("detail/{id}") { backStackEntry ->

                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toIntOrNull() ?: 0

                val customers by vm.customers.collectAsState()

                val customer = customers.find { it.id == id }

                customer?.let {
                    CustomerDetailScreen(it, vm)
                }
            }
        }
    }
}