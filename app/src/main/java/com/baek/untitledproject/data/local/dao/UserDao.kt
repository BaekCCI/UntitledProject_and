package com.baek.untitledproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baek.untitledproject.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_table LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("SELECT * FROM user_table LIMIT 1")
    fun observeSingle(): Flow<UserEntity?>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllData()

}