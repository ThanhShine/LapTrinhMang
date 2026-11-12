package com.example.laptrinhmang.feature.approval.domain.model

data class RoomCategory(
    val id: String,
    val name: String,
    val description: String,
    val roomCount: Int = 0
)
