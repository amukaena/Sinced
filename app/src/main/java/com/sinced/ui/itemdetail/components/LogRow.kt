package com.sinced.ui.itemdetail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinced.data.entity.LogEntry
import com.sinced.domain.util.DateUtil

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogRow(
    entry: LogEntry,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onTap, onLongClick = onLongPress)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = DateUtil.formatDate(entry.performedAt),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        val noteText = entry.note?.takeIf { it.isNotBlank() } ?: "- (메모 없음)"
        Text(
            text = noteText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
