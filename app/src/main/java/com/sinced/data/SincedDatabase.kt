package com.sinced.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sinced.data.dao.CategoryDao
import com.sinced.data.dao.ItemDao
import com.sinced.data.dao.LogEntryDao
import com.sinced.data.entity.Category
import com.sinced.data.entity.Item
import com.sinced.data.entity.LogEntry

@Database(
    entities = [Category::class, Item::class, LogEntry::class],
    version = 1,
    exportSchema = false
)
abstract class SincedDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun itemDao(): ItemDao
    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile
        private var INSTANCE: SincedDatabase? = null

        fun getInstance(context: Context): SincedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SincedDatabase::class.java,
                    "sinced.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
