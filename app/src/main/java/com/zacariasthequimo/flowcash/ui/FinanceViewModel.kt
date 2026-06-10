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
import com.zacariasthequimo.flowcash.notification.NotificationHelper
import com.zacariasthequimo.flowcash.notification.NotificationReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class FinanceViewModel(application: Application) : AndroidViewModel(application) {

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

    // Onboarding state
    val isOnboardingComplete: Boolean
        get() = sharedPrefs.getBoolean("onboarding_complete", false)

    fun setOnboardingComplete() {
        sharedPrefs.edit().putBoolean("onboarding_complete", true).apply()
    }

    // Profile photo
    private val _profilePhotoPath = MutableStateFlow(sharedPrefs.getString("profile_photo", null))
    val profilePhotoPath: StateFlow<String?> = _profilePhotoPath.asStateFlow()

    fun setProfilePhotoPath(path: String) {
        sharedPrefs.edit().putString("profile_photo", path).apply()
        _profilePhotoPath.value = path
    }

    // PIN lock
    private val _isPinEnabled = MutableStateFlow(sharedPrefs.getBoolean("pin_enabled", false))
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private var _pinHash = sharedPrefs.getString("pin_hash", "") ?: ""

    fun setPinEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("pin_enabled", enabled).apply()
        _isPinEnabled.value = enabled
    }

    fun setPinHash(pin: String) {
        val hash = pin.hashCode().toString()
        _pinHash = hash
        sharedPrefs.edit().putString("pin_hash", hash).apply()
    }

    fun verifyPin(pin: String): Boolean {
        return _pinHash == pin.hashCode().toString()
    }

    // Notifications
    private val _notificationsEnabled = MutableStateFlow(sharedPrefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init { scheduleNotificationsIfEnabled() }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _notificationsEnabled.value = enabled
        val context = getApplication<Application>()
        if (enabled) {
            NotificationReceiver.scheduleDailyReminder(context)
            NotificationReceiver.scheduleWeeklySummary(context)
            NotificationReceiver.scheduleMonthlySummary(context)
        } else {
            NotificationReceiver.cancelAll(context)
        }
    }

    private fun scheduleNotificationsIfEnabled() {
        if (_notificationsEnabled.value) {
            val context = getApplication<Application>()
            NotificationReceiver.scheduleDailyReminder(context)
            NotificationReceiver.scheduleWeeklySummary(context)
            NotificationReceiver.scheduleMonthlySummary(context)
        }
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
            if (_notificationsEnabled.value) {
                NotificationHelper.showTransactionAlert(
                    getApplication(),
                    title, category, amount, type == "DESPESA"
                )
            }
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
                val newAmount = currentGoal.currentAmount + amount
                repository.insertGoal(
                    currentGoal.copy(currentAmount = newAmount)
                )
                repository.insertTransaction(
                    Transaction(
                        title = "Depósito: ${currentGoal.title}",
                        category = "Outros",
                        type = "DESPESA",
                        amount = amount,
                        date = System.currentTimeMillis(),
                        description = "Depósito para poupança - ${currentGoal.title}"
                    )
                )
                if (_notificationsEnabled.value && currentGoal.targetAmount > 0) {
                    val progress = newAmount / currentGoal.targetAmount * 100
                    if (progress >= 90 && progress < 100) {
                        NotificationHelper.showGoalAlert(
                            getApplication(),
                            currentGoal.title,
                            progress,
                            currentGoal.targetAmount - newAmount
                        )
                    }
                }
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
