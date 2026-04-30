package com.sinced.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinced.data.entity.LogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface LogEntryDao {

    @Query("SELECT * FROM log_entries WHERE itemId = :itemId ORDER BY performedAt DESC, id DESC")
    fun observeByItem(itemId: Long): Flow<List<LogEntry>>

    @Query("SELECT * FROM log_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LogEntry?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: LogEntry): Long

    @Update
    suspend fun update(entry: LogEntry)

    @Delete
    suspend fun delete(entry: LogEntry)
}
