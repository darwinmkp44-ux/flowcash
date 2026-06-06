package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Segurança Financeira", "Trabalho & Carreira", "Lazer & Sonhos"
    val currentAmount: Double,
    val targetAmount: Double
)
