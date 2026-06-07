package com.zacariasthequimo.flowcash.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long? = null,
    val customerName: String = "",
    val itemsJson: String = "[]",
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "Dinheiro",
    val date: Long = System.currentTimeMillis(),
    val status: String = "CONCLUIDO"
)
