package com.zacariasthequimo.flowcash.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long? = null,
    val customerName: String = "",
    val amount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val description: String = "",
    val dueDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val paid: Boolean = false
) {
    val remaining: Double get() = amount - paidAmount
}
