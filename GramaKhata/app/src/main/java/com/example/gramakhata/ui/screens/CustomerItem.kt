package com.example.gramakhata.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gramakhata.data.Customer
import com.example.gramakhata.viewmodel.LedgerViewModel

@Composable
fun CustomerItem(
    customer: Customer,
    vm: LedgerViewModel,
    navController: NavController
) {

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF2F7)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                navController.navigate("detail/${customer.id}")
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row {
                Image(
                    painter = rememberAsyncImagePainter(customer.photoUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(customer.name)
                    Text("₹${customer.balance}")
                }
            }
        }
    }
}