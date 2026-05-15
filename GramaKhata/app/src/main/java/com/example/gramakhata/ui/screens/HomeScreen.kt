package com.example.gramakhata.ui.screens

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gramakhata.viewmodel.LedgerViewModel
import kotlin.math.abs

@Composable
fun HomeScreen(
    vm: LedgerViewModel,
    navController: NavController
) {

    val customers by vm.customers.collectAsState(initial = emptyList())
    val todayTransactions by vm.getTodayTransactions()
        .collectAsState(initial = emptyList())

    val totalPending = customers.sumOf { it.balance }

    val todayCredit = todayTransactions
        .filter { it.type == "GIVE" }
        .sumOf { it.amount }

    val todayPaid = todayTransactions
        .filter { it.type == "TAKE" }
        .sumOf { abs(it.amount) }

    val highRiskCustomers = customers
        .filter { it.balance > 0 }
        .sortedByDescending { it.balance }
        .take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // HEADER
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E4A92)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", color = Color.White)
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text("Grama-Khata", color = Color.White)
                    Text("DAILY LEDGER", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // BALANCE
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E4A92)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("TOTAL PENDING BALANCE", color = Color.White.copy(alpha = 0.7f))

                Spacer(Modifier.height(10.dp))

                Text("₹$totalPending", color = Color.White)

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("TODAY CREDIT", color = Color.White.copy(alpha = 0.7f))
                        Text("₹$todayCredit", color = Color.White)
                    }

                    Column {
                        Text("TODAY PAID", color = Color.White.copy(alpha = 0.7f))
                        Text("₹$todayPaid", color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("HIGH DUE RISK")

            Text(
                text = "View All",
                color = Color(0xFF2E4A92),
                modifier = Modifier.clickable {
                    navController.navigate("customers")
                }
            )
        }

        Spacer(Modifier.height(10.dp))

        if (highRiskCustomers.isEmpty()) {
            Text("No high risk customers")
        } else {
            highRiskCustomers.forEach { customer ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // ✅ IMAGE FIX
                        if (customer.photoUri != null) {
                            AsyncImage(
                                model = customer.photoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.LightGray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(customer.name.first().toString())
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(customer.name)
                            Text("Last active: Today", color = Color.Gray)
                        }

                        Text("₹${customer.balance}")
                    }
                }
            }
        }
    }
}