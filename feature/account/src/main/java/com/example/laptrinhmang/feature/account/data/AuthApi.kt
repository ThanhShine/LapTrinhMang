package com.example.laptrinhmang.feature.account.data

import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponseData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<OtpResponseData>>

    @POST("auth/activate")
    suspend fun activateAccount(@Body request: OtpVerifyRequest): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password/request")
    suspend fun requestForgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("account/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @PUT("account/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<User>>

    @PUT("account/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>
}
