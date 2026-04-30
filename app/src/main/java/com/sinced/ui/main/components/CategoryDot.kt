package com.sinced.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryDot(color: Int?, modifier: Modifier = Modifier) {
    val resolved = color?.let { Color(it) } ?: MaterialTheme.colorScheme.outline
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(resolved)
    )
}
