package com.example.laptrinhmang.feature.booking.di

import com.example.laptrinhmang.feature.booking.data.BookingApi
import com.example.laptrinhmang.feature.booking.data.BookingRepository
import com.example.laptrinhmang.feature.booking.data.BookingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository

    companion object {
        @Provides
        @Singleton
        fun provideBookingApi(retrofit: Retrofit): BookingApi {
            return retrofit.create(BookingApi::class.java)
        }
    }
}

