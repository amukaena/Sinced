package com.sinced

import android.app.Application
import com.sinced.data.SincedDatabase
import com.sinced.data.repository.CategoryRepository
import com.sinced.data.repository.ItemRepository
import com.sinced.data.repository.LogRepository
import com.sinced.data.seed.DefaultCategories
import com.sinced.prefs.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SincedApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { SincedDatabase.getInstance(this) }

    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val itemRepository by lazy {
        ItemRepository(database.itemDao(), database.logEntryDao())
    }
    val logRepository by lazy { LogRepository(database.logEntryDao()) }
    val userPreferences by lazy { UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            DefaultCategories.seedIfEmpty(database.categoryDao())
        }
    }
}
