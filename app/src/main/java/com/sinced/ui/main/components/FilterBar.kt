package com.sinced.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinced.data.entity.Category
import com.sinced.domain.filter.CycleFilter

@Composable
fun FilterBar(
    cycle: CycleFilter,
    onCycleChange: (CycleFilter) -> Unit,
    categories: List<Category>,
    selectedCategoryIds: Set<Long?>,
    onToggleCategory: (Long?) -> Unit,
    onClearCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = cycle == CycleFilter.ALL,
            onClick = { onCycleChange(CycleFilter.ALL) },
            label = { Text("전체") }
        )
        FilterChip(
            selected = cycle == CycleFilter.WITH_CYCLE,
            onClick = { onCycleChange(CycleFilter.WITH_CYCLE) },
            label = { Text("D-day") }
        )
        FilterChip(
            selected = cycle == CycleFilter.WITHOUT_CYCLE,
            onClick = { onCycleChange(CycleFilter.WITHOUT_CYCLE) },
            label = { Text("경과일") }
        )

        CategoryFilterDropdown(
            categories = categories,
            selectedIds = selectedCategoryIds,
            onToggle = onToggleCategory,
            onClear = onClearCategories
        )
    }
}

@Composable
private fun CategoryFilterDropdown(
    categories: List<Category>,
    selectedIds: Set<Long?>,
    onToggle: (Long?) -> Unit,
    onClear: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = if (selectedIds.isEmpty()) "카테고리" else "카테고리 (${selectedIds.size})"

    FilterChip(
        selected = selectedIds.isNotEmpty(),
        onClick = { expanded = true },
        label = { Text(label) },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
    )

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text(if (selectedIds.isEmpty()) "전체 (선택됨)" else "전체") },
            onClick = {
                onClear()
                expanded = false
            }
        )
        categories.forEach { cat ->
            val checked = selectedIds.contains(cat.id)
            DropdownMenuItem(
                text = { Text(if (checked) "✓ ${cat.name}" else cat.name) },
                onClick = { onToggle(cat.id) }
            )
        }
        val uncategorizedChecked = selectedIds.contains(null)
        DropdownMenuItem(
            text = { Text(if (uncategorizedChecked) "✓ 미분류" else "미분류") },
            onClick = { onToggle(null) }
        )
    }
}
