package com.example.gramakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gramakhata.viewmodel.LedgerViewModel

@Composable
fun CustomerScreen(
    vm: LedgerViewModel,
    navController: NavController
) {

    val customers by vm.customers.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    val filtered = customers.filter {
        it.name.contains(search, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            Text(
                text = "Customers",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search customer") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(10.dp))

            LazyColumn {
                items(filtered) { customer ->
                    CustomerItem(
                        customer = customer,
                        vm = vm,
                        navController = navController
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(
            onAdd = { name, phone, photo ->
                vm.addCustomer(name, phone, photo)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}