package com.baek.untitledproject.di

import com.baek.untitledproject.data.repository.BoardRepositoryImpl
import com.baek.untitledproject.data.repository.MyRecruitsRepositoryImpl
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.data.repository.ApplicantRepositoryImpl
import com.baek.untitledproject.data.repository.NotificationRepositoryImpl
import com.baek.untitledproject.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindBoardRepository(
        boardRepositoryImpl: BoardRepositoryImpl
    ): BoardRepository

    @Binds
    @Singleton
    abstract fun bindMyRecruitsRepository(
        myRecruitsRepositoryImpl: MyRecruitsRepositoryImpl
    ): MyRecruitsRepository

    @Binds
    @Singleton
    abstract fun bindApplicantRepository(
        applicantRepositoryImpl: ApplicantRepositoryImpl
    ): ApplicantRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}