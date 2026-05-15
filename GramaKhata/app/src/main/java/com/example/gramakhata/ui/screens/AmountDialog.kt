package com.example.gramakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AmountDialog(
    onConfirm: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        amount.toDoubleOrNull() ?: 0.0,
                        note
                    )
                }
            ) {
                Text("OK")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        title = { Text("Enter Amount") },

        text = {
            Column {

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}