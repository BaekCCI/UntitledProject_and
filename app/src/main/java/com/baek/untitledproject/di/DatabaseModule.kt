package com.baek.untitledproject.di

import android.content.Context
import androidx.room.Room
import com.baek.untitledproject.data.local.dao.UserDao
import com.baek.untitledproject.data.local.database.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        ).build()
    }
    @Provides
    @Singleton
    fun provideUserDao(db: UserDatabase): UserDao = db.userDao()
}
