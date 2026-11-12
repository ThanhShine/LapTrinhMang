package com.example.laptrinhmang.feature.approval.di

import com.example.laptrinhmang.feature.approval.data.remote.ApprovalApiService
import com.example.laptrinhmang.feature.approval.data.repository.ApprovalRepositoryImpl
import com.example.laptrinhmang.feature.approval.domain.repository.ApprovalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApprovalBindingModule {

    @Binds
    @Singleton
    abstract fun bindApprovalRepository(
        impl: ApprovalRepositoryImpl
    ): ApprovalRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ApprovalNetworkModule {

    @Provides
    @Singleton
    fun provideApprovalApiService(retrofit: Retrofit): ApprovalApiService {
        return retrofit.create(ApprovalApiService::class.java)
    }
}
