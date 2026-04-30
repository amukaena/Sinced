package com.sinced.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sinced.ui.category.CategoryManageScreen
import com.sinced.ui.itemdetail.ItemDetailScreen
import com.sinced.ui.itemedit.ItemEditScreen
import com.sinced.ui.main.MainScreen
import com.sinced.ui.settings.SettingsScreen

object SincedRoutes {
    const val MAIN = "main"
    const val ITEM_NEW = "item/new"
    const val ITEM_EDIT = "item/edit/{itemId}"
    const val ITEM_DETAIL = "item/detail/{itemId}"
    const val CATEGORY_MANAGE = "category"
    const val SETTINGS = "settings"

    fun itemEdit(itemId: Long) = "item/edit/$itemId"
    fun itemDetail(itemId: Long) = "item/detail/$itemId"
}

@Composable
fun SincedNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = SincedRoutes.MAIN) {

        composable(SincedRoutes.MAIN) {
            MainScreen(
                onAddItem = { navController.navigate(SincedRoutes.ITEM_NEW) },
                onItemClick = { id -> navController.navigate(SincedRoutes.itemDetail(id)) },
                onManageCategories = { navController.navigate(SincedRoutes.CATEGORY_MANAGE) },
                onOpenSettings = { navController.navigate(SincedRoutes.SETTINGS) }
            )
        }

        composable(SincedRoutes.ITEM_NEW) {
            ItemEditScreen(
                itemId = null,
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = SincedRoutes.ITEM_EDIT,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId")
            ItemEditScreen(
                itemId = itemId,
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = SincedRoutes.ITEM_DETAIL,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(SincedRoutes.itemEdit(itemId)) },
                onDeleted = { navController.popBackStack() }
            )
        }

        composable(SincedRoutes.CATEGORY_MANAGE) {
            CategoryManageScreen(onBack = { navController.popBackStack() })
        }

        composable(SincedRoutes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
