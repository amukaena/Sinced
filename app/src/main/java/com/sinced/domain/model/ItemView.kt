package com.sinced.domain.model

data class ItemView(
    val id: Long,
    val name: String,
    val categoryId: Long?,
    val cycleDays: Int?,
    val memo: String?,
    val sortOrder: Int,
    val createdAt: Long,
    val lastPerformedAt: Long?,
    val daysSince: Long?,
    val nextDueAt: Long?,
    val daysUntilDue: Long?,
    val status: ItemStatus
)
