package com.example.gramakhata.repository

import com.example.gramakhata.data.*
import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val dao: LedgerDao) {

    // 👤 CUSTOMERS
    fun getCustomers(): Flow<List<Customer>> = dao.getCustomers()

    suspend fun addCustomer(c: Customer) = dao.insertCustomer(c)

    suspend fun updateCustomer(c: Customer) = dao.updateCustomer(c)

    suspend fun deleteCustomer(c: Customer) = dao.deleteCustomer(c)

    // 💰 TRANSACTION + BALANCE UPDATE
    suspend fun addTransaction(t: Transaction) {
        dao.insertTransaction(t)
        dao.updateBalance(t.customerId, t.amount)
    }

    // 🔥 TODAY TRANSACTIONS (FIXED NAME)
    fun getTodayTransactions(start: Long, end: Long): Flow<List<Transaction>> {
        return dao.getTodayTransactions(start, end)
    }

    // 🔥 ALL TRANSACTIONS (THIS WAS CAUSING ERROR)
    fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions()
    }

    // 📜 CUSTOMER HISTORY
    fun getTransactions(customerId: Int): Flow<List<Transaction>> {
        return dao.getTransactions(customerId)
    }
}