package com.sinced.domain.util

import com.sinced.data.relation.ItemWithLastLog
import com.sinced.domain.model.ItemStatus
import com.sinced.domain.model.ItemView

object ItemDeriver {

    fun derive(row: ItemWithLastLog, todayMillis: Long): ItemView {
        val last = row.lastPerformedAt
        val cycle = row.cycleDays

        val daysSince: Long? = last?.let { DateUtil.daysBetween(it, todayMillis) }

        val nextDueAt: Long? = if (last != null && cycle != null && cycle > 0) {
            DateUtil.addDays(last, cycle)
        } else null

        val daysUntilDue: Long? = nextDueAt?.let { DateUtil.daysBetween(todayMillis, it) }

        val status = when {
            last == null -> ItemStatus.NEVER_LOGGED
            cycle == null -> ItemStatus.TRACKING_ONLY
            daysUntilDue == null -> ItemStatus.TRACKING_ONLY
            daysUntilDue < 0L -> ItemStatus.OVERDUE
            daysUntilDue == 0L -> ItemStatus.DUE_TODAY
            daysUntilDue <= 3L -> ItemStatus.DUE_SOON
            else -> ItemStatus.OK
        }

        return ItemView(
            id = row.id,
            name = row.name,
            categoryId = row.categoryId,
            cycleDays = cycle,
            memo = row.memo,
            sortOrder = row.sortOrder,
            createdAt = row.createdAt,
            lastPerformedAt = last,
            daysSince = daysSince,
            nextDueAt = nextDueAt,
            daysUntilDue = daysUntilDue,
            status = status
        )
    }

    fun deriveAll(rows: List<ItemWithLastLog>, todayMillis: Long): List<ItemView> =
        rows.map { derive(it, todayMillis) }
}
