package com.example.laptrinhmang.feature.account.di

import com.example.laptrinhmang.feature.account.data.AuthApi
import com.example.laptrinhmang.feature.account.data.AuthRepository
import com.example.laptrinhmang.feature.account.data.AuthRepositoryImpl
import com.example.laptrinhmang.feature.account.data.NotificationApi
import com.example.laptrinhmang.feature.account.data.NotificationRepository
import com.example.laptrinhmang.feature.account.data.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }

        @Provides
        @Singleton
        fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
            return retrofit.create(NotificationApi::class.java)
        }
    }
}
