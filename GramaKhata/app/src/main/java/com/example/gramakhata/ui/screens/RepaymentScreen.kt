package com.example.gramakhata.ui.screens

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gramakhata.data.Customer
import com.example.gramakhata.data.Transaction
import com.example.gramakhata.viewmodel.LedgerViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun RepaymentScreen(vm: LedgerViewModel) {

    val context = LocalContext.current

    val transactions by vm.getTodayTransactions()
        .collectAsState(initial = emptyList())

    val customers by vm.customers.collectAsState()

    val creditGiven = transactions
        .filter { it.type == "GIVE" }
        .sumOf { it.amount }

    val paymentsReceived = transactions
        .filter { it.type == "TAKE" }
        .sumOf { abs(it.amount) }

    val netChange = creditGiven - paymentsReceived

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Daily Report", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Card {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Credit Given Today: ₹$creditGiven")
                Text("Payments Received Today: ₹$paymentsReceived")

                Spacer(Modifier.height(10.dp))

                Divider()

                Spacer(Modifier.height(10.dp))

                Text("Net Change: ₹$netChange")
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🔥 DOWNLOAD BUTTON
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                generatePdf(
                    context,
                    transactions,
                    customers,
                    creditGiven,
                    paymentsReceived,
                    netChange
                )
            }
        ) {
            Text("Download Report")
        }
    }
}

//////////////////////////////////////////////////////////////////
// 🔥 PDF GENERATION FUNCTION (DON’T REMOVE)
//////////////////////////////////////////////////////////////////

fun generatePdf(
    context: Context,
    transactions: List<Transaction>,
    customers: List<Customer>,
    credit: Double,
    paid: Double,
    net: Double
) {
    try {
        val pdf = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        var y = 40
        paint.textSize = 14f

        // 🔥 HEADER
        canvas.drawText("GramaKhata Daily Report", 40f, y.toFloat(), paint)
        y += 30

        canvas.drawText("Credit Given: ₹$credit", 40f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Payments Received: ₹$paid", 40f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Net Change: ₹$net", 40f, y.toFloat(), paint)
        y += 30

        canvas.drawText("Transactions:", 40f, y.toFloat(), paint)
        y += 25

        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

        // 🔥 TRANSACTION LIST
        transactions.forEach { t ->

            val name = customers.find { it.id == t.customerId }?.name ?: "Unknown"

            val sign = if (t.type == "GIVE") "+₹" else "-₹"
            val amountText = "$sign${abs(t.amount)}"

            canvas.drawText("$name  $amountText", 40f, y.toFloat(), paint)
            y += 18

            if (t.note.isNotEmpty()) {
                canvas.drawText("Note: ${t.note}", 40f, y.toFloat(), paint)
                y += 18
            }

            val date = formatter.format(Date(t.time))
            canvas.drawText(date, 40f, y.toFloat(), paint)
            y += 25

            // Prevent overflow
            if (y > 800) {
                y = 40
            }
        }

        pdf.finishPage(page)

        // 🔥 SAVE FILE
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "GramaKhata_${System.currentTimeMillis()}.pdf"
        )

        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        Toast.makeText(context, "PDF Saved:\n${file.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}