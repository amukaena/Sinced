package com.sinced.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinced.SincedApplication
import com.sinced.data.entity.Category
import com.sinced.ui.common.SincedViewModelFactory
import com.sinced.ui.theme.CategoryPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as SincedApplication
    val viewModel: CategoryViewModel = viewModel(factory = SincedViewModelFactory(app))
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showAdd by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<Category?>(null) }
    var deleting by remember { mutableStateOf<Category?>(null) }
    var deletingItemCount by remember { mutableIntStateOf(0) }
    var coloring by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("카테고리 관리") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.categories, key = { it.id }) { cat ->
                    CategoryRow(
                        category = cat,
                        onMoveUp = { viewModel.moveUp(cat) },
                        onMoveDown = { viewModel.moveDown(cat) },
                        onRename = { renaming = cat },
                        onColorClick = { coloring = cat },
                        onDelete = { deleting = cat }
                    )
                    HorizontalDivider()
                }
            }
            Button(
                onClick = { showAdd = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("+ 카테고리 추가")
            }
        }
    }

    if (showAdd) {
        NameInputDialog(
            title = "카테고리 추가",
            initial = "",
            onDismiss = { showAdd = false },
            onConfirm = { name ->
                viewModel.add(name)
                showAdd = false
            }
        )
    }

    renaming?.let { cat ->
        NameInputDialog(
            title = "이름 변경",
            initial = cat.name,
            onDismiss = { renaming = null },
            onConfirm = { name ->
                viewModel.rename(cat, name)
                renaming = null
            }
        )
    }

    coloring?.let { cat ->
        ColorPickerDialog(
            current = cat.color,
            onDismiss = { coloring = null },
            onPick = { color ->
                viewModel.setColor(cat, color)
                coloring = null
            }
        )
    }

    deleting?.let { cat ->
        LaunchedEffect(cat.id) {
            deletingItemCount = viewModel.countItemsInCategory(cat)
        }
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("카테고리 삭제") },
            text = {
                Text(
                    if (deletingItemCount > 0)
                        "이 카테고리의 항목 ${deletingItemCount}개는 '미분류'가 됩니다."
                    else
                        "정말 삭제할까요?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(cat)
                    deleting = null
                }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text("취소") }
            }
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRename: () -> Unit,
    onColorClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMoveUp) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "위로")
        }
        IconButton(onClick = onMoveDown) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "아래로")
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(category.color?.let { Color(it) } ?: MaterialTheme.colorScheme.outline)
                .clickable { onColorClick() }
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = category.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = onRename) {
            Icon(Icons.Default.Edit, contentDescription = "이름 변경")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "삭제")
        }
    }
}

@Composable
private fun NameInputDialog(
    title: String,
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("이름") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }, enabled = value.isNotBlank()) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
private fun ColorPickerDialog(
    current: Int?,
    onDismiss: () -> Unit,
    onPick: (Int?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("색상 선택") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryPalette.presets.take(4).forEach { c ->
                        ColorSwatch(color = c, selected = c == current, onClick = { onPick(c) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryPalette.presets.drop(4).forEach { c ->
                        ColorSwatch(color = c, selected = c == current, onClick = { onPick(c) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { onPick(null) }) { Text("색상 없음") }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("닫기") }
        }
    )
}

@Composable
private fun ColorSwatch(color: Int, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(if (selected) 36.dp else 32.dp)
            .clip(CircleShape)
            .background(Color(color))
            .clickable(onClick = onClick)
    )
}
