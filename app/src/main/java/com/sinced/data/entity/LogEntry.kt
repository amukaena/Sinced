package com.sinced.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "log_entries",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId"), Index("performedAt")]
)
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val performedAt: Long,
    val note: String? = null,
    val createdAt: Long
)
