package com.example.gramakhata.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {

    // 👤 GET ALL CUSTOMERS
    @Query("SELECT * FROM Customer ORDER BY name ASC")
    fun getCustomers(): Flow<List<Customer>>

    // ➕ ADD CUSTOMER
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    // ✏️ UPDATE CUSTOMER
    @Update
    suspend fun updateCustomer(customer: Customer)

    // 🗑 DELETE CUSTOMER
    @Delete
    suspend fun deleteCustomer(customer: Customer)

    // 💰 ADD TRANSACTION
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    // 🔄 UPDATE BALANCE
    @Query("UPDATE Customer SET balance = balance + :amount WHERE id = :customerId")
    suspend fun updateBalance(customerId: Int, amount: Double)

    // 🔥 TODAY TRANSACTIONS (USED IN REPORT + HOME)
    @Query("""
        SELECT * FROM `Transaction`
        WHERE time BETWEEN :start AND :end
        ORDER BY time DESC
    """)
    fun getTodayTransactions(start: Long, end: Long): Flow<List<Transaction>>

    // 🔥 ALL TRANSACTIONS (USED IN ANALYTICS)
    @Query("SELECT * FROM `Transaction` ORDER BY time DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // 📜 CUSTOMER HISTORY
    @Query("""
        SELECT * FROM `Transaction`
        WHERE customerId = :id
        ORDER BY time DESC
    """)
    fun getTransactions(id: Int): Flow<List<Transaction>>
}