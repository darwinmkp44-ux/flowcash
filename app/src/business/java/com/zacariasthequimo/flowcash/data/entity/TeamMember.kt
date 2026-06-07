package com.zacariasthequimo.flowcash.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team_members")
data class TeamMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val role: String = "STAFF",
    val permissions: String = "ver_vendas,ver_clientes",
    val createdAt: Long = System.currentTimeMillis()
)
