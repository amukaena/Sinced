package com.sinced.ui.itemedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinced.SincedApplication
import com.sinced.domain.util.DateUtil
import com.sinced.ui.common.SincedViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    itemId: Long?,
    onClose: () -> Unit
) {
    val app = LocalContext.current.applicationContext as SincedApplication
    val viewModel: ItemEditViewModel = viewModel(
        factory = SincedViewModelFactory(app, SincedViewModelFactory.Args.ItemEdit(itemId))
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onClose()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNew) "항목 추가" else "항목 편집") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "닫기")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.save() },
                        enabled = !state.saving
                    ) { Text("저장") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("이름") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("카테고리")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.categoryId == null,
                    onClick = { viewModel.setCategory(null) },
                    label = { Text("미분류") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { cat ->
                    FilterChip(
                        selected = state.categoryId == cat.id,
                        onClick = { viewModel.setCategory(cat.id) },
                        label = { Text(cat.name) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.cycleEnabled,
                    onCheckedChange = viewModel::setCycleEnabled
                )
                Text("주기 설정 (D-day 표시)")
            }
            if (state.cycleEnabled) {
                OutlinedTextField(
                    value = state.cycleDays,
                    onValueChange = viewModel::setCycleDays,
                    label = { Text("주기 (일)") },
                    isError = state.cycleError != null,
                    supportingText = state.cycleError?.let { { Text(it) } },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.memo,
                onValueChange = viewModel::setMemo,
                label = { Text("메모 (선택)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            if (state.isNew) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.firstLogEnabled,
                        onCheckedChange = viewModel::setFirstLogEnabled
                    )
                    Text("첫 기록도 함께 등록")
                }
                if (state.firstLogEnabled) {
                    FirstLogDatePicker(
                        millis = state.firstLogAtMillis,
                        onChange = viewModel::setFirstLogDate
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FirstLogDatePicker(millis: Long, onChange: (Long) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    TextButton(onClick = { showPicker = true }) {
        Text("날짜: ${DateUtil.formatDate(millis)}")
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
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
                    pickerState.selectedDateMillis?.let { onChange(it) }
                    showPicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
