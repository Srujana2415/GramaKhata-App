package com.example.gramakhata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramakhata.data.*
import com.example.gramakhata.repository.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class LedgerViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: LedgerRepository

    val customers: StateFlow<List<Customer>>

    init {
        val db = AppDatabase.get(app)
        repo = LedgerRepository(db.dao())

        customers = repo.getCustomers()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    // 🔹 ADD CUSTOMER
    fun addCustomer(name: String, phone: String, photo: String?) {
        viewModelScope.launch {
            repo.addCustomer(
                Customer(
                    name = name,
                    phone = phone,
                    photoUri = photo,
                    balance = 0.0
                )
            )
        }
    }

    // 🔹 UPDATE CUSTOMER (DIRECT)
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repo.updateCustomer(customer)
        }
    }

    // 🔹 UPDATE CUSTOMER (FIELDS)
    fun updateCustomer(customer: Customer, name: String, phone: String, photo: String?) {
        viewModelScope.launch {
            repo.updateCustomer(
                customer.copy(
                    name = name,
                    phone = phone,
                    photoUri = photo
                )
            )
        }
    }

    // 🔹 DELETE CUSTOMER
    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repo.deleteCustomer(customer)
        }
    }

    // 🔥 ADD TRANSACTION (WITH NOTE + TIME)
    fun addAmount(customerId: Int, amount: Double, note: String) {
        viewModelScope.launch {

            repo.addTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    note = note,
                    time = System.currentTimeMillis(),
                    type = if (amount > 0) "GIVE" else "TAKE"
                )
            )
        }
    }

    // 🔥 TODAY TRANSACTIONS
    fun getTodayTransactions(): Flow<List<Transaction>> {

        val calendar = Calendar.getInstance()

        // START OF DAY
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // END OF DAY
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis

        return repo.getTodayTransactions(start, end)
    }

    // 🔥 ALL TRANSACTIONS (FOR ANALYTICS)
    fun getAllTransactions(): Flow<List<Transaction>> {
        return repo.getAllTransactions()
    }

    // 🔥 CUSTOMER HISTORY
    fun getTransactions(customerId: Int): Flow<List<Transaction>> {
        return repo.getTransactions(customerId)
    }

    // 🔥 OPTIONAL (FOR HOME / ANALYTICS USE)
    fun getTotalPending(): Double {
        return customers.value.sumOf { it.balance }
    }

    fun getTotalCreditGiven(): Double {
        return customers.value.sumOf { if (it.balance > 0) it.balance else 0.0 }
    }

    fun getTotalPaid(): Double {
        return customers.value.sumOf { if (it.balance < 0) -it.balance else 0.0 }
    }
}