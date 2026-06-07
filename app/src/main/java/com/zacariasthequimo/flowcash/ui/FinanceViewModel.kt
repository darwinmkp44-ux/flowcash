package com.zacariasthequimo.flowcash.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zacariasthequimo.flowcash.data.database.AppDatabase
import com.zacariasthequimo.flowcash.data.entity.Goal
import com.zacariasthequimo.flowcash.data.entity.Transaction
import com.zacariasthequimo.flowcash.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(
        database.transactionDao(),
        database.goalDao()
    )

    // User Profile data via SharedPreferences
    private val sharedPrefs = application.getSharedPreferences("flowcash_prefs", android.content.Context.MODE_PRIVATE)
    private val _userName = MutableStateFlow(sharedPrefs.getString("user_name", "FlowCash User") ?: "FlowCash User")
    val userName: StateFlow<String> = _userName.asStateFlow()
    private val _userEmail = MutableStateFlow(sharedPrefs.getString("user_email", "user@flowcash.com") ?: "user@flowcash.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    fun updateProfile(name: String, email: String) {
        sharedPrefs.edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .apply()
        _userName.value = name
        _userEmail.value = email
    }

    // Toggle for balance visibility (Ocultar/Mostrar saldo)
    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

    // Database states
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Toggle balance visibility function
    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    // Custom flow transformations to calculate sums dynamically
    val totalIncome: StateFlow<Double> = transactions
        .combine(MutableStateFlow(0.0)) { txList, _ ->
            txList.filter { it.type == "RECEITA" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = transactions
        .combine(MutableStateFlow(0.0)) { txList, _ ->
            txList.filter { it.type == "DESPESA" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val poupancaSum: StateFlow<Double> = goals
        .combine(MutableStateFlow(0.0)) { goalList, _ ->
            goalList.sumOf { it.currentAmount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Operations
    fun addTransaction(title: String, category: String, type: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    title = title,
                    category = category,
                    type = type,
                    amount = amount,
                    date = System.currentTimeMillis(),
                    description = description
                )
            )
        }
    }

    fun addGoal(title: String, category: String, targetAmount: Double) {
        viewModelScope.launch {
            repository.insertGoal(
                Goal(
                    title = title,
                    category = category,
                    currentAmount = 0.0,
                    targetAmount = targetAmount
                )
            )
        }
    }

    fun addGoalSavings(goalId: Long, amount: Double) {
        viewModelScope.launch {
            val currentGoal = goals.value.find { it.id == goalId }
            if (currentGoal != null) {
                repository.insertGoal(
                    currentGoal.copy(currentAmount = currentGoal.currentAmount + amount)
                )
                // Also optionally, we could create an automatic transfer/transaction for it, 
                // but let's keep it simple as specified.
            }
        }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}
