package com.baek.untitledproject.di

import com.baek.untitledproject.data.repository.BoardRepositoryImpl
import com.baek.untitledproject.data.repository.MyRecruitsRepositoryImpl
import com.baek.untitledproject.domain.repository.BoardRepository
import com.baek.untitledproject.domain.repository.MyRecruitsRepository
import com.baek.untitledproject.domain.repository.ApplicantRepository
import com.baek.untitledproject.data.repository.ApplicantRepositoryImpl
import com.baek.untitledproject.data.repository.ApplyRepositoryImpl
import com.baek.untitledproject.data.repository.EmailVerifyRepositoryImpl
import com.baek.untitledproject.data.repository.NotificationRepositoryImpl
import com.baek.untitledproject.data.repository.UserRepositoryImpl
import com.baek.untitledproject.domain.repository.ApplyRepository
import com.baek.untitledproject.domain.repository.EmailVerifyRepository
import com.baek.untitledproject.domain.repository.NotificationRepository
import com.baek.untitledproject.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindApplyRepository(
        applyRepositoryImpl: ApplyRepositoryImpl
    ): ApplyRepository

    @Binds
    @Singleton
    abstract fun bindEmailVerifyRepository(
        emailVerifyRepositoryImpl: EmailVerifyRepositoryImpl
    ): EmailVerifyRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

}