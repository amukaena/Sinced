package com.sinced.ui.main.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinced.data.entity.Category
import com.sinced.domain.model.ItemStatus
import com.sinced.domain.model.ItemView
import com.sinced.domain.util.ItemFormatting
import com.sinced.ui.theme.StatusDueSoon
import com.sinced.ui.theme.StatusDueToday
import com.sinced.ui.theme.StatusNeverLogged
import com.sinced.ui.theme.StatusOk
import com.sinced.ui.theme.StatusOverdue

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    item: ItemView,
    category: Category?,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (category != null) {
                        CategoryDot(color = category.color)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(4.dp))
                val subtitle = buildSubtitle(item, category)
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ItemFormatting.formatLastLogLine(item)?.let { line ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = ItemFormatting.formatBadge(item),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor(item.status)
            )
        }
    }
}

private fun buildSubtitle(item: ItemView, category: Category?): String {
    val parts = mutableListOf<String>()
    if (category != null) parts.add(category.name)
    item.cycleDays?.let { parts.add("${it}일 주기") }
    return parts.joinToString(" · ")
}

private fun badgeColor(status: ItemStatus): Color = when (status) {
    ItemStatus.OVERDUE -> StatusOverdue
    ItemStatus.DUE_TODAY -> StatusDueToday
    ItemStatus.DUE_SOON -> StatusDueSoon
    ItemStatus.OK, ItemStatus.TRACKING_ONLY -> StatusOk
    ItemStatus.NEVER_LOGGED -> StatusNeverLogged
}
