package com.example.gramakhata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val photoUri: String?,
    val balance: Double = 0.0
)