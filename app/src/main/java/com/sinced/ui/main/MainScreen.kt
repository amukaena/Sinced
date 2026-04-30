package com.sinced.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinced.R
import com.sinced.SincedApplication
import com.sinced.domain.filter.CycleFilter
import com.sinced.ui.common.SincedViewModelFactory
import com.sinced.ui.main.components.FilterBar
import com.sinced.ui.main.components.ItemCard
import com.sinced.ui.main.components.SortMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddItem: () -> Unit,
    onItemClick: (Long) -> Unit,
    onManageCategories: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val app = LocalContext.current.applicationContext as SincedApplication
    val viewModel: MainViewModel = viewModel(factory = SincedViewModelFactory(app))
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onManageCategories) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "카테고리 관리")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItem) {
                Icon(Icons.Default.Add, contentDescription = "항목 추가")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            FilterBar(
                cycle = state.filter.cycle,
                onCycleChange = { viewModel.setCycleFilter(it) },
                categories = state.categories,
                selectedCategoryIds = state.filter.categoryIds,
                onToggleCategory = { viewModel.toggleCategory(it) },
                onClearCategories = { viewModel.clearCategoryFilter() }
            )
            SortMenu(
                sort = state.filter.sort,
                onChange = { viewModel.setSort(it) }
            )

            if (state.items.isEmpty() && !state.isLoading) {
                val isUnfiltered = state.filter.categoryIds.isEmpty() &&
                    state.filter.cycle == CycleFilter.ALL
                EmptyState(
                    text = if (isUnfiltered) "첫 항목을 추가해보세요"
                    else "조건에 맞는 항목이 없습니다"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        val category = state.categories.firstOrNull { it.id == item.categoryId }
                        ItemCard(
                            item = item,
                            category = category,
                            onClick = { onItemClick(item.id) },
                            onLongPress = {
                                viewModel.quickLog(item.id)
                                scope.launch {
                                    val result = snackbarHost.showSnackbar(
                                        message = "${item.name} 기록 추가",
                                        actionLabel = "취소",
                                        withDismissAction = true
                                    )
                                    // No-op: undo for quick log not yet supported.
                                    if (result == SnackbarResult.ActionPerformed) Unit
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
