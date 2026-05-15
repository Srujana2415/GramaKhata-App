package com.example.gramakhata.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddCustomerDialog(
    onAdd: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 🔥 FIXED IMAGE PICKER (PERMANENT)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onAdd(name, phone, imageUri?.toString())
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Customer") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable {
                            launcher.launch(arrayOf("image/*"))
                        }
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("+", color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
            }
        }
    )
}