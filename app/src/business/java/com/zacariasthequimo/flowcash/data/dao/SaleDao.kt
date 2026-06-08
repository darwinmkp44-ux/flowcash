package com.zacariasthequimo.flowcash.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zacariasthequimo.flowcash.data.entity.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): Sale?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: Sale): Long

    @Delete
    suspend fun delete(sale: Sale)

    @Query("SELECT * FROM sales WHERE date >= :start AND date <= :end ORDER BY date DESC")
    fun getSalesInRange(start: Long, end: Long): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE customerId = :customerId ORDER BY date DESC")
    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>>

    @Query("SELECT SUM(total) FROM sales WHERE date >= :start AND date <= :end")
    suspend fun getTotalRevenueInRange(start: Long, end: Long): Double?

    @Query("SELECT SUM(total) FROM sales WHERE date >= :start")
    suspend fun getTotalRevenueSince(start: Long): Double?

    @Query("SELECT SUM(subtotal) FROM sales WHERE date >= :start AND date <= :end")
    suspend fun getTotalSubtotalInRange(start: Long, end: Long): Double?

    @Query("SELECT COUNT(*) FROM sales WHERE date >= :start AND date <= :end")
    suspend fun getSalesCountInRange(start: Long, end: Long): Int

    @Query("SELECT paymentMethod, SUM(total) AS total FROM sales WHERE date >= :start AND date <= :end GROUP BY paymentMethod")
    suspend fun getRevenueByPaymentMethod(start: Long, end: Long): List<PaymentMethodRevenue>
}

data class PaymentMethodRevenue(
    val paymentMethod: String,
    val total: Double
)
