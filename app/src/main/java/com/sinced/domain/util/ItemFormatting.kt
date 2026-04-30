package com.sinced.domain.util

import com.sinced.domain.model.ItemStatus
import com.sinced.domain.model.ItemView
import kotlin.math.absoluteValue

object ItemFormatting {

    fun formatBadge(view: ItemView): String = when (view.status) {
        ItemStatus.NEVER_LOGGED -> "기록 없음"
        ItemStatus.TRACKING_ONLY -> "${view.daysSince ?: 0L}일 경과"
        ItemStatus.DUE_TODAY -> "D-DAY"
        ItemStatus.OVERDUE -> "D+${view.daysUntilDue!!.absoluteValue}"
        ItemStatus.DUE_SOON, ItemStatus.OK -> "D-${view.daysUntilDue!!}"
    }

    fun formatLastLogLine(view: ItemView): String? {
        val last = view.lastPerformedAt ?: return null
        val days = view.daysSince ?: return null
        val dateStr = DateUtil.formatDate(last)
        return "마지막: ${days}일 전 ($dateStr)"
    }
}
