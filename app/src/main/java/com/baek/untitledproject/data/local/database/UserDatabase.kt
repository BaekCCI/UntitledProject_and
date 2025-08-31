package com.baek.untitledproject.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.baek.untitledproject.data.local.dao.UserDao
import com.baek.untitledproject.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}