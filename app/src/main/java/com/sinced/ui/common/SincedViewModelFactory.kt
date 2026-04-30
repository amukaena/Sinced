package com.sinced.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.sinced.SincedApplication
import com.sinced.ui.category.CategoryViewModel
import com.sinced.ui.itemdetail.ItemDetailViewModel
import com.sinced.ui.itemedit.ItemEditViewModel
import com.sinced.ui.main.MainViewModel

class SincedViewModelFactory(
    private val app: SincedApplication,
    private val args: Args = Args.None
) : ViewModelProvider.Factory {

    sealed class Args {
        data object None : Args()
        data class ItemEdit(val itemId: Long?) : Args()
        data class ItemDetail(val itemId: Long) : Args()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                itemRepository = app.itemRepository,
                categoryRepository = app.categoryRepository,
                logRepository = app.logRepository,
                userPreferences = app.userPreferences
            ) as T

            modelClass.isAssignableFrom(ItemEditViewModel::class.java) -> {
                val argsTyped = args as Args.ItemEdit
                ItemEditViewModel(
                    itemRepository = app.itemRepository,
                    categoryRepository = app.categoryRepository,
                    itemId = argsTyped.itemId
                ) as T
            }

            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> {
                val argsTyped = args as Args.ItemDetail
                ItemDetailViewModel(
                    itemRepository = app.itemRepository,
                    logRepository = app.logRepository,
                    categoryRepository = app.categoryRepository,
                    itemId = argsTyped.itemId
                ) as T
            }

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(
                repository = app.categoryRepository
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}
