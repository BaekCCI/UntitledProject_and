package com.baek.untitledproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val name: String,
    val birthYear: Int,
    val gender: String,
    val department: String,
    val studentId: String,
    val createdAt: Long, //epochMillis
    val updatedAt: Long
)