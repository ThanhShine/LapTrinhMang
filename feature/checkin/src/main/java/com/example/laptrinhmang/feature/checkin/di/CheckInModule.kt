package com.example.laptrinhmang.feature.checkin.di

import com.example.laptrinhmang.feature.checkin.data.CheckInApi
import com.example.laptrinhmang.feature.checkin.data.CheckInRepositoryImpl
import com.example.laptrinhmang.feature.checkin.domain.repository.CheckInRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckInBindingModule {

    @Binds
    @Singleton
    abstract fun bindCheckInRepository(
        impl: CheckInRepositoryImpl
    ): CheckInRepository
}

@Module
@InstallIn(SingletonComponent::class)
object CheckInNetworkModule {

    @Provides
    @Singleton
    fun provideCheckInApi(retrofit: Retrofit): CheckInApi {
        return retrofit.create(CheckInApi::class.java)
    }
}
