package com.zacariasthequimo.flowcash.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val type: String, // "DESPESA" or "RECEITA"
    val amount: Double,
    val date: Long, // milliseconds
    val description: String
)
