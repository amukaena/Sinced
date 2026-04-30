package com.sinced.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinced.data.entity.Item
import com.sinced.data.relation.ItemWithLastLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query(
        """
        SELECT i.id AS id,
               i.name AS name,
               i.categoryId AS categoryId,
               i.cycleDays AS cycleDays,
               i.memo AS memo,
               i.sortOrder AS sortOrder,
               i.createdAt AS createdAt,
               i.archivedAt AS archivedAt,
               MAX(l.performedAt) AS lastPerformedAt
        FROM items i
        LEFT JOIN log_entries l ON l.itemId = i.id
        WHERE i.archivedAt IS NULL
        GROUP BY i.id
        """
    )
    fun observeItemsWithLastLog(): Flow<List<ItemWithLastLog>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Item?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE items SET archivedAt = :archivedAt WHERE id = :id")
    suspend fun setArchived(id: Long, archivedAt: Long?)
}
