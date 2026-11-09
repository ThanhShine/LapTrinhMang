package com.example.laptrinhmang.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SessionEvent {
    data object Unauthorized : SessionEvent // 401 -> logout & login
    data class Forbidden(val message: String) : SessionEvent // 403 -> no permission
    data class Conflict(val message: String) : SessionEvent // 409 -> schedule conflict / data modified
}

@Singleton
class SessionManager @Inject constructor() {
    private val _sessionEvents = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 10)
    val sessionEvents = _sessionEvents.asSharedFlow()

    fun emitEvent(event: SessionEvent) {
        _sessionEvents.tryEmit(event)
    }
}
