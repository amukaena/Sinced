package com.sinced.data.relation

data class ItemWithLastLog(
    val id: Long,
    val name: String,
    val categoryId: Long?,
    val cycleDays: Int?,
    val memo: String?,
    val sortOrder: Int,
    val createdAt: Long,
    val archivedAt: Long?,
    val lastPerformedAt: Long?
)
