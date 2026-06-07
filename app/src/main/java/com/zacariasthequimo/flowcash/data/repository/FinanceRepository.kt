package com.zacariasthequimo.flowcash.data.repository

import com.zacariasthequimo.flowcash.data.dao.GoalDao
import com.zacariasthequimo.flowcash.data.dao.TransactionDao
import com.zacariasthequimo.flowcash.data.entity.Goal
import com.zacariasthequimo.flowcash.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }
}
