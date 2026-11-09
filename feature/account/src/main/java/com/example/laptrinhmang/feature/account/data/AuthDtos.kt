package com.example.laptrinhmang.feature.account.data

import com.example.laptrinhmang.core.model.User
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("password") val password: String
)

data class LoginResponseData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("user") val user: User
)

data class RegisterRequest(
    @SerializedName("user_code") val userCode: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("full_name") val fullName: String
)

data class OtpResponseData(
    @SerializedName("email") val email: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int = 300,
    @SerializedName("resend_in_seconds") val resendInSeconds: Int = 60
)

data class OtpVerifyRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp_code") val otpCode: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp_code") val otpCode: String,
    @SerializedName("new_password") val newPassword: String
)

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class ChangePasswordRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)
