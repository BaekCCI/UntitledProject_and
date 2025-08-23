package com.baek.untitledproject.data.local.database

import androidx.room.Database
import com.baek.untitledproject.data.local.dao.UserDao
import com.baek.untitledproject.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 0)
abstract class UserDatabase {
    abstract fun userDao(): UserDao
}