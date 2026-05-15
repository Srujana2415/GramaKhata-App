package com.example.gramakhata.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.gramakhata.data.Customer

@Composable
fun EditCustomerDialog(
    customer: Customer,
    onUpdate: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf(customer.name) }
    var phone by remember { mutableStateOf(customer.phone) }
    var photoUri by remember { mutableStateOf(customer.photoUri) }

    // 🔥 FIXED IMAGE PICKER (PERMANENT)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            photoUri = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onUpdate(name, phone, photoUri)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Customer") },
        text = {
            Column {

                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .border(3.dp, Color(0xFF2E4A92), CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            launcher.launch(arrayOf("image/*"))
                        }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
            }
        }
    )
}