package com.example.gramakhata.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gramakhata.data.Customer
import com.example.gramakhata.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerDetailScreen(
    customer: Customer,
    vm: LedgerViewModel
) {

    val context = LocalContext.current

    var showAmountDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isGive by remember { mutableStateOf(true) }

    val transactions by vm.getTransactions(customer.id)
        .collectAsState(initial = emptyList())

    val message =
        "Namaskara ${customer.name}, your due at Grama-Khata is ₹${customer.balance}. Please clear it soon."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // PROFILE
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = rememberAsyncImagePainter(customer.photoUri),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(customer.name, style = MaterialTheme.typography.titleLarge)
                Text(customer.phone)
            }
        }

        Spacer(Modifier.height(20.dp))

        // TOTAL CARD
        Card {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Total Due")

                Text(
                    "₹${customer.balance}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(12.dp))

                Row {

                    // WHATSAPP
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val uri = Uri.parse(
                                "https://wa.me/${customer.phone}?text=${Uri.encode(message)}"
                            )
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                    ) {
                        Text("WhatsApp")
                    }

                    Spacer(Modifier.width(10.dp))

                    // SMS
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${customer.phone}")
                                putExtra("sms_body", message)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Text("SMS")
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // GIVE / TAKE
        Row {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    isGive = true
                    showAmountDialog = true
                }
            ) {
                Text("GIVE")
            }

            Spacer(Modifier.width(10.dp))

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    isGive = false
                    showAmountDialog = true
                }
            ) {
                Text("TAKE")
            }
        }

        Spacer(Modifier.height(20.dp))

        // EDIT + DELETE
        Row {

            Button(
                modifier = Modifier.weight(1f),
                onClick = { showEditDialog = true }
            ) {
                Text("Edit")
            }

            Spacer(Modifier.width(10.dp))

            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    vm.deleteCustomer(customer)
                }
            ) {
                Text("Delete")
            }
        }

        Spacer(Modifier.height(24.dp))

        // HISTORY
        Text("History", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(10.dp))

        transactions.forEach { t ->

            // ✅ CORRECT LOGIC
            val isGiveTransaction = t.type == "GIVE"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGiveTransaction)
                        Color.Red.copy(alpha = 0.1f)   // GIVE = RED
                    else
                        Color.Green.copy(alpha = 0.1f) // TAKE = GREEN
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = if (isGiveTransaction)
                            "-₹${t.amount}"   // GIVE = -
                        else
                            "+₹${t.amount}",  // TAKE = +
                        color = if (isGiveTransaction)
                            Color.Red
                        else
                            Color(0xFF2E7D32) // dark green
                    )

                    if (t.note.isNotEmpty()) {
                        Text(t.note)
                    }

                    Text(
                        text = SimpleDateFormat(
                            "dd MMM, hh:mm a",
                            Locale.getDefault()
                        ).format(Date(t.time)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    // AMOUNT DIALOG
    if (showAmountDialog) {
        AmountDialog(
            onConfirm = { amount, note ->
                val value = if (isGive) amount else -amount
                vm.addAmount(customer.id, value, note)
                showAmountDialog = false
            },
            onDismiss = { showAmountDialog = false }
        )
    }

    // EDIT DIALOG
    if (showEditDialog) {
        EditCustomerDialog(
            customer = customer,
            onUpdate = { name, phone, photo ->
                vm.updateCustomer(customer, name, phone, photo)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}