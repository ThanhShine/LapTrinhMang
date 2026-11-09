package com.example.laptrinhmang.feature.admin.di

import com.example.laptrinhmang.feature.admin.data.AdminApi
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.AdminRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository

    companion object {
        @Provides
        @Singleton
        fun provideAdminApi(retrofit: Retrofit): AdminApi {
            return retrofit.create(AdminApi::class.java)
        }
    }
}
