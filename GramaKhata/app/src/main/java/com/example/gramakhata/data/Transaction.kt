package com.example.gramakhata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val note: String,
    val time: Long,
    val type: String
)