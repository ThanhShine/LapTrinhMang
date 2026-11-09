package com.example.laptrinhmang.feature.account.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.network.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUserFlow: Flow<User?>
    suspend fun login(identifier: String, password: String): NetworkResult<User>
    suspend fun register(userCode: String, email: String, password: String, fullName: String): NetworkResult<OtpResponseData>
    suspend fun activateAccount(email: String, otpCode: String): NetworkResult<Unit>
    suspend fun requestForgotPassword(email: String): NetworkResult<Unit>
    suspend fun resetPassword(email: String, otpCode: String, newPassword: String): NetworkResult<Unit>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun getProfile(): NetworkResult<User>
    suspend fun updateProfile(fullName: String, phone: String?, avatarUrl: String?): NetworkResult<User>
    suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : AuthRepository {

    override val currentUserFlow: Flow<User?> = tokenManager.userFlow

    override suspend fun login(identifier: String, password: String): NetworkResult<User> {
        return handleApiCall {
            val response = authApi.login(LoginRequest(identifier, password))
            if (response.isSuccessful && response.body()?.data != null) {
                val loginData = response.body()!!.data!!
                tokenManager.saveTokens(loginData.accessToken, loginData.refreshToken)
                tokenManager.saveUser(loginData.user)
            }
            response
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(result.data.data!!.user)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun register(userCode: String, email: String, password: String, fullName: String): NetworkResult<OtpResponseData> {
        return handleApiCall {
            authApi.register(RegisterRequest(userCode, email, password, fullName))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(result.data.data!!)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun activateAccount(email: String, otpCode: String): NetworkResult<Unit> {
        return handleApiCall {
            authApi.activateAccount(OtpVerifyRequest(email, otpCode))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(Unit)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun requestForgotPassword(email: String): NetworkResult<Unit> {
        return handleApiCall {
            authApi.requestForgotPassword(ForgotPasswordRequest(email))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(Unit)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun resetPassword(email: String, otpCode: String, newPassword: String): NetworkResult<Unit> {
        return handleApiCall {
            authApi.resetPassword(ResetPasswordRequest(email, otpCode, newPassword))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(Unit)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        return try {
            authApi.logout()
            tokenManager.clearSession()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            tokenManager.clearSession()
            NetworkResult.Success(Unit)
        }
    }

    override suspend fun getProfile(): NetworkResult<User> {
        return handleApiCall {
            val response = authApi.getProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                tokenManager.saveUser(response.body()!!.data!!)
            }
            response
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(result.data.data!!)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun updateProfile(fullName: String, phone: String?, avatarUrl: String?): NetworkResult<User> {
        return handleApiCall {
            authApi.updateProfile(UpdateProfileRequest(fullName, phone, avatarUrl))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val user = result.data.data!!
                    tokenManager.saveUser(user)
                    NetworkResult.Success(user)
                }
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit> {
        return handleApiCall {
            authApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
        }.let { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(Unit)
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    private suspend fun <T> handleApiCall(call: suspend () -> Response<ApiResponse<T>>): NetworkResult<ApiResponse<T>> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val apiError = if (!errorBody.isNullOrBlank()) {
                    runCatching { gson.fromJson(errorBody, ApiError::class.java) }
                        .getOrDefault(ApiError(response.code(), message = "Lỗi hệ thống (${response.code()})"))
                } else {
                    ApiError(response.code(), message = "Lỗi hệ thống (${response.code()})")
                }
                NetworkResult.Error(apiError)
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối mạng"))
        }
    }
}
