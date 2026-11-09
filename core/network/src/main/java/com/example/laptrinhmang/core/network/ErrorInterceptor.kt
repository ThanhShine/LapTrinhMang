package com.example.laptrinhmang.core.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) : Interceptor {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            401 -> {
                scope.launch {
                    tokenManager.clearSession()
                }
                sessionManager.emitEvent(SessionEvent.Unauthorized)
            }
            403 -> {
                sessionManager.emitEvent(SessionEvent.Forbidden("Bạn không có quyền thực hiện thao tác này"))
            }
            409 -> {
                sessionManager.emitEvent(SessionEvent.Conflict("Trùng lịch hoặc dữ liệu đã thay đổi, vui lòng tải lại"))
            }
        }

        return response
    }
}
