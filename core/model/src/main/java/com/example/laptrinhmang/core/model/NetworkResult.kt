package com.example.laptrinhmang.core.model

import com.google.gson.annotations.SerializedName

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val apiError: ApiError) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

data class ApiError(
    val statusCode: Int,
    val code: String? = null,
    val message: String,
    val errors: Map<String, String>? = null
) {
    fun isUnauthorized(): Boolean = statusCode == 401
    fun isForbidden(): Boolean = statusCode == 403
    fun isConflict(): Boolean = statusCode == 409
}

data class ApiResponse<T>(
    val status: Int,
    val message: String? = null,
    val data: T? = null,
    val code: String? = null,
    val errors: Map<String, String>? = null
)

data class PaginatedData<T>(
    val items: List<T>,
    val pagination: PaginationInfo
)

data class PaginationInfo(
    val page: Int,
    val limit: Int,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int
)
