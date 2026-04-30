package com.sinced.ui.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinced.SincedApplication
import com.sinced.data.entity.LogEntry
import com.sinced.domain.util.DateUtil
import com.sinced.domain.util.ItemFormatting
import com.sinced.ui.common.SincedViewModelFactory
import com.sinced.ui.itemdetail.components.LogRow
import com.sinced.ui.log.LogEntryDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val app = LocalContext.current.applicationContext as SincedApplication
    val viewModel: ItemDetailViewModel = viewModel(
        factory = SincedViewModelFactory(app, SincedViewModelFactory.Args.ItemDetail(itemId))
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.deleted) {
        if (state.deleted) onDeleted()
    }

    var showAddLog by remember { mutableStateOf(false) }
    var editingLog by remember { mutableStateOf<LogEntry?>(null) }
    var deletingLog by remember { mutableStateOf<LogEntry?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val item = state.item
    val view = state.view

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "편집")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("보관") },
                            onClick = {
                                menuExpanded = false
                                viewModel.archive()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("영구 삭제") },
                            onClick = {
                                menuExpanded = false
                                showDeleteConfirm = true
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddLog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("기록") }
            )
        }
    ) { padding ->
        if (item == null || view == null) {
            // Loading or item not found
            return@Scaffold
        }
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = ItemFormatting.formatBadge(view),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                ItemFormatting.formatLastLogLine(view)?.let {
                    Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                view.nextDueAt?.let {
                    Text("다음 예정: ${DateUtil.formatDate(it)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item.cycleDays?.let {
                    Text("주기: ${it}일", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.category?.let {
                    Text("카테고리: ${it.name}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item.memo?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            HorizontalDivider()
            Text(
                text = "이력 (${state.logs.size}건)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
            if (state.logs.isEmpty()) {
                Text(
                    "아직 기록이 없습니다.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.logs, key = { it.id }) { entry ->
                        LogRow(
                            entry = entry,
                            onTap = { editingLog = entry },
                            onLongPress = { deletingLog = entry }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showAddLog) {
        LogEntryDialog(
            onDismiss = { showAddLog = false },
            onConfirm = { millis, note ->
                viewModel.addLog(millis, note)
                showAddLog = false
            }
        )
    }

    editingLog?.let { entry ->
        LogEntryDialog(
            initialMillis = entry.performedAt,
            initialNote = entry.note.orEmpty(),
            title = "기록 편집",
            onDismiss = { editingLog = null },
            onConfirm = { millis, note ->
                viewModel.updateLog(entry.id, millis, note)
                editingLog = null
            }
        )
    }

    deletingLog?.let { entry ->
        AlertDialog(
            onDismissRequest = { deletingLog = null },
            title = { Text("기록 삭제") },
            text = { Text("${DateUtil.formatDate(entry.performedAt)} 기록을 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLog(entry)
                    deletingLog = null
                }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { deletingLog = null }) { Text("취소") }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("항목을 삭제할까요?") },
            text = { Text("이력 ${state.logs.size}건이 함께 삭제됩니다. 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deletePermanently()
                }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("취소") }
            }
        )
    }
}
