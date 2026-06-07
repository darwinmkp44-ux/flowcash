package com.zacariasthequimo.flowcash.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zacariasthequimo.flowcash.data.entity.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY paid ASC, dueDate ASC")
    fun getAllDebts(): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtById(id: Long): Debt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt): Long

    @Update
    suspend fun update(debt: Debt)

    @Delete
    suspend fun delete(debt: Debt)

    @Query("SELECT * FROM debts WHERE paid = 0")
    fun getPendingDebts(): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE customerId = :customerId")
    fun getDebtsByCustomer(customerId: Long): Flow<List<Debt>>

    @Query("SELECT SUM(amount - paidAmount) FROM debts WHERE paid = 0")
    suspend fun getTotalPendingAmount(): Double?
}
