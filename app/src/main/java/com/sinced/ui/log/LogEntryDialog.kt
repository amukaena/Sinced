package com.sinced.ui.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinced.domain.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogEntryDialog(
    initialMillis: Long = DateUtil.todayMillis(),
    initialNote: String = "",
    title: String = "기록 추가",
    onDismiss: () -> Unit,
    onConfirm: (millis: Long, note: String?) -> Unit
) {
    var millis by remember { mutableLongStateOf(initialMillis) }
    var note by remember { mutableStateOf(initialNote) }
    var showPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("날짜: ${DateUtil.formatDate(millis)}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { millis = DateUtil.todayMillis() }, label = { Text("오늘") })
                    AssistChip(onClick = { millis = DateUtil.addDays(DateUtil.todayMillis(), -1) }, label = { Text("어제") })
                    AssistChip(onClick = { millis = DateUtil.addDays(DateUtil.todayMillis(), -2) }, label = { Text("그저께") })
                }
                TextButton(onClick = { showPicker = true }) {
                    Text("날짜 선택")
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("메모 (선택)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(millis, note.ifBlank { null }) }) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )

    if (showPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = millis,
            selectableDates = object : androidx.compose.material3.SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= DateUtil.todayMillis() + 24L * 60 * 60 * 1000
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis = it }
                    showPicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
