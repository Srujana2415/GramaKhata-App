package com.example.gramakhata.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.gramakhata.data.Transaction
import com.example.gramakhata.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// ---------------- DATA MODEL ----------------
data class DayData(
    val day: String,
    val give: Double,
    val take: Double
)

@Composable
fun AnalyticsScreen(vm: LedgerViewModel) {

    val transactions by vm.getAllTransactions()
        .collectAsState(initial = emptyList())

    val customers by vm.customers.collectAsState()

    val chartData = remember(transactions) {
        getLast7DaysData(transactions)
    }

    val totalGive = transactions
        .filter { it.type == "GIVE" }
        .sumOf { it.amount }

    val totalTake = transactions
        .filter { it.type == "TAKE" }
        .sumOf { abs(it.amount) }

    val topDebtors = customers
        .filter { it.balance > 0.0 }
        .sortedByDescending { it.balance }
        .take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // ✅ IMPORTANT
            .padding(16.dp)
    ) {

        Text("Analytics", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        // 🔵 CASH FLOW
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("CASH FLOW BALANCE")

                Spacer(Modifier.height(16.dp))

                CashFlowChart(totalGive, totalTake)

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Legend(Color.Red, "Credit Given")
                    Spacer(Modifier.width(16.dp))
                    Legend(Color(0xFF2ECC71), "Payment Received")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🔵 LAST 7 DAYS
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("LAST 7 DAYS TREND")

                Spacer(Modifier.height(16.dp))

                Chart(chartData)
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🔵 TOP DEBTORS (FIXED)
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("TOP DEBTORS")

                Spacer(Modifier.height(12.dp))

                if (topDebtors.isEmpty()) {
                    Text("No debtors")
                } else {

                    val maxDue = topDebtors.maxOfOrNull { it.balance } ?: 1.0

                    topDebtors.forEach { customer ->

                        val ratio = (customer.balance / maxDue).toFloat()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(customer.name)
                                Text("₹${customer.balance}", color = Color.Red)
                            }

                            Spacer(Modifier.height(4.dp))

                            // Background bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                            ) {

                                // Actual value bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)   // ✅ FIX
                                        .height(6.dp)
                                        .background(Color.Red, RoundedCornerShape(4.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//////////////////////////////////////////////////
// 🔴 DONUT CHART
//////////////////////////////////////////////////

@Composable
fun CashFlowChart(give: Double, take: Double) {

    val total = give + take
    val giveAngle = if (total == 0.0) 0f else ((give / total) * 360f).toFloat()
    val takeAngle = 360f - giveAngle

    Box(contentAlignment = Alignment.Center) {

        Canvas(modifier = Modifier.size(150.dp)) {

            drawArc(
                color = Color.Red,
                startAngle = 0f,
                sweepAngle = giveAngle,
                useCenter = false,
                style = Stroke(width = 30f)
            )

            drawArc(
                color = Color(0xFF2ECC71),
                startAngle = giveAngle,
                sweepAngle = takeAngle,
                useCenter = false,
                style = Stroke(width = 30f)
            )
        }
    }
}

//////////////////////////////////////////////////
// 🔵 LEGEND
//////////////////////////////////////////////////

@Composable
fun Legend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(text)
    }
}

//////////////////////////////////////////////////
// 📊 BAR CHART
//////////////////////////////////////////////////

@Composable
fun Chart(data: List<DayData>) {

    val maxValue = (data.maxOfOrNull {
        maxOf(it.give, it.take)
    } ?: 1.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {

        data.forEach { day ->

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height((day.give / maxValue * 120).dp)
                            .background(Color.Red, RoundedCornerShape(4.dp))
                    )

                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height((day.take / maxValue * 120).dp)
                            .background(Color(0xFF2ECC71), RoundedCornerShape(4.dp))
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(day.day)
            }
        }
    }
}

//////////////////////////////////////////////////
// 📅 LAST 7 DAYS LOGIC
//////////////////////////////////////////////////

fun getLast7DaysData(transactions: List<Transaction>): List<DayData> {

    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())

    val result = mutableListOf<DayData>()

    for (i in 6 downTo 0) {

        val cal = calendar.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, -i)

        val start = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = cal.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        val dayTx = transactions.filter { it.time in start..end }

        val give = dayTx.filter { it.type == "GIVE" }.sumOf { it.amount }
        val take = dayTx.filter { it.type == "TAKE" }.sumOf { abs(it.amount) }

        result.add(
            DayData(
                day = sdf.format(cal.time),
                give = give,
                take = take
            )
        )
    }

    return result
}